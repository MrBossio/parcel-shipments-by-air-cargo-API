package com.redex.application.sourcefiles;

import com.redex.application.algorithm.AstarAlgorithm.RunAlgorithm;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Person;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.repository.business.*;
import com.redex.application.core.service.business.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class LoadShipments {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private RunAlgorithm runAlgorithmWired;
    @Autowired
    private LoadFlightCargo loadFlightCargo;
    @Autowired
    private FlightCargoRepository flightCargoRepository;

    private final Logger log = LoggerFactory.getLogger(LoadShipments.class);

//    @Async
    public void loadShipmentsFromClassPathResource(ClassPathResource shipments, ApplicationContext context, int days){

        List<Shipment> shipmentList = new ArrayList<>();
        Shipment first = null;

        Optional<Person> personSender = personRepository.findById(1L);
        Optional<Person> personReceiver = personRepository.findById(2L);

        List<Airport> airportList = airportRepository.findAll();

        try{
            InputStream inputStreamShipments = shipments.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamShipments));
            ShipmentService shipmentService = context.getBean(ShipmentService.class);
            Shipment shipment = null;
            while(reader.ready()){
                String line = reader.readLine();
                shipment = shipmentService.getObjectByStringDummyPersons(line, airportList, personSender.get(), personReceiver.get());

                //compare
                if (first == null){
                    first = shipment;
                }
                else{
                    if(shipment.getRegistrationDatetime().toLocalDate().isBefore(first.getRegistrationDatetime().toLocalDate())){
                        first = shipment;
                    }
                    LocalDate s = shipment.getRegistrationDatetime().toLocalDate();
                    LocalDate f = first.getRegistrationDatetime().toLocalDate().plusDays(days);
                    if(s.isEqual(f)){
                        break;
                    }
                }
                shipmentList.add(shipment);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
        shipmentRepository.saveAll(shipmentList);
        log.info("Saved in Shipment table "+shipmentList.size()+" items");
    }

    public  List<Shipment> dynamicLoadShipmentsFromClassPathResource(Resource shipments, List<Airport> airportList, List<Person> persons, Long epochdatetime){

        //get timestamp in string form, two days to past because we need a pre run of shipments
        OffsetDateTime timestampstart = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).minusDays(1);
        Long timestampstartLong = timestampstart.getYear()*10000L+timestampstart.getMonthValue()*100L+ (long) timestampstart.getDayOfMonth();
        //one day after end range = 6 days
        OffsetDateTime timestampend = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).plusDays(6);
        Long timestampendLong = timestampend.getYear()*10000L+timestampend.getMonthValue()*100L+ (long) timestampend.getDayOfMonth();


        List<Shipment> shipmentList = new ArrayList<>();

        Person personSender = persons.get(0);
        Person personReceiver = persons.get(1);

//        List<Airport> airportList = airportRepository.findAll();

        try{
            InputStream inputStreamShipments = shipments.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamShipments));

            Shipment shipment = null;
            while(reader.ready()){
                String line = reader.readLine();
                //find a day before timestamp (we don't know if timezone applied to date can change date to a day before)
                String dateLine = line.substring(14,22);
                Long dateLong = Long.parseLong(dateLine);
                if(dateLong < timestampstartLong) continue;
                if(dateLong > timestampendLong) break;

                shipment = shipmentService.getObjectByStringDummyPersons(line, airportList, personSender, personReceiver);

                shipmentList.add(shipment);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
        log.info("Loaded in memory "+shipmentList.size()+" items");
        return shipmentList;
    }

    public  List<Shipment> dynamicLoadShipmentsFromClassPathResourceEternal(Resource shipments, List<Airport> airportList, List<Person> persons, Long epochdatetime){

        //get timestamp in string form, two days to past because we need a pre run of shipments
        OffsetDateTime timestampstart = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).minusDays(1);
        Long timestampstartLong = timestampstart.getYear()*10000L+timestampstart.getMonthValue()*100L+ (long) timestampstart.getDayOfMonth();
        //one day after end range = 6 days
        OffsetDateTime timestampend = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochdatetime), ZoneOffset.UTC).plusDays(20);
        Long timestampendLong = timestampend.getYear()*10000L+timestampend.getMonthValue()*100L+ (long) timestampend.getDayOfMonth();


        List<Shipment> shipmentList = new ArrayList<>();

        Person personSender = persons.get(0);
        Person personReceiver = persons.get(1);

//        List<Airport> airportList = airportRepository.findAll();

        try{
            InputStream inputStreamShipments = shipments.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamShipments));

            Shipment shipment = null;
            while(reader.ready()){
                String line = reader.readLine();
                //find a day before timestamp (we don't know if timezone applied to date can change date to a day before)
                String dateLine = line.substring(14,22);
                Long dateLong = Long.parseLong(dateLine);
                if(dateLong < timestampstartLong) continue;
                if(dateLong > timestampendLong) break;

                shipment = shipmentService.getObjectByStringDummyPersons(line, airportList, personSender, personReceiver);

                shipmentList.add(shipment);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
        log.info("Loaded in memory "+shipmentList.size()+" items");
        return shipmentList;
    }

    public Shipment loadShipmentFromRegisteredData(String userSender, String userReceiver, String originOaci, String destinationOaci, Integer quantity){

        Optional<Person> personSender = Optional.ofNullable(personRepository.findByDNI(userSender));
        Optional<Person> personReceiver = Optional.ofNullable(personRepository.findByDNI(userReceiver));

        Optional<Airport> origin = airportRepository.findByOACI(originOaci);
        Optional<Airport> destination = airportRepository.findByOACI(destinationOaci);
        List<Airport> airportList = airportRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        OffsetDateTime today = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<FlightCargo> flightCargoList = flightCargoRepository.findBySimulationEqualsAndStartDateTimeGreaterThanEqualAndStartDateTimeLessThan(
                1L, today, today.plusDays(2)
        );

        if(origin.isPresent() && destination.isPresent()  && personSender.isPresent() && personReceiver.isPresent()){
            Shipment shipment = null;
            shipment = shipmentService.getObjectByData(personSender.get(), personReceiver.get(), origin.get(), destination.get(), quantity);

            int result =  runAlgorithmWired.runAlgorithmSingle(shipment, airportList, flightCargoList);
            if(result == -1) {
                shipment.setCollapse(true);
                log.info("COLLAPSE IN SHIPMENT "+shipment.getCode());
            }
            shipmentRepository.save(shipment);
            log.info("Registered Shipment "+shipment.getCode());
            return shipment;
        }
        else return null;
    }

}
