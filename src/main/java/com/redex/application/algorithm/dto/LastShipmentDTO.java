package com.redex.application.algorithm.dto;

import com.redex.application.core.model.business.Shipment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LastShipmentDTO {
    Long id;
    String code;
    Integer totalQuantity;
    Long registrationDatetime;
    Long limitDatetime;
    String origin;
    String destination;
    Long simulation;
    boolean collapse;
    boolean previous;
    boolean airportCollapse;

    public LastShipmentDTO(Shipment shipment) {
        this.id = shipment.getId();
        this.code = shipment.getCode();
        this.totalQuantity = shipment.getTotalQuantity();
        this.registrationDatetime = shipment.getRegistrationDatetime().toInstant().toEpochMilli();
        this.limitDatetime = shipment.getLimitDatetime().toInstant().toEpochMilli();
        this.origin = shipment.getOrigin().getOACI();
        this.destination = shipment.getDestination().getOACI();
        this.simulation = shipment.getSimulation();
        this.collapse = shipment.isCollapse();
        this.previous = shipment.isPrevious();
        this.airportCollapse = shipment.isAirportColapse();
    }
}
