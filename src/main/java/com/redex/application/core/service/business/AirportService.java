package com.redex.application.core.service.business;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Continent;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.ContinentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.redex.application.core.Utils.DateAndTime.convertToZoneOffset;

@Service
public class AirportService {
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    ContinentRepository continentRepository;
    @Autowired
    ContinentService continentService;

    public static final String europa = "Europa";
    public static final String south_america = "America del Sur";

    public Airport getOrInsertByString(String line){

        String[] values = line.split(";");

        Continent continent = continentService.getOrCreateByName("", values[1]);

        Optional<Airport> airportOpt = airportRepository.findByOACI(values[2]);
        Airport airport = null;
        Integer warehouseTotalCapacity = 0;
        if(airportOpt.isPresent()){
            airport = airportOpt.get();
        }
        else{
            if(continent.getName().equals(europa)){
                warehouseTotalCapacity = 900;
            }
            else if(continent.getName().equals(south_america)){
                warehouseTotalCapacity = 850;
            }
            airport = new Airport(
                    values[2],
                    values[5],
                    "",
                    values[3],
                    Double.valueOf(values[6]),
                    Double.valueOf(values[7]),
                    values[4],
                    warehouseTotalCapacity,
                    0,
                    convertToZoneOffset(ZoneId.of(values[9])),
                    continent);
            airportRepository.save(airport);
        }
        return airport;
    }

    public Airport createObjectByString(String line){

        String[] values = line.split(";");

        Continent continent = continentService.getOrCreateByName("", values[1]);
        Airport airport = null;
        Integer warehouseTotalCapacity = 0;

        if(continent.getName().equals(europa)){
            warehouseTotalCapacity = 900;
        }
        else if(continent.getName().equals(south_america)){
            warehouseTotalCapacity = 850;
        }
        airport = new Airport(values[2],
                values[5],
                "",
                values[3],
                Double.valueOf(values[6]),
                Double.valueOf(values[7]),
                values[4],
                warehouseTotalCapacity,
                0,
                convertToZoneOffset(ZoneId.of(values[9])),
                continent);

        return airport;
    }



}
