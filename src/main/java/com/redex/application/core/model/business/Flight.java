package com.redex.application.core.model.business;

import javax.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;


@Entity
@Table(name = "flight")
@SQLDelete(sql = "UPDATE flight SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Flight extends BaseEntity {

    private String acronym;

    private LocalTime departureTime;
    private LocalTime arrivalTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_airport_departure")
    private Airport airportDeparture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_airport_arrival")
    private Airport airportArrival;

    @Override
    public String toString() {
        return "Flight{" +
                "acronym='" + acronym + '\'' +
                ", departure_time=" + departureTime +
                ", arrival_time=" + arrivalTime +
                ", airport_departure=" + airportDeparture.getAirportCity() +
                ", airport_arrival=" + airportArrival.getAirportCity() +
                '}';
    }
}
