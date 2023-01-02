package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.simulation.AirportSimulation;
import com.redex.application.core.model.simulation.FlightCargo;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.model.simulation.Simulation;
import com.redex.application.core.repository.business.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleRunAStarAlgorithm {
    @Autowired
    private RunAlgorithm runAlgorithmWired;
    @Autowired
    private  ShipmentRepository shipmentRepository;

    private final Logger log = LoggerFactory.getLogger(SimpleRunAStarAlgorithm.class);

    public int runAlgorithmFromBatch(List<Shipment> shipmentList,
                                     List<Airport> airportList,
                                     List<FlightCargo> flightCargoList,
                                     List<Shipment> previousAndActual,
                                     Simulation simulation,
                                     List<AirportSimulation> airportSimulationList){
        int result = 0;
        for (Shipment shipment : shipmentList) {
            previousAndActual.set(0, previousAndActual.get(1));
            previousAndActual.set(1,shipment);

            // first filter: if shipment cause airport collapse, this is collapse type -2
            for(AirportSimulation airportSimulation : airportSimulationList){
                if(airportSimulation.getAirport().getOACI().equals(shipment.getOrigin().getOACI())){
                    if(shipment.getTotalQuantity() > (airportSimulation.getAirport().getWarehouseTotalCapacity()-airportSimulation.getWarehouseActualCapacity())){
                        previousAndActual.get(0).setSimulation(simulation.getId());
                        previousAndActual.get(0).setPrevious(true);
                        previousAndActual.get(1).setSimulation(simulation.getId());
                        previousAndActual.get(1).setCollapse(true);
                        previousAndActual.get(1).setAirportColapse(true);
                        shipmentRepository.saveAll(previousAndActual);
                        log.warn("Shipment cause of collapse: ");
                        log.warn(shipment.toString());
                        return -2;
                    }
                    break;

                }
            }

            result = runAlgorithmWired.runAlgorithmWC(shipment, airportList, flightCargoList);
            if(result == -1) {
                previousAndActual.get(0).setSimulation(simulation.getId());
                previousAndActual.get(0).setPrevious(true);
                previousAndActual.get(1).setSimulation(simulation.getId());
                previousAndActual.get(1).setCollapse(true);
                shipmentRepository.saveAll(previousAndActual);
                break;
            }
        }
        return  result;
    }

}
