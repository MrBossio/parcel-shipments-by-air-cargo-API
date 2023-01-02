package com.redex.application.algorithm.dto;

import com.redex.application.core.model.business.Shipment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter

@Component
public class ResultSimulationByDayDTO {
    Long epochdatetime;
    Long simulation;
    List<AirportSimulationListDTO> current;
    List<FlightCargoDTO> flightCargoDTOList;
    LastShipmentDTO lastShipmentBeforeCollapse;
    LastShipmentDTO lastShipmentCauseCollapse;
    int total_registered;
    int total_loaded;
    int total_delivered;
    int total_departure;
    int total_arrival;
    int total_shipments;

    public ResultSimulationByDayDTO() {
    }

    public ResultSimulationByDayDTO(Long epochdatetime, Long simulation) {
        this.epochdatetime = epochdatetime;
        this.simulation = simulation;
        this.total_registered = 0;
        this.total_loaded=0;
        this.total_delivered = 0;
        this.total_departure = 0;
        this.total_arrival = 0;
        this.total_shipments = 0;
    }

}
