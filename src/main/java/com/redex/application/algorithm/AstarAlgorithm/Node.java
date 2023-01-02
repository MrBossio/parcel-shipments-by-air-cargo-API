package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.simulation.FlightCargo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor

@Getter
@Setter
public class Node {

    /*
     * A Node is an envelope for airports.
     * As a difference of airports, a node have an attribute flightCargoOrigin,
     * where we get the flight that connected the previous node with this node.
    */

    private Long id;
    private Airport airport;
    private FlightCargo flightCargoOrigin;

    public Node(Airport airport) {
        this.id = airport.getId();
        this.airport = airport;
        this.flightCargoOrigin = null;
    }

    public String nodeValue(){
        return "->" + airport.getOACI();
    }

    @Override
    public String toString() {
        String route;
        if(flightCargoOrigin != null)
            route = flightCargoOrigin.toString();
        else
            route = "This is the origin";
        return "\nNode: \n" +
                "id=" + id + ", airport=" + airport + "\n" +
                route +
                "\n";
    }

}