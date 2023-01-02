package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.simulation.FlightCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface FlightCargoRepository extends JpaRepository<FlightCargo, Long> {

    @Query("select flightcargo from FlightCargo flightcargo\n" +
            "where flightcargo.flight.airportDeparture in(\n" +
            "    select flight.airportDeparture from Flight flight\n" +
            "    where flight.airportDeparture = ?1)\n" +
            "and flightcargo.currentLoad < flightcargo.totalCapacity\n" +
            "and flightcargo.startDateTime >= ?2\n" +
            "and flightcargo.endDateTime <= ?3")
    List<FlightCargo> findAllInRangeOfOffsetDateTime(
            Airport airport,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime);

    @Query("select flightcargo from FlightCargo flightcargo\n" +
            "where flightcargo.flight.airportDeparture in(\n" +
            "    select flight.airportDeparture from Flight flight\n" +
            "    where flight.airportDeparture = ?1)"+
            "and flightcargo.flight in(\n" +
            "    select flight from Flight flight\n" +
            "    where flight.airportDeparture.continent = flight.airportArrival.continent)"+
            "and flightcargo.currentLoad < flightcargo.totalCapacity\n" +
            "and flightcargo.startDateTime >= ?2\n" +
            "and flightcargo.endDateTime <= ?3")
    List<FlightCargo> findAllInRangeOfOffsetDateTimeSameContinent(
            Airport airport,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime);

    List<FlightCargo> findByStartDateTimeAfterAndStartDateTimeBeforeOrderByStartDateTime(
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    );

    List<FlightCargo> findBySimulationEqualsAndStartDateTimeGreaterThanEqualAndStartDateTimeLessThan(
            Long simulation,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    );

    Integer countBySimulationEqualsAndStartDateTimeGreaterThanEqualAndStartDateTimeLessThan(
            Long simulation,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    );


}
