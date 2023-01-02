package com.redex.application.algorithm.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
@Getter
@Setter
public class AirportSimulationListDTO {
    private Long timestamp;
    private List<AirportSimulationDTO> airports;

    public AirportSimulationListDTO(OffsetDateTime timestamp, List<AirportSimulationDTO> airports) {
        this.timestamp = timestamp.toInstant().toEpochMilli();
        this.airports = airports;
    }
}
