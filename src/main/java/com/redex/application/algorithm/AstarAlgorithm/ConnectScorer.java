package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.model.business.Shipment;

public class ConnectScorer implements Scorer{

    @Override
    public double computeCost(Node from,
                              Node to, Shipment shipment){

            //we use three combined heuristics: distance calculated from haversine formula, time to departure in flight and current load.
            //time to travel and current load are penalties, distance has the primary weight.

            //distance:
            double R = 6372.8; // Earth's Radius, in kilometers
            double dLat = Math.toRadians(to.getAirport().getLatitude() - from.getAirport().getLatitude());
            double dLon = Math.toRadians(to.getAirport().getLongitude() - from.getAirport().getLongitude());
            double lat1 = Math.toRadians(from.getAirport().getLatitude());
            double lat2 = Math.toRadians(to.getAirport().getLatitude());
            double a = Math.pow(Math.sin(dLat / 2),2)
                    + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));

            double earthDistanceScore =  R * c;

            double timeToTravelScore = 0.0;
            double currentLoad = 0.0;

            //if is a calc between two regular connected nodes, time to flight  is added as penalty
            if(to.getFlightCargoOrigin() != null) // all nodes except the first node have a flight with which that node was reached
            {
                timeToTravelScore = (double) to.getFlightCargoOrigin().getStartDateTime().toEpochSecond() - shipment.getRegistrationDatetime().toEpochSecond();
                currentLoad = to.getFlightCargoOrigin().getCurrentLoad();
            }

            //score, actual current load in flight is a penalty
            return earthDistanceScore*10 + timeToTravelScore*0.5 + currentLoad*20;

    }
}
