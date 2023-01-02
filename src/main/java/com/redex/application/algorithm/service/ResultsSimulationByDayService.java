package com.redex.application.algorithm.service;

import com.redex.application.algorithm.AstarAlgorithm.SimpleRunAStarAlgorithm;
import com.redex.application.algorithm.dto.*;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.simulation.AirportSimulation;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.model.simulation.Simulation;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.FlightCargoRepository;
import com.redex.application.core.repository.business.ShipmentRepository;
import com.redex.application.core.repository.simulation.AirportSimulationRepository;
import com.redex.application.core.repository.simulation.SimulationRepository;
import com.redex.application.core.service.simulation.AirportSimulationService;
import com.redex.application.core.service.simulation.SimulationService;
import com.redex.application.sourcefiles.DynamicLoadFiles;
import com.redex.application.sourcefiles.LoadFlightCargo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Predicate;

@Service
public class ResultsSimulationByDayService {
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private AirportSimulationService airportSimulationService;
    @Autowired
    private LoadFlightCargo loadFlightCargo;
    @Autowired
    private SimpleRunAStarAlgorithm runAStarAlgorithm;
    @Autowired
    private AirportSimulationRepository airportSimulationRepository;
    @Autowired
    FlightCargoRepository flightCargoRepository;
    @Autowired
    DynamicLoadFiles dynamicLoadFiles;
    @Autowired
    SimulationService simulationService;
    @Autowired
    ShipmentRepository shipmentRepository;
    @Autowired
    private SimulationRepository simulationRepository;

    private final Logger log = LoggerFactory.getLogger(ResultsSimulationByDayService.class);

    @Async
    public void calculateSimulation5D(Long epochdatetime, Simulation simulation){
        
        OffsetDateTime first = OffsetDateTime.now();
        Simulation updatedSimulation = simulation; // to get stop signal
        List<Shipment> previousAndActual =new ArrayList<>(2);
        previousAndActual.add(null);
        previousAndActual.add(null);
        int result;

        //get all shipments in range, pre run of two days, post run of zero day
        OffsetDateTime staticStartDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).minusDays(0);
        OffsetDateTime startDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).minusDays(0);
        OffsetDateTime endDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).plusDays(5);
        log.info("Initializing simulation for five days at "+OffsetDateTime.now()+" in the range: "+startDateTime.plusDays(0)+" - "+endDateTime.minusDays(0));
