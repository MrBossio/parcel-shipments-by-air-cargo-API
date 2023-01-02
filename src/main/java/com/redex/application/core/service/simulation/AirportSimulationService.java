package com.redex.application.core.service.simulation;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.model.simulation.AirportSimulation;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.simulation.AirportSimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class AirportSimulationService {
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    AirportSimulationRepository airportSimulationRepository;


    public List<AirportSimulation> createAirportSimulationByTimestamp(List<Airport> airportList, List<AirportSimulation> previousAirportSimulationList, Long simulation, OffsetDateTime timestamp){

        //create a new set of airportSimulations
        List<AirportSimulation> airportSimulationList = new ArrayList<>();
        //check if exists a previous set of simulations
//        List<AirportSimulation> previousAirportSimulationList =
//                airportSimulationRepository.findByTimestampEqualsAndSimulationEqualsOrderById(timestamp.minusHours(1L),simulation);

        for (Airport airport:airportList) {
            AirportSimulation airportSimulation = new AirportSimulation(airport, simulation, 0, timestamp);
            if(previousAirportSimulationList!= null){
                Predicate<AirportSimulation> prev = airportSim -> airportSim.getAirport().getOACI().equals(airport.getOACI());
                AirportSimulation previous = previousAirportSimulationList.stream().filter(prev).findFirst().orElse(null);
                if(previous!= null)
                    airportSimulation.setWarehouseActualCapacity(previous.getWarehouseActualCapacity());
            }
            airportSimulationList.add(airportSimulation);
        }
        airportSimulationRepository.saveAll(airportSimulationList);
        return airportSimulationList;
    }

//    @Async
    public List<AirportSimulation> loadAndEmptyAirport(List<Shipment> shipmentRegistryFiltered,
                                                       List<Shipment> lastHourShipmentRegistryFiltered,
                                                       List<Shipment>  shipmentDeliveryFiltered,
                                    List<FlightCargo> flightCargoDepartureFiltered, List<FlightCargo>flightCargoArrivalFiltered,
                                    List<Airport> airportList, List<AirportSimulation> airportSimulationListOld,
                                    Long simulation, OffsetDateTime endDateTime){

        // create airport data for this hour
        List<AirportSimulation> airportSimulationList = createAirportSimulationByTimestamp(airportList, airportSimulationListOld, simulation, endDateTime);


//        //fill from shipments registered in this hour (first point in route)
//        for (Shipment shipment : shipmentRegistryFiltered) {
//            for (AirportSimulation airportSimulation: airportSimulationList) {
//                if(shipment.getOrigin().getId() == airportSimulation.getAirport().getId()){
//                    airportSimulation.increment(shipment.getTotalQuantity());
//                    break;
//                }
//            }
//        }
//
//        //empty from shipments send from redex office to airplanes
//        for (Shipment shipment : lastHourShipmentRegistryFiltered) {
//            for (AirportSimulation airportSimulation: airportSimulationList) {
//                if(shipment.getOrigin().getId() == airportSimulation.getAirport().getId()){
//                    airportSimulation.decrement(shipment.getTotalQuantity());
//                    break;
//                }
//            }
//        }

//        empty from shipments delivered in this hour (last point, after delivery)
        for (Shipment shipment : shipmentDeliveryFiltered) {
            for (AirportSimulation airportSimulation: airportSimulationList) {
                if(shipment.getDestination().getId() == airportSimulation.getAirport().getId()){
                    airportSimulation.decrement(shipment.getTotalQuantity());
                    break;
                }
            }
        }

        //fill and empty from scale (arrival and departures)
        for (FlightCargo flightcargo: flightCargoDepartureFiltered) {
            for (AirportSimulation airportSimulation: airportSimulationList) {
               if(flightcargo.getFlight().getAirportDeparture().getId() == airportSimulation.getAirport().getId() && flightcargo.getCurrentLoadWithoutFirstPointOnRoute()!=0){
                   airportSimulation.decrement(flightcargo.getCurrentLoadWithoutFirstPointOnRoute());
                   break;
               }
            }
        }
//
        for (FlightCargo flightcargo: flightCargoArrivalFiltered) {
            for (AirportSimulation airportSimulation: airportSimulationList) {
                if(flightcargo.getFlight().getAirportArrival().getId() == airportSimulation.getAirport().getId() && flightcargo.getCurrentLoad()!=0){
                    airportSimulation.increment(flightcargo.getCurrentLoad());
                    break;
                }
            }
        }

        airportSimulationRepository.saveAll(airportSimulationList);

        return airportSimulationList;
    }


}
