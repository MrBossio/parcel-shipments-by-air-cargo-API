package com.redex.application.core.model.simulation;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.BaseEntity;
import com.redex.application.core.model.business.Flight;
import com.redex.application.core.model.business.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "airportsimulation")
@SQLDelete(sql = "UPDATE airportsimulation SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AirportSimulation extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_airport",
            foreignKey = @ForeignKey(name = "fk_airport"))
    private Airport airport;

    private Long simulation;

    private Integer warehouseActualCapacity;

    private OffsetDateTime timestamp;

    public void increment(int quantity){
        warehouseActualCapacity += quantity;
    }
    public void decrement(int quantity){
        warehouseActualCapacity -= quantity;
    }

    @Override
    public String toString() {
        return "AirportSimulation{" +
                "airport=" + airport +
                ", warehouseActualCapacity=" + warehouseActualCapacity +
                '}';
    }
}
