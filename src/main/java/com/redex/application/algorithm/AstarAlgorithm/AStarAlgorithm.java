package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.algorithm.util.ApplicationContextHolder;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.TransportPlan;
import com.redex.application.core.model.business.TransportPlanNode;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.FlightCargoRepository;
import com.redex.application.core.repository.business.FlightRepository;
import com.redex.application.core.service.business.FlightCargoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AStarAlgorithm{

    @Autowired
    private FlightCargoService flightCargoService;

    private Graph worldMap;
    private RouteFinder routeFinder;
    public Graph getWorldMap() {
        return worldMap;
    }
    public RouteFinder getRouteFinder() {
        return routeFinder;
    }

    /*
     * Algorithm have two stages: setUpVersionWC and findRouteWC.
     *
     * In setUpVersionWC, a graph of routes is created from node to node, using flights as connections.
     * Each node have a list of connections.
     * Flights are filtered in util flights using restrictions about shipments:
     *   a continental shipment have a 23 hour of deadline
     *   an intercontinental shipment have a 47 hour of deadline
     *   time between flights have to be at minimum one hour.
     *
     * In findRouteWC, a route is found using A* algorithm.
     * weight of routes is detailed in ConnectScorer class.
     *
     */

    public void setUpVersionWC(Shipment shipment, List<Airport> airportList,   List<FlightCargo> flightCargos){  //date?
        List<Node> nodes = new ArrayList<>();
        Map<Long, List<Node>> connections = new HashMap<>();

        final Long continent = shipment.getOrigin().getContinent().getId();

        //creating filters for lambda using restrictions about shipments
        Predicate<FlightCargo> rangeStart = flightCargo -> !flightCargo.getStartDateTime().isBefore(shipment.getRegistrationDatetime());
        Predicate<FlightCargo> rangeEnd = flightCargo -> !flightCargo.getEndDateTime().isAfter(shipment.getLimitDatetime());
        Predicate<FlightCargo> minCapacity = flightCargo -> (flightCargo.getCurrentLoad()<flightCargo.getTotalCapacity());
        //filter to get only continental flights
        Predicate<FlightCargo> sameContinent = flightCargo -> flightCargo.getFlight().getAirportDeparture().getContinent().getId() == flightCargo.getFlight().getAirportArrival().getContinent().getId();
        //filter to get flights only in same continent of shipment
        Predicate<FlightCargo> insideContinent = flightCargo -> flightCargo.getFlight().getAirportDeparture().getContinent().getId() == continent;

        Long origin = shipment.getOrigin().getContinent().getId();
        Long destination = shipment.getDestination().getContinent().getId();
        int continentalShipment = (origin == destination)? 1 : 0;

        List<FlightCargo> flightCargosFiltered = flightCargos.stream().filter(rangeStart).filter(rangeEnd).filter(minCapacity).toList();
        if(continentalShipment == 1){
            flightCargosFiltered = flightCargosFiltered.stream().filter(sameContinent).filter(insideContinent).toList();
        }
        List<FlightCargo> flightCargosFilteredByAirport;

        //for each airport, a node is created, and all connections are created using filtered flights
        for (Airport airport: airportList) {
            if(continentalShipment==1){
                if(airport.getContinent().getId() != origin) continue;
            }

            nodes.add(new Node(airport));
            Predicate<FlightCargo> byAirport = flightCargo -> flightCargo.getFlight().getAirportDeparture().getId() == airport.getId();
            flightCargosFilteredByAirport = flightCargosFiltered.stream().filter(byAirport).toList();

            List<Node> destinations = new ArrayList<>();
            for (FlightCargo flightCargo: flightCargosFilteredByAirport) {
                Node node = new Node(flightCargo.getFlight().getAirportArrival());
                node.setFlightCargoOrigin(flightCargo);
                destinations.add(node);
            }
            connections.put(airport.getId(), destinations);

        }

        worldMap = new Graph(nodes, connections);
        routeFinder = new RouteFinder(worldMap, new ConnectScorer(), new ConnectScorer());

    }

    public int findRouteWC(Shipment shipment, int remainder){
        Node origin = worldMap.getNode(shipment.getOrigin().getId());
        Node destination = worldMap.getNode(shipment.getDestination().getId());

        //logic of findRoute is detailed inside function findRoute
        List<Node> route = routeFinder.findRoute(origin, destination, shipment);

        if(route != null){ //if route is found, shipments are charged to flights, if remainder is greater than zero, another route have to be found
            remainder = flightCargoService.updateFlightCargos(shipment, route, remainder);
        }

        //check if route does not exist
        if(route == null){
            System.out.println("Route not found: "+shipment.getCode());
            return -1;
        }

        return remainder;

    }

    public int findRouteByOne(Shipment shipment, TransportPlan transportPlan, int remainder){
        Node origin = worldMap.getNode(shipment.getOrigin().getId());
        Node destination = worldMap.getNode(shipment.getDestination().getId());
        List<Node> route = routeFinder.findRoute(origin, destination, shipment);
        int actual = remainder;
        if(route != null){
            remainder = flightCargoService.updateFlightCargos(shipment, route, remainder);
            transportPlan.setQuantity(actual-remainder);
            for(Node node : route){
                TransportPlanNode transportPlanNode = new TransportPlanNode(node, transportPlan);
                transportPlan.getTransportPlanNodes().add(transportPlanNode);
            }
        }

        //check if route does not exist
        if(route == null){
            System.out.println("Route not found: "+shipment.getCode());
            return -1;
        }

        return remainder;

    }
}
