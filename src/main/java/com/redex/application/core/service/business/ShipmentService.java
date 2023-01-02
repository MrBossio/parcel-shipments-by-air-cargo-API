package com.redex.application.core.service.business;

import com.redex.application.core.Utils.FormatStrings;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Person;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.PersonRepository;
import com.redex.application.core.repository.business.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AirportRepository airportRepository;


    public Shipment getOrInsertByString(String line){
        String[] values = FormatStrings.SplitShipmentString(line);

        Optional <Shipment> shipmentOpt = shipmentRepository.findByCode(values[1]);
        Optional<Airport> airportOriginOpt = airportRepository.findByOACI(values[0]);
        Optional<Airport> airportDestinationOpt = airportRepository.findByOACI(values[4]);

        Shipment shipment = null;
        if(shipmentOpt.isPresent()){
            shipment = shipmentOpt.get();
            return shipment;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try{

//            LocalDate shipment_date = LocalDate.parse(values[2], formatter);
//            LocalTime shipment_time = LocalTime.of(
//                    Integer.valueOf(values[3].substring(0,2)),
//                    Integer.valueOf(values[3].substring(3)));

            LocalDateTime localDateTime = LocalDateTime.of(
                    LocalDate.parse(values[2], formatter),
                    LocalTime.of(Integer.valueOf(values[3].substring(0,2)),
                        Integer.valueOf(values[3].substring(3))));

            Optional<Person> personSender = personRepository.findById(1L);
            Optional<Person> personReceiver = personRepository.findById(2L);

            if(airportOriginOpt.isPresent() && airportDestinationOpt.isPresent()){

                shipment = new Shipment(values[0]+values[4]+values[1],
                        Integer.valueOf(values[5]),
                        OffsetDateTime.of(localDateTime, airportOriginOpt.get().getZoneId()),
                        personSender.get(), personReceiver.get(),
                        airportOriginOpt.get(), airportDestinationOpt.get());
                shipmentRepository.save(shipment);
            }
        }
        catch (Exception e) {
           System.out.println(e);
        }

        return shipment;
    }

    public Shipment getObjectByStringDummyPersons(String line, List<Airport> airportList, Person personSender, Person personReceiver){
        String[] values = FormatStrings.SplitShipmentString(line);

        Shipment shipment = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try{

            LocalDateTime localDateTime = LocalDateTime.of(
                    LocalDate.parse(values[2], formatter),
                    LocalTime.of(Integer.valueOf(values[3].substring(0,2)),
                            Integer.valueOf(values[3].substring(3))));

            Predicate<Airport> getAirportOrigin = airport -> airport.getOACI().contentEquals(values[0]);
            Predicate<Airport> getAirportDestiny = airport -> airport.getOACI().contentEquals(values[4]);

            Airport airportOrigin = airportList.stream().filter(getAirportOrigin).findAny().orElse(null);
            Airport airportDestination = airportList.stream().filter(getAirportDestiny).findAny().orElse(null);

            if(airportOrigin!=null && airportDestination!=null){

                shipment = new Shipment(values[0]+values[4]+values[1],
                        Integer.valueOf(values[5]),
                        OffsetDateTime.of(localDateTime, airportOrigin.getZoneId()),
                        personSender, personReceiver,
                        airportOrigin, airportDestination);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return shipment;
    }


    public Shipment getObjectByData(Person personSender, Person personReceiver, Airport origin, Airport destination,  Integer quantity){

        Shipment shipment = null;
        shipment = new Shipment(origin.getOACI()+destination.getOACI()+OffsetDateTime.now().toString(),
            quantity,
            OffsetDateTime.now(),
            personSender, personReceiver,
            origin, destination);
        shipment.setSimulation(1L);
        shipment=shipmentRepository.save(shipment);

        return shipment;
    }
}
