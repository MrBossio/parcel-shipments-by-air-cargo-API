package com.redex.application.sourcefiles;

import com.redex.application.core.model.business.Flight;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.model.simulation.Simulation;
import com.redex.application.core.repository.business.FlightCargoRepository;
import com.redex.application.core.repository.business.FlightRepository;
import com.redex.application.core.repository.business.ShipmentRepository;
import com.redex.application.core.repository.simulation.SimulationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LoadFlightCargo {

    @Autowired
    FlightRepository flightRepository;
    @Autowired
    ShipmentRepository shipmentRepository;
    @Autowired
    FlightCargoRepository flightCargoRepository;
    @Autowired
    SimulationRepository simulationRepository;

    // constants
    public static final String europa = "Europa";
    public static final String south_america = "America del Sur";
    private final Logger log = LoggerFactory.getLogger(LoadFlightCargo.class);

    public void generateFlightCargoByFlight( ApplicationContext context){

//        FlightRepository flightRepository = context.getBean(FlightRepository.class);
//        ShipmentRepository shipmentRepository = context.getBean(ShipmentRepository.class);
//        FlightCargoRepository flightCargoRepository = context.getBean(FlightCargoRepository.class);

        Optional<Shipment> first = shipmentRepository.findTopByOrderByRegistrationDatetimeAsc();
        Optional<Shipment> last = shipmentRepository.findTopByOrderByRegistrationDatetimeDesc();

        List<FlightCargo> flightCargoList = new ArrayList<>();

        if(first.isPresent() && last.isPresent()){

            OffsetDateTime start_date = first.get().getRegistrationDatetime();
            OffsetDateTime end_date = last.get().getRegistrationDatetime();

            OffsetDateTime start_date_zero =  first.get().getRegistrationDatetime();
            OffsetDateTime end_date_plus_three = end_date.plusDays(3);

            List<Flight> flights =  flightRepository.findAll(Sort.by(Sort.Direction.ASC, "departureTime"));
            Long timecorrection = 0L;

            while (!start_date_zero.toLocalDate().isAfter(end_date_plus_three.toLocalDate())){

                for (Flight flight: flights) {
                    timecorrection = 0L;

                    LocalDateTime startDateTime = LocalDateTime.of(start_date_zero.toLocalDate(), flight.getDepartureTime());
                    if(flight.getDepartureTime().toSecondOfDay() > flight.getArrivalTime().toSecondOfDay()){ timecorrection = 1L; }
                    LocalDateTime endDateTime = LocalDateTime.of(start_date_zero.toLocalDate().plusDays(timecorrection),flight.getArrivalTime());

                    Integer totalCapacity = 0;
                    if(flight.getAirportDeparture().getContinent().getId() ==  flight.getAirportArrival().getContinent().getId()){
                        if(flight.getAirportDeparture().getContinent().getName().equals(europa)){
                            totalCapacity = 250;
                        }
                        else if(flight.getAirportDeparture().getContinent().getName().equals(south_america)){
                            totalCapacity = 300;
                        }
                    }
                    else{
                        totalCapacity = 350;
                    }

                    FlightCargo flightCargo = new FlightCargo(
                            totalCapacity, 0, 0,
                            OffsetDateTime.of(startDateTime, flight.getAirportDeparture().getZoneId() ),
                            OffsetDateTime.of(endDateTime, flight.getAirportArrival().getZoneId()),
                            flight);

                    flightCargoList.add(flightCargo);
//                    System.out.println(flightCargo);
                }
                start_date_zero = start_date_zero.plusDays(1);
            }

            flightCargoRepository.saveAll(flightCargoList);
            log.info("Saved in FlightCargo table "+flightCargoList.size()+" items");
        }

    }

    public List<FlightCargo> generateFlightCargoFromDate(OffsetDateTime start_date, OffsetDateTime end_date, Long simulation){

        List<FlightCargo> flightCargoList = new ArrayList<>();

        OffsetDateTime start_date_minus_one =  start_date;
        OffsetDateTime end_date_plus_two = end_date.plusDays(2); //this is necessary for intercontinental flights

        List<Flight> flights =  flightRepository.findAll(Sort.by(Sort.Direction.ASC, "departureTime"));
        Long timecorrection = 0L;

        while (!start_date_minus_one.toLocalDate().isAfter(end_date_plus_two.toLocalDate())){

            for (Flight flight: flights) {
                timecorrection = 0L;

                LocalDateTime startDateTime = LocalDateTime.of(start_date_minus_one.toLocalDate(), flight.getDepartureTime());
                if(flight.getDepartureTime().toSecondOfDay() > flight.getArrivalTime().toSecondOfDay()){ timecorrection = 1L; }
                LocalDateTime endDateTime = LocalDateTime.of(start_date_minus_one.toLocalDate().plusDays(timecorrection),flight.getArrivalTime());

                Integer totalCapacity = 0;
                if(flight.getAirportDeparture().getContinent().getId() ==  flight.getAirportArrival().getContinent().getId()){
                    if(flight.getAirportDeparture().getContinent().getName().equals(europa)){
                        totalCapacity = 250;
                    }
                    else if(flight.getAirportDeparture().getContinent().getName().equals(south_america)){
                        totalCapacity = 300;
                    }
                }
                else{
                    totalCapacity = 350;
                }

                FlightCargo flightCargo = new FlightCargo(
                        totalCapacity, 0, 0,
                        OffsetDateTime.of(startDateTime, flight.getAirportDeparture().getZoneId() ),
                        OffsetDateTime.of(endDateTime, flight.getAirportArrival().getZoneId()),
                        flight, simulation);

                flightCargoList.add(flightCargo);

            }
            start_date_minus_one = start_date_minus_one.plusDays(1);
        }

        flightCargoRepository.saveAll(flightCargoList);
        log.info("Saved in FlightCargo table "+flightCargoList.size()+" items from simulation with id "+simulation);
        return flightCargoList;
    }

    public List<FlightCargo> generateFlightCargoDaily(OffsetDateTime start_date, Simulation simulation){

        OffsetDateTime end_date = start_date.plusDays(2);

        List<FlightCargo> flightCargoList = new ArrayList<>();

        List<Flight> flights =  flightRepository.findAll(Sort.by(Sort.Direction.ASC, "departureTime"));
        Long timecorrection = 0L;

        while (!start_date.toLocalDate().isAfter(end_date.toLocalDate())){

            //verify if selected day actually have flightcargos for daily simulation
            Integer flightCargosForThisDay = flightCargoRepository.countBySimulationEqualsAndStartDateTimeGreaterThanEqualAndStartDateTimeLessThan(
                    simulation.getId(), start_date, start_date.plusDays(1));
            if(flightCargosForThisDay>0) {
                start_date = start_date.plusDays(1);
                continue;
            }

            for (Flight flight: flights) {
                timecorrection = 0L;

                LocalDateTime startDateTime = LocalDateTime.of(start_date.toLocalDate(), flight.getDepartureTime());
                if(flight.getDepartureTime().toSecondOfDay() > flight.getArrivalTime().toSecondOfDay()){ timecorrection = 1L; }
                LocalDateTime endDateTime = LocalDateTime.of(start_date.toLocalDate().plusDays(timecorrection),flight.getArrivalTime());

                Integer totalCapacity = 0;
                if(flight.getAirportDeparture().getContinent().getId() ==  flight.getAirportArrival().getContinent().getId()){
                    if(flight.getAirportDeparture().getContinent().getName().equals(europa)){
                        totalCapacity = 250;
                    }
                    else if(flight.getAirportDeparture().getContinent().getName().equals(south_america)){
                        totalCapacity = 300;
                    }
                }
                else{
                    totalCapacity = 350;
                }

                FlightCargo flightCargo = new FlightCargo(
                        totalCapacity, 0, 0,
                        OffsetDateTime.of(startDateTime, flight.getAirportDeparture().getZoneId() ),
                        OffsetDateTime.of(endDateTime, flight.getAirportArrival().getZoneId()),
                        flight, simulation.getId());

                flightCargoList.add(flightCargo);

            }
            start_date = start_date.plusDays(1);
        }

        flightCargoRepository.saveAll(flightCargoList);
        log.info("Saved in FlightCargo table "+flightCargoList.size()+" items from daily operations with id "+simulation);
        return flightCargoList;
    }

}