//        log.info("Processing simulation from two days before, one day after range: "+startDateTime+" - "+endDateTime);

        //option 2: shipments to ram
        List<Shipment> shipmentList = dynamicLoadFiles.dynamicLoadShipmentsAlgorithmAStar(epochdatetime);

        List<Airport> airportList = airportRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        //create flightCargos for this simulation
        List<FlightCargo> flightCargoList = loadFlightCargo.generateFlightCargoFromDate(
                startDateTime.minusDays(1), endDateTime, simulation.getId());

        Integer totalRegistered = 0, totalLoadedToPlane = 0, totalDelivered = 0, totalArrived = 0, totalDepartured = 0;
        Integer totalShipments = 0;

        List<AirportSimulation> airportSimulationList = new ArrayList<>();

        //save last hour of shipments
        List<Shipment> lastHourShipmentRegistryFiltered = new ArrayList<>();
        //process shipments for every hour
        while(startDateTime.isBefore(endDateTime)){

            //check if simulation was cancelled
            updatedSimulation = simulationService.getUpdatedSimulationData(simulation.getId());
            if(updatedSimulation != null){
                updatedSimulation.setSimulationStatistics(totalRegistered, totalLoadedToPlane, totalDelivered, totalDepartured, totalArrived, totalShipments);
                simulationRepository.save(updatedSimulation);
                if(updatedSimulation.getStopped() != null){
                    log.warn("Simulation "+updatedSimulation.getId()+" is stopping in datetime "+startDateTime);
                    break;
                }
            }

            //SHIPMENTS
            // from customer to register in agency: one hour of shipments registered, it will ignore
            OffsetDateTime realStartDateTime = startDateTime;
            Predicate<Shipment> shipmentRegistryRange = shipment -> !shipment.getRegistrationDatetime().isBefore(realStartDateTime) &&
                    !shipment.getRegistrationDatetime().isEqual(realStartDateTime.plusHours(1)) &&
                    !shipment.getRegistrationDatetime().isAfter(realStartDateTime.plusHours(1));
            List<Shipment> shipmentRegistryFiltered = new ArrayList<>(shipmentList.stream().filter(shipmentRegistryRange).toList());
            //sorting shipments by least delivery time
            Collections.sort(shipmentRegistryFiltered);

            //call algorithm, fill flights and transportplans
            result = runAStarAlgorithm.runAlgorithmFromBatch(shipmentRegistryFiltered, airportList, flightCargoList, previousAndActual, simulation, airportSimulationList);
            if(result == -1){
                log.warn("FOUND LOGISTIC COLLAPSE IN FLIGHTS (NO ROUTE FOUND), STOPPING SIMULATION AT "+ startDateTime);
                break;
            }
            if(result == -2){
                log.warn("FOUND LOGISTIC COLLAPSE BY AIRPORT (AIRPORT FULL BY SHIPMENT REGISTERED), STOPPING SIMULATION AT "+ startDateTime);
                break;
            }

            // from delivery to customers: one hour of shipments delivered (one hour after arrived at last destination), only after first day of simulation
            Predicate<Shipment> shipmentDeliveryRange = shipment -> !shipment.getRegistrationDatetime().isBefore(staticStartDateTime) &&
                !shipment.getDeliveryDateTime().isBefore(realStartDateTime) &&
                !shipment.getDeliveryDateTime().isEqual(realStartDateTime.plusHours(1)) &&
                !shipment.getDeliveryDateTime().isAfter(realStartDateTime.plusHours(1));
            List<Shipment> shipmentDeliveryFiltered = shipmentList.stream().filter(shipmentDeliveryRange).toList();

            //FLIGHTCARGOS

            //case 1: half hour before departure, shipments will be loaded in flights
            Predicate<FlightCargo> flightDepartureRange = flightcargo -> !flightcargo.getStartDateTime().minusMinutes(30).isBefore(realStartDateTime) &&
                    !flightcargo.getStartDateTime().minusMinutes(30).isEqual(realStartDateTime.plusHours(1)) &&
                    !flightcargo.getStartDateTime().minusMinutes(30).isAfter(realStartDateTime.plusHours(1)) &&
                    flightcargo.getCurrentLoad()>0;
            List<FlightCargo> flightCargoDepartureFiltered = flightCargoList.stream().filter(flightDepartureRange).toList();

            //case 2: half hour after arrival in destination, shipments are translated to airport office
            Predicate<FlightCargo> flightArrivalRange = flightcargo -> !flightcargo.getEndDateTime().plusMinutes(30).isBefore(realStartDateTime) &&
                    !flightcargo.getEndDateTime().plusMinutes(30).isEqual(realStartDateTime.plusHours(1)) &&
                    !flightcargo.getEndDateTime().plusMinutes(30).isAfter(realStartDateTime.plusHours(1)) &&
                    flightcargo.getCurrentLoad()>0;
            List<FlightCargo> flightCargoArrivalFiltered = flightCargoList.stream().filter(flightArrivalRange).toList();

            //keep list of registered flights, after an hour, will be charged to flights and dischargued from airports
            lastHourShipmentRegistryFiltered = shipmentRegistryFiltered;

            airportSimulationList = airportSimulationService.loadAndEmptyAirport(shipmentRegistryFiltered, lastHourShipmentRegistryFiltered, shipmentDeliveryFiltered, flightCargoDepartureFiltered, flightCargoArrivalFiltered, airportList, airportSimulationList , simulation.getId(), startDateTime);

            //save all flights in that hour for request
            flightCargoRepository.saveAll(flightCargoDepartureFiltered);

            //update time
            startDateTime = startDateTime.plusHours(1);

            //count registered processed
            totalShipments += shipmentRegistryFiltered.size();

            //statistics
            for(Shipment shipment : shipmentRegistryFiltered){
                totalRegistered+=shipment.getTotalQuantity();
            }
//            for(Shipment shipment : lastHourShipmentRegistryFiltered){
//                totalLoadedToPlane+=shipment.getTotalQuantity();
//            }
            totalLoadedToPlane = totalRegistered;

            for (Shipment shipment:shipmentDeliveryFiltered) {
                totalDelivered+=shipment.getTotalQuantity();
            }
            for(FlightCargo flightCargo : flightCargoDepartureFiltered){
                totalDepartured+= flightCargo.getCurrentLoadWithoutFirstPointOnRoute();
            }
            for(FlightCargo flightCargo : flightCargoArrivalFiltered){
                totalArrived+= flightCargo.getCurrentLoad();
            }

        }

        log.info("Total \"packages registered in office\" processed: "+totalRegistered);
        log.info("Total \"packages loaded from office to airplanes\" processed: "+totalLoadedToPlane);
        log.info("Total \"packages delivered to client\"  processed: "+totalDelivered);
        log.info("Total \"packages departed in flight scales\" processed: "+totalDepartured);
        log.info("Total \"packages arrived from flights\" processed: "+totalArrived);

        log.info("Total shipments processed: "+totalShipments);

        OffsetDateTime last = OffsetDateTime.now();

        Duration totalTime = Duration.between(first, last);
        log.info("Total time: "+totalTime);

        //save final statistics
        updatedSimulation.setSimulationStatistics(totalRegistered, totalLoadedToPlane,  totalDelivered, totalDepartured, totalArrived, totalShipments);
        simulationRepository.save(updatedSimulation);

        if(previousAndActual.get(0)!= null){
            previousAndActual.get(0).setSimulation(simulation.getId());
            previousAndActual.get(1).setSimulation(simulation.getId());
            shipmentRepository.saveAll(previousAndActual);
        }


    }

    @Async
    public void calculateSimulationEternal(Long epochdatetime, Simulation simulation){

        OffsetDateTime first = OffsetDateTime.now();
        Simulation updatedSimulation = simulation; // to get stop signal
        List<Shipment> previousAndActual =new ArrayList<>(2);
        previousAndActual.add(null);
        previousAndActual.add(null);
        int result;

        //get all shipments in range, pre run of two days, post run of zero day
        OffsetDateTime staticStartDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).minusDays(0);
        OffsetDateTime startDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).minusDays(0);
        OffsetDateTime endDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).plusDays(20);
        log.info("Initializing simulation for five days at "+OffsetDateTime.now()+" in the range: "+startDateTime.plusDays(0)+" - "+endDateTime.minusDays(0));
