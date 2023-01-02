package com.redex.application.algorithm.dto;

import com.redex.application.core.model.simulation.FlightCargo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCargoDTO {

    Long id;
    String departureAirport;
    String arrivalAirport;
    Long departureTime;
    Long arrivalTime;
    Integer usedCapacity;
    Integer totalCapacity;

    public FlightCargoDTO(FlightCargo flightCargo) {
        this.id = flightCargo.getId();
        this.departureAirport = flightCargo.getFlight().getAirportDeparture().getOACI();
        this.arrivalAirport = flightCargo.getFlight().getAirportArrival().getOACI();
        this.departureTime = flightCargo.getStartDateTime().toInstant().toEpochMilli();
        this.arrivalTime = flightCargo.getEndDateTime().toInstant().toEpochMilli();
        this.usedCapacity = flightCargo.getCurrentLoad();
        this.totalCapacity = flightCargo.getTotalCapacity();
    }
}
