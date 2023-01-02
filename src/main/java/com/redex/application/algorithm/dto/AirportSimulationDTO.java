package com.redex.application.algorithm.dto;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.simulation.AirportSimulation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirportSimulationDTO {
    String icao;
    Integer usedCapacity;
    Integer totalCapacity;

    public AirportSimulationDTO(AirportSimulation airportSimulation) {
        this.icao = airportSimulation.getAirport().getOACI();
        this.usedCapacity = airportSimulation.getWarehouseActualCapacity();
        this.totalCapacity = airportSimulation.getAirport().getWarehouseTotalCapacity();
    }
}
