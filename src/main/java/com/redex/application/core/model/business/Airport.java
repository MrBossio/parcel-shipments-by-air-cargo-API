package com.redex.application.core.model.business;
import javax.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZoneOffset;

@Entity
@Table(name = "airport")
@SQLDelete(sql = "UPDATE airport SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Airport extends BaseEntity{

    private String OACI;
    private String abbreviation;
    private String airportName;
    private String airportCity;
    private Double latitude;
    private Double longitude;
    private String airportCountry;
    private Integer warehouseTotalCapacity;
    private Integer warehouseActualCapacity;
    private ZoneOffset zoneId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_continent")
    private Continent continent;

    @Override
    public String toString() {
        return "Airport{" +
                "id =" + getId() +
                ", OACI='" + OACI + '\'' +
                ", airport_city='" + airportCity + '\'' +
                ", airport_country='" + airportCountry + '\'' +
                ", timeZone='" + zoneId + '\'' +
                '}';
    }

    public String customJson() {
        return "{" +
                "'id':" + getId() +
                ", 'icao':" + OACI +
                ", 'totalCapacity':" + warehouseTotalCapacity +
                ", 'continent':" + continent.getName() +
                "},";
    }
}
