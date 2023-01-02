package com.redex.application.sourcefiles;

import com.redex.application.core.controller.api.UserController;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.service.business.AirportService;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoadAirports {

    @Autowired
    AirportRepository airportRepository;

    private final Logger log = LoggerFactory.getLogger(LoadAirports.class);

    public void loadAirportsFromClassPathResource(ClassPathResource airports, ApplicationContext context){

        List<Airport> airportList = new ArrayList<>();

        try{
            InputStream inputStreamAirports = airports.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamAirports));

            AirportService airportService = context.getBean(AirportService.class);

            Airport airport = null;
            while(reader.ready()){
                String line = reader.readLine();
                airport = airportService.createObjectByString(line);
                airportList.add(airport);
//                System.out.println(airport.toString());
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
        airportRepository.saveAll(airportList);
        log.info("Saved in Airport table "+airportList.size()+" items");
    }



}
