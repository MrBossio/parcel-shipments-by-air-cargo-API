package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByAcronymAndDepartureTimeAndArrivalTime(String acronym, LocalTime departureTime, LocalTime arrivalTime);

    List<Flight> findAllByAirportDeparture(Airport airport);

    //TODO
    @Query("SELECT flight FROM Flight flight \n" +
            "where flight.airportDeparture = ?1\n " +
            "and flight.departureTime >= STR_TO_DATE(?2, '%H:%i:%s')\n" +
            "and flight.arrivalTime <= STR_TO_DATE(?3, '%H:%i:%s')\n" +
            "order by flight.departureTime")
    List<Flight> findFlightsFromDepartureInRange(Airport airport1, LocalTime departureTime, LocalTime arrivalTime);

    //TODO
    @Query("SELECT flight FROM Flight flight \n" +
            "where flight.airportDeparture = ?1\n " +
            "and flight.departureTime >= STR_TO_DATE(?2, '%H:%i:%s')\n" +
            "order by flight.departureTime")
    List<Flight> findFlightsFromDeparture(Airport airport1, LocalTime departureTime);

    //TODO
    @Query("SELECT flight FROM Flight flight \n" +
            "where flight.airportDeparture = ?1\n " +
            "and flight.airportArrival = ?2\n " +

            "and flight.departureTime >= STR_TO_DATE(?3, '%H:%i:%s')\n" +
            "and flight.arrivalTime <= STR_TO_DATE(?4, '%H:%i:%s')\n" +
            "order by flight.departureTime")
    List<Flight> findFlightsInRange(Airport airport1, Airport airport2, LocalTime departureTime, LocalTime arrivalTime);

    //TODO
    @Query("SELECT flight FROM Flight flight \n" +
            "where flight.airportDeparture = ?1\n " +
            "and flight.airportArrival = ?2\n " +
            "and flight.departureTime >= STR_TO_DATE(?3, '%H:%i:%s')\n" +
            "order by flight.departureTime")
    List<Flight> findFlightsInRoute(Airport airport1, Airport airport2, LocalTime departureTime);



//    @Query(value = "SELECT * FROM flight \n" +
//            "where flight.airportDeparture = ?1\n " +
//            "and flight.airportArrival = ?2\n " +
//
//            "and if( " +
//
//            "flight.departureTime <= flight.arrivalTime,\n" +
//
//            "flight.departureTime BETWEEN STR_TO_DATE(?3, '%H:%i:%s') and STR_TO_DATE(?4, '%H:%i:%s') and\n" +
//            "flight.arrivalTime BETWEEN STR_TO_DATE(?3, '%H:%i:%s') and STR_TO_DATE(?4, '%H:%i:%s'), \n" +
//
//            "flight.departureTime BETWEEN STR_TO_DATE(?4, '%H:%i:%s') and STR_TO_DATE('00:00:00', '%H:%i:%s') or\n" +
//            "flight.departureTime BETWEEN STR_TO_DATE('00:00:00', '%H:%i:%s') and STR_TO_DATE(?3, '%H:%i:%s') and\n" +
//            "flight.arrivalTime BETWEEN STR_TO_DATE(?4, '%H:%i:%s') and STR_TO_DATE('00:00:00', '%H:%i:%s') or\n" +
//            "flight.arrivalTime BETWEEN STR_TO_DATE('00:00:00', '%H:%i:%s') and STR_TO_DATE(?3, '%H:%i:%s') " +
//            ")\n" +
//            "order by flight.departureTime",
//            nativeQuery = true)
//    List<Flight> findFlightsInRange(Airport airport1, Airport airport2, LocalTime departureTime, LocalTime arrivalTime);

//    @Query(value = "SELECT * FROM flight \n" +
//            "where airport_departure = :airport1\n " +
//            "and airport_arrival = :airport2\n " +
//
//            "and if( " +
//
//            ":departureTime <= :arrivalTime,\n" +
//
//            ":departureTime BETWEEN STR_TO_DATE(:departureTime, '%H:%i:%s') and STR_TO_DATE(:arrivalTime, '%H:%i:%s') and\n" +
//            ":arrivalTime BETWEEN STR_TO_DATE(:departureTime, '%H:%i:%s') and STR_TO_DATE(:arrivalTime, '%H:%i:%s'), \n" +
//
//            ":departureTime BETWEEN STR_TO_DATE(:arrivalTime, '%H:%i:%s') and STR_TO_DATE('00:00:00', '%H:%i:%s') or\n" +
//            ":departureTime BETWEEN STR_TO_DATE('00:00:00', '%H:%i:%s') and STR_TO_DATE(:departureTime, '%H:%i:%s') and\n" +
//            ":arrivalTime BETWEEN STR_TO_DATE(:arrivalTime, '%H:%i:%s') and STR_TO_DATE('00:00:00', '%H:%i:%s') or\n" +
//            ":arrivalTime BETWEEN STR_TO_DATE('00:00:00', '%H:%i:%s') and STR_TO_DATE(:departureTime, '%H:%i:%s') " +
//            ")\n" +
//            "order by airport_departure",
//            nativeQuery = true)
//    List<Flight> findFlightsInRange(Airport airport1, Airport airport2, LocalTime departureTime, LocalTime arrivalTime);

}
