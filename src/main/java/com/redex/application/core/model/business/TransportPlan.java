package com.redex.application.core.model.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transportplan")
@SQLDelete(sql = "UPDATE transportplan SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@AllArgsConstructor
@Getter
@Setter
public class TransportPlan extends BaseEntity {

    private Integer quantity;

    @ManyToOne()
    @JoinColumn(name = "id_shipment")
    @JsonBackReference
    private Shipment shipment;


    @OneToMany(mappedBy = "transportPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<TransportPlanNode> transportPlanNodes;


    public TransportPlan() {
        transportPlanNodes = new ArrayList<>();
    }
}