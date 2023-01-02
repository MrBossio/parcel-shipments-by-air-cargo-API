package com.redex.application.core.model.business;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.redex.application.core.model.simulation.Simulation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shipment")
@SQLDelete(sql = "UPDATE shipment SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
public class Shipment extends BaseEntity implements Comparable<Shipment>{

    private String code;
    private Integer totalQuantity;
    private OffsetDateTime registrationDatetime;
    private OffsetDateTime limitDatetime;
    private OffsetDateTime deliveryDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_person_sender",
            foreignKey = @ForeignKey(name = "fk_shipment_sender"))
    private Person sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_person_receiver",
            foreignKey = @ForeignKey(name = "fk_shipment_receiver"))
    private Person receiver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_airport_origin",
            foreignKey = @ForeignKey(name = "fk_shipment_origin"))
    private Airport origin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_airport_destination",
            foreignKey = @ForeignKey(name = "fk_shipment_destination"))
    private Airport destination;

    private Long simulation;
    private boolean isPrevious;
    private boolean isCollapse;
    private boolean isAirportColapse;

    @OneToMany(mappedBy ="shipment",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @JsonManagedReference
    private List<TransportPlan> transportPlans;

    public Shipment(String code, Integer totalQuantity, OffsetDateTime offsetDateTime, Person sender, Person receiver, Airport origin, Airport destination) {
        this.code = code;
        this.totalQuantity = totalQuantity;
        this.sender = sender;
        this.receiver = receiver;
        this.origin = origin;
        this.destination = destination;

        this.registrationDatetime = offsetDateTime;

        if(origin.getContinent().getId() == destination.getContinent().getId()){
            this.limitDatetime = registrationDatetime.plusDays(1).minusHours(1);
        }
        else{
            this.limitDatetime = registrationDatetime.plusDays(2).minusHours(1);
        }
        this.deliveryDateTime = limitDatetime;

        transportPlans = new ArrayList<>();

        this.simulation = null;
        this.isPrevious = false;
        this.isCollapse = false;
        this.isAirportColapse = false;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "code='" + code + '\'' +
                ", total_quantity=" + totalQuantity +
                ", Reg=" + registrationDatetime +
                ", Limit=" + deliveryDateTime +
                ", origin=" + origin.getAirportCity() +
                ", destination=" + destination.getAirportCity() +
                '}';
    }

    @Override
    public int compareTo(Shipment o) {
        return getLimitDatetime().compareTo(o.getLimitDatetime());
    }
}
