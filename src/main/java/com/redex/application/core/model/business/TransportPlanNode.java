package com.redex.application.core.model.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.redex.application.algorithm.AstarAlgorithm.Node;
import com.redex.application.core.model.simulation.FlightCargo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "transportplannode")
@SQLDelete(sql = "UPDATE transportplannode SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransportPlanNode extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_flightCargoOrigin")
    private FlightCargo flightCargoOrigin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_airport")
    private Airport airport;


    @ManyToOne()
    @JoinColumn(name = "id_transportplan")
    @JsonBackReference
    private TransportPlan transportPlan;

    public TransportPlanNode(Node node, TransportPlan transportPlan){
        this.airport = node.getAirport();
        this.flightCargoOrigin = node.getFlightCargoOrigin();
        this.transportPlan = transportPlan;
    }

}