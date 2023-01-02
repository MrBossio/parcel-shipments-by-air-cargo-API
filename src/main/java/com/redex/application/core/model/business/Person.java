package com.redex.application.core.model.business;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "person")
@SQLDelete(sql = "UPDATE person SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Person extends BaseEntity{

    @Column
    private Integer rol;

    @Column
    private String name;

    @Column
    private String idCard;

    @JoinColumn(name = "id_user")
    @OneToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private User idUser;

    @Override
    public String toString() {
        return "Person{" +
                "rol=" + rol +
                ", name='" + name + '\'' +
                ", id_card='" + idCard + '\'' +
                '}';
    }
}