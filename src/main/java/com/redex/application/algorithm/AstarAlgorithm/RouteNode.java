package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.model.simulation.FlightCargo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class RouteNode extends Node implements Comparable<RouteNode>{

    /*
     * A RouteNode is an envelope for Nodes, used in A* algorithm logic
     */

    private final Node current;
    private Node previous;
    private double routeScore;
    private double estimatedScore;
    private FlightCargo flightCargoConnection;

    RouteNode(Node current){
        this.current = current;
        this.previous = null;
        this.routeScore = Double.POSITIVE_INFINITY;
        this.estimatedScore = Double.POSITIVE_INFINITY;
    }

    RouteNode(Node current, Node previous, double routeScore, double estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    @Override
    public int compareTo(RouteNode other){
        if (this.estimatedScore > other.estimatedScore) {
            return 1;
        } else if (this.estimatedScore < other.estimatedScore) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        String strprevious;
        if(previous == null)
            strprevious =  "null id - null OACI";
        else
            strprevious = current.getAirport().getOACI() + "-"+ current.getAirport().getId();
        return "RouteNode{" +
                "current=" + current.getAirport().getOACI() + "-"+ current.getAirport().getId() +
                ", previous=" + strprevious  +
                ", routeScore=" + routeScore +
                ", estimatedScore=" + estimatedScore +
                '}';
    }
}
