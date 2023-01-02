package com.redex.application.core.service.business;

import com.redex.application.algorithm.AstarAlgorithm.Node;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.repository.business.FlightCargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightCargoService {

    @Autowired
    private FlightCargoRepository flightCargoRepository;

    public int updateFlightCargos(Shipment shipment, List<Node> route, int remainder){

        int min = Integer.MAX_VALUE;
        int send = 0;
//        int total = shipment.getTotalQuantity();
        List<FlightCargo> flightCargos = new ArrayList<>();
        for(int i =0; i < route.size(); i++){

            if(i==0) continue;
            else{
                flightCargos.add(route.get(i).getFlightCargoOrigin());
                int capacity = route.get(i).getFlightCargoOrigin().getTotalCapacity() - route.get(i).getFlightCargoOrigin().getCurrentLoad();
                if(capacity <= 0){
                   min = -1;
                   break;
                }
                else{
                    if(capacity < min) min = capacity;
                }
            }
            if(i == route.size()-1){
                shipment.setDeliveryDateTime(route.get(i).getFlightCargoOrigin().getEndDateTime().plusHours(1));
            }
        }

        if(min>= remainder) send = remainder;
        else send = min;
        int first = 0;
        for (FlightCargo flightcargo: flightCargos) {
            flightcargo.increment(send);
            if(first == 0){
                first = 1;
            }
            else{
                flightcargo.incrementWFP(send);
            }
        }
//        flightCargoRepository.saveAll(flightCargos);

        return remainder-send;
    }

}
