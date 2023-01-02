package com.redex.application.core.service.business;

import com.redex.application.core.Utils.DateAndTime;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Flight;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirportRepository airportRepository;

    public Flight getOrInsertByString(String line){
        String[] values = line.split("-");

        Optional<Airport> airportDepartureOpt = airportRepository.findByOACI(values[0]);
        Optional<Airport> airportArrivalOpt = airportRepository.findByOACI(values[1]);

        LocalTime departureTime = DateAndTime.HHMMStringToLocalTime(values[2]);
        LocalTime arrivalTime = DateAndTime.HHMMStringToLocalTime(values[3]);

        Optional<Flight> flightOpt = flightRepository.findByAcronymAndDepartureTimeAndArrivalTime(values[0]+values[1], departureTime, arrivalTime);
        Flight flight = null;
        if(flightOpt.isPresent()){
            flight = flightOpt.get();
        }
        else{
            if(airportArrivalOpt.isPresent() && airportArrivalOpt.isPresent()){
                flight = new Flight(
                        values[0]+values[1],
                        departureTime,
                        arrivalTime,
                        airportDepartureOpt.get(), airportArrivalOpt.get());
                flightRepository.save(flight);
            }
        }

        return flight;
    }

    public Flight createObjectByString(String line, List<Airport> airportList){
        String[] values = line.split("-");

        Predicate<Airport> getAirportOrigin = airport -> airport.getOACI().contentEquals(values[0]);
        Predicate<Airport> getAirportDestiny = airport -> airport.getOACI().contentEquals(values[1]);
        Airport airportOrigin = airportList.stream().filter(getAirportOrigin).findAny().orElse(null);
        Airport airportDestination = airportList.stream().filter(getAirportDestiny).findAny().orElse(null);

        LocalTime departureTime = DateAndTime.HHMMStringToLocalTime(values[2]);
        LocalTime arrivalTime = DateAndTime.HHMMStringToLocalTime(values[3]);

        Flight flight = null;

        if(airportOrigin!=null && airportDestination!=null){
            flight = new Flight(
                    values[0]+values[1],
                    departureTime,
                    arrivalTime,
                    airportOrigin, airportDestination);
        }

        return flight;
    }

}