//        log.info("Processing simulation from two days before, one day after range: "+startDateTime+" - "+endDateTime);

        //option 2: shipments to ram
        List<Shipment> shipmentList = dynamicLoadFiles.dynamicLoadShipmentsAlgorithmAStarEternal(epochdatetime);

        List<Airport> airportList = airportRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        //create flightCargos for this simulation
        List<FlightCargo> flightCargoList = loadFlightCargo.generateFlightCargoFromDate(
                startDateTime.minusDays(1), endDateTime, simulation.getId());

        Integer totalRegistered = 0, totalLoadedToPlane = 0, totalDelivered = 0, totalArrived = 0, totalDepartured = 0;
        Integer totalShipments = 0;

        List<AirportSimulation> airportSimulationList = new ArrayList<>();

        //save last hour of shipments
        List<Shipment> lastHourShipmentRegistryFiltered = new ArrayList<>();
        //process shipments for every hour
        while(startDateTime.isBefore(endDateTime)){

            //check if simulation was cancelled
            updatedSimulation = simulationService.getUpdatedSimulationData(simulation.getId());
            if(updatedSimulation != null){
                updatedSimulation.setSimulationStatistics(totalRegistered, totalLoadedToPlane, totalDelivered, totalDepartured, totalArrived, totalShipments);
                simulationRepository.save(updatedSimulation);
                if(updatedSimulation.getStopped() != null){
                    log.warn("Simulation "+updatedSimulation.getId()+" is stopping in datetime "+startDateTime);
                    break;
                }
            }

            //SHIPMENTS
            // from customer to register in agency: one hour of shipments registered, it will ignore
            OffsetDateTime realStartDateTime = startDateTime;
            Predicate<Shipment> shipmentRegistryRange = shipment -> !shipment.getRegistrationDatetime().isBefore(realStartDateTime) &&
                    !shipment.getRegistrationDatetime().isEqual(realStartDateTime.plusHours(1)) &&
                    !shipment.getRegistrationDatetime().isAfter(realStartDateTime.plusHours(1));
            List<Shipment> shipmentRegistryFiltered = new ArrayList<>(shipmentList.stream().filter(shipmentRegistryRange).toList());
            //sorting shipments by least delivery time
            Collections.sort(shipmentRegistryFiltered);

            //call algorithm, fill flights and transportplans
            result = runAStarAlgorithm.runAlgorithmFromBatch(shipmentRegistryFiltered, airportList, flightCargoList, previousAndActual, simulation, airportSimulationList);
            if(result == -1){
                log.warn("FOUND LOGISTIC COLLAPSE IN FLIGHTS (NO ROUTE FOUND), STOPPING SIMULATION AT "+ startDateTime);
                break;
            }
            if(result == -2){
                log.warn("FOUND LOGISTIC COLLAPSE BY AIRPORT (AIRPORT FULL BY SHIPMENT REGISTERED), STOPPING SIMULATION AT "+ startDateTime);
                break;
            }

            // from delivery to customers: one hour of shipments delivered, only after first day of simulation
            Predicate<Shipment> shipmentDeliveryRange = shipment -> !shipment.getRegistrationDatetime().isBefore(staticStartDateTime) &&
                    !shipment.getDeliveryDateTime().isBefore(realStartDateTime) &&
                    !shipment.getDeliveryDateTime().isEqual(realStartDateTime.plusHours(1)) &&
                    !shipment.getDeliveryDateTime().isAfter(realStartDateTime.plusHours(1));
            List<Shipment> shipmentDeliveryFiltered = shipmentList.stream().filter(shipmentDeliveryRange).toList();

            //FLIGHTCARGOS

            //case 1: half hour before departure, shipments will be loaded in flights
            Predicate<FlightCargo> flightDepartureRange = flightcargo -> !flightcargo.getStartDateTime().minusMinutes(30).isBefore(realStartDateTime) &&
                    !flightcargo.getStartDateTime().minusMinutes(30).isEqual(realStartDateTime.plusHours(1)) &&
                    !flightcargo.getStartDateTime().minusMinutes(30).isAfter(realStartDateTime.plusHours(1)) &&
                    flightcargo.getCurrentLoad()>0;
            List<FlightCargo> flightCargoDepartureFiltered = flightCargoList.stream().filter(flightDepartureRange).toList();

            //case 2: half hour after arrival in destination, shipments are translated to airport office
            Predicate<FlightCargo> flightArrivalRange = flightcargo -> !flightcargo.getEndDateTime().plusMinutes(30).isBefore(realStartDateTime) &&
                    !flightcargo.getEndDateTime().plusMinutes(30).isEqual(realStartDateTime.plusHours(1)) &&
                    !flightcargo.getEndDateTime().plusMinutes(30).isAfter(realStartDateTime.plusHours(1)) &&
                    flightcargo.getCurrentLoad()>0;
            List<FlightCargo> flightCargoArrivalFiltered = flightCargoList.stream().filter(flightArrivalRange).toList();

            //keep list of registered flights, after an hour, will be charged to flights and dischargued from airports
            lastHourShipmentRegistryFiltered = shipmentRegistryFiltered;

            airportSimulationList = airportSimulationService.loadAndEmptyAirport(shipmentRegistryFiltered, lastHourShipmentRegistryFiltered, shipmentDeliveryFiltered, flightCargoDepartureFiltered, flightCargoArrivalFiltered, airportList, airportSimulationList , simulation.getId(), startDateTime);

            //save all flights in that hour for request
            flightCargoRepository.saveAll(flightCargoDepartureFiltered);

            //update time
            startDateTime = startDateTime.plusHours(1);

            //count registered processed
            totalShipments += shipmentRegistryFiltered.size();

            //statistics
            for(Shipment shipment : shipmentRegistryFiltered){
                totalRegistered+=shipment.getTotalQuantity();
            }
//            for(Shipment shipment : lastHourShipmentRegistryFiltered){
//                totalLoadedToPlane+=shipment.getTotalQuantity();
//            }
            totalLoadedToPlane = totalRegistered;

            for (Shipment shipment:shipmentDeliveryFiltered) {
                totalDelivered+=shipment.getTotalQuantity();
            }
            for(FlightCargo flightCargo : flightCargoDepartureFiltered){
                totalDepartured+= flightCargo.getCurrentLoadWithoutFirstPointOnRoute();
            }
            for(FlightCargo flightCargo : flightCargoArrivalFiltered){
                totalArrived+= flightCargo.getCurrentLoad();
            }

        }

        log.info("Total \"packages registered in office\" processed: "+totalRegistered);
        log.info("Total \"packages loaded from office to airplanes\" processed: "+totalLoadedToPlane);
        log.info("Total \"packages delivered to client\"  processed: "+totalDelivered);
        log.info("Total \"packages departed in flight scales\" processed: "+totalDepartured);
        log.info("Total \"packages arrived from flights\" processed: "+totalArrived);

        log.info("Total shipments processed: "+totalShipments);

        OffsetDateTime last = OffsetDateTime.now();

        Duration totalTime = Duration.between(first, last);
        log.info("Total time: "+totalTime);

        //save final statistics
        updatedSimulation.setSimulationStatistics(totalRegistered, totalLoadedToPlane,  totalDelivered, totalDepartured, totalArrived, totalShipments);
        simulationRepository.save(updatedSimulation);

        if(previousAndActual.get(0)!= null){
            previousAndActual.get(0).setSimulation(simulation.getId());
            previousAndActual.get(1).setSimulation(simulation.getId());
            shipmentRepository.saveAll(previousAndActual);
        }

    }

    public List<AirportSimulationListDTO> calculateAirports(Long epochdatetime, Long simulation){

        List<AirportSimulationListDTO> airportSimulationListDTOList = new ArrayList<>();

        //get final time from each hour
        OffsetDateTime timestamp = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).plusHours(1);
        //end at midnight
        OffsetDateTime endDateTime = timestamp.minusHours(1).plusHours(4);
        //by hour
        while(!timestamp.isAfter(endDateTime)){

            //get simulation data from one hour
            List<AirportSimulation> list = airportSimulationRepository.findByTimestampEqualsAndSimulationEqualsOrderById(
                    timestamp, simulation);
            //convert to dto
            List<AirportSimulationDTO> listDTO = new ArrayList<>();
            for (AirportSimulation airportSimulation: list) {
                listDTO.add(new AirportSimulationDTO(airportSimulation));
            }
            //add data from one hour to a DTO envelope and envelope to list of lists
            airportSimulationListDTOList.add(new AirportSimulationListDTO(timestamp, listDTO));

            timestamp = timestamp.plusHours(1);
        }

        return  airportSimulationListDTOList;

    }

    public List<FlightCargoDTO> calculateFlightCargos(Long epochdatetime, Long simulation){

        List<FlightCargoDTO> flightCargoDTOList = new ArrayList<>();

        OffsetDateTime startDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC);
        List<FlightCargo> flightCargoList = flightCargoRepository.findBySimulationEqualsAndStartDateTimeGreaterThanEqualAndStartDateTimeLessThan(
                simulation, startDateTime, startDateTime.plusHours(4)
        );

        for (FlightCargo flightCargo: flightCargoList) {
            flightCargoDTOList.add(new FlightCargoDTO(flightCargo));
        }
        return flightCargoDTOList;
    }

    public List<Shipment> obtainShipmentsFromCollapse(Long simulation){
        List<Shipment> shipmentsFromCollapse = shipmentRepository.findBySimulationEquals(simulation);
        return  shipmentsFromCollapse;
    }
