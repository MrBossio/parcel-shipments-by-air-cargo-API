package com.redex.application.core.model.simulation;


import com.redex.application.core.model.business.BaseEntity;
import com.redex.application.core.model.business.Flight;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "flightcargo")
@SQLDelete(sql = "UPDATE flightcargo SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FlightCargo extends BaseEntity implements Comparable {

    private Integer totalCapacity;
    private Integer currentLoad;
    private Integer currentLoadWithoutFirstPointOnRoute;
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_flight")
    private Flight flight;

    private Long simulation;

    public FlightCargo(Integer totalCapacity, Integer currentLoad, Integer currentLoadWithoutFirstPointOnRoute, OffsetDateTime startDateTime, OffsetDateTime endDateTime, Flight flight) {
        this.totalCapacity = totalCapacity;
        this.currentLoad = currentLoad;
        this.currentLoadWithoutFirstPointOnRoute = currentLoadWithoutFirstPointOnRoute;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.flight = flight;
    }

    public void increment(int quantity){
        currentLoad += quantity;
    }
    public void incrementWFP(int quantity){
        currentLoadWithoutFirstPointOnRoute += quantity;
    }

    @Override
    public String toString() {
        return "FlightCargo{" +
                "id=" + flight.getId() +
                ", flight=" + flight.getAcronym() +
                ", origin=" + flight.getAirportDeparture().getAirportCity() +
                ", destination=" + flight.getAirportArrival().getAirportCity() +
                ", start=" + startDateTime +
                ", end=" + endDateTime +
                ", totalCapacity=" + totalCapacity +
                ", currentLoad=" + currentLoad +
                '}';
        
    }

    public String customJson() {

        Long zdtDT = startDateTime.toInstant().toEpochMilli();
        Long zdtAT = endDateTime.toInstant().toEpochMilli();
        return "{" +
                "  'id':" + getId() +
                ", 'departureAirport':'" + flight.getAirportDeparture().getOACI() +
                "', 'arrivalAirport':'" + flight.getAirportArrival().getOACI() +
                "', 'departureTime':'" + zdtDT +
                "', 'arrivalTime':'" + zdtAT +
                "', 'totalCapacity':" + totalCapacity +
                "},";
    }

    @Override
    public int compareTo(Object o) {
        FlightCargo other = (FlightCargo) o;
        if(this.getId() > other.getId()) return 1;
        else return -1;
    }

}
