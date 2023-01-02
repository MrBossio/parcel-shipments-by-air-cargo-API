package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.TransportPlan;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class RunAlgorithm {
    @Autowired
    AStarAlgorithm algorithm;

    private final Logger log = LoggerFactory.getLogger(RunAlgorithm.class);

    public int runAlgorithmWC(Shipment shipment,
                             List<Airport> airportList, List<FlightCargo> flightCargos){

        int remainder = shipment.getTotalQuantity();
        while(remainder > 0){
            algorithm.setUpVersionWC(shipment,airportList,flightCargos);
            remainder = algorithm.findRouteWC(shipment, remainder);
            if(remainder == -1){
                log.error("Shipment cause of collapse "+shipment.getCode());
                return -1;
            }
        }
        return 0;
    }

    public int runAlgorithmSingle(Shipment shipment,
                              List<Airport> airportList, List<FlightCargo> flightCargos){

        int remainder = shipment.getTotalQuantity();
        while(remainder > 0){
            TransportPlan transportPlan = new TransportPlan();
            transportPlan.setShipment(shipment);
            algorithm.setUpVersionWC(shipment,airportList,flightCargos);
            remainder = algorithm.findRouteByOne(shipment, transportPlan, remainder);
            if(remainder == -1){
                log.error("Shipment cause of collapse "+shipment.getCode());
                return -1;
            }
            shipment.getTransportPlans().add(transportPlan);
        }
        return 0;
    }
}
