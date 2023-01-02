package com.redex.application.sourcefiles;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Flight;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.FlightRepository;
import com.redex.application.core.service.business.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoadFlights {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirportRepository airportRepository;

    private final Logger log = LoggerFactory.getLogger(LoadFlights.class);

    public void loadFlightsFromClassPathResource(ClassPathResource flights, ApplicationContext context){

        List<Flight> flightList = new ArrayList<>();
        List<Airport> airportList = airportRepository.findAll();
        log.info("Found "+airportList.size()+" airports in database");
        try{
            InputStream inputStreamFlights = flights.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamFlights));

            FlightService flightService = context.getBean(FlightService.class);

            Flight flight = null;
            while(reader.ready()){
                String line = reader.readLine();
                flight = flightService.createObjectByString(line, airportList);
//                System.out.println(flight.toString());
                flightList.add(flight);
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
        log.info("Generated "+flightList.size()+" flight items");
        flightRepository.saveAll(flightList);
        log.info("Saved in Flight table "+flightList.size()+" items");
    }

}