//
//    public Shipment obtainLastShipmentCauseCollapse(){
//
//    }

    public ResultSimulationByDayDTO resultSimulationByDay5D(Long epochdatetime, Long simulation){

        ResultSimulationByDayDTO result = new ResultSimulationByDayDTO(epochdatetime, simulation);
        List<FlightCargoDTO> fc = calculateFlightCargos(epochdatetime, simulation);
        List<AirportSimulationListDTO> as = calculateAirports(epochdatetime, simulation);
        List<Shipment> shipmentsFromCollapse = obtainShipmentsFromCollapse(simulation);

        Simulation updatedSimulation;
        updatedSimulation = simulationService.getUpdatedSimulationData(simulation);
        if(updatedSimulation != null){
            result.setTotal_registered(updatedSimulation.getTotal_registered());
            result.setTotal_loaded(updatedSimulation.getTotal_loaded());
            result.setTotal_delivered(updatedSimulation.getTotal_delivered());
            result.setTotal_departure(updatedSimulation.getTotal_departure());
            result.setTotal_arrival(updatedSimulation.getTotal_arrival());
            result.setTotal_shipments(updatedSimulation.getTotal_shipments());
        }

        result.setFlightCargoDTOList(fc);
        result.setCurrent(as);
        // if collapse
        int collapse = 0;
        for(Shipment shipment : shipmentsFromCollapse){
            if(shipment.isCollapse()){
                result.setLastShipmentCauseCollapse(new LastShipmentDTO(shipment));
                collapse = 1;
            }
            if(shipment.isPrevious()) result.setLastShipmentBeforeCollapse(new LastShipmentDTO(shipment));
        }
        if(collapse == 0 && shipmentsFromCollapse.size() > 0){
            result.setLastShipmentBeforeCollapse(new LastShipmentDTO(shipmentsFromCollapse.get(0)));
            result.setLastShipmentCauseCollapse(new LastShipmentDTO(shipmentsFromCollapse.get(1)));
        }

        return result;
    }

}
