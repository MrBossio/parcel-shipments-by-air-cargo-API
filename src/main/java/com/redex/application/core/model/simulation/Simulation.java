package com.redex.application.core.model.simulation;

import com.redex.application.core.model.business.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "simulation")
@SQLDelete(sql = "UPDATE simulation SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")



@Getter
@Setter
public class Simulation  extends BaseEntity {
    private boolean daily;
    private OffsetDateTime stopped;
    int total_registered;
    int total_loaded;
    int total_delivered;
    int total_departure;
    int total_arrival;
    int total_shipments;

    public Simulation() {

        this.daily = false;
        this.stopped = null;
        this.total_registered = 0;
        this.total_loaded = 0;
        this.total_delivered = 0;
        this.total_departure = 0;
        this.total_arrival = 0;
        this.total_shipments = 0;
    }

    public Simulation(boolean daily) {
        this.daily = daily;
        this.stopped = null;
        this.total_registered = 0;
        this.total_loaded = 0;
        this.total_delivered = 0;
        this.total_departure = 0;
        this.total_arrival = 0;
        this.total_shipments = 0;
    }

    public void setSimulationStatistics(int total_registered, int total_loaded, int  total_delivered, int total_departure, int total_arrival, int total_shipments){
        this.total_registered = total_registered;
        this.total_loaded = total_loaded;
        this.total_delivered = total_delivered;
        this.total_departure = total_departure;
        this.total_arrival = total_arrival;
        this.total_shipments = total_shipments;
    }
}
