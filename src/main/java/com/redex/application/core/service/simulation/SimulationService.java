package com.redex.application.core.service.simulation;

import com.redex.application.algorithm.service.ResultsSimulationByDayService;
import com.redex.application.algorithm.service.StartSimulationService;
import com.redex.application.core.model.simulation.Simulation;
import com.redex.application.core.repository.simulation.SimulationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SimulationService {

    @Autowired
    private SimulationRepository simulationRepository;

    private final Logger log = LoggerFactory.getLogger(StartSimulationService.class);


//    public String startSimulation5D(Long epochdatetime){
//        //start simulation
//        Simulation simulation = new Simulation();
//        simulationRepository.save(simulation);
//        resultsSimulationByDayService.calculateSimulation5D(epochdatetime, simulation);
//        return "{\"id_simulation\": " + simulation.getId() + "}";
//    }

    public Simulation getUpdatedSimulationData(Long id){
        Optional<Simulation> dailySimulation = simulationRepository.findById(id);
        return dailySimulation.orElse(null);
    }

    public Simulation getOrCreateDailyOperation(){
        List<Simulation> dailySimulation = simulationRepository.findByDailyTrue();

        if(dailySimulation.size()>0) return dailySimulation.get(0);
        else{
            Simulation simulation = new Simulation(true);
            simulationRepository.save(simulation);
            return simulation;
        }
    }

//    public String stopSimulation5DById(Long id){
//        //checks if simulation exists and is not the daily simulation
//        Optional<Simulation> simulationOpt = simulationRepository.findById(id);
//        if(simulationOpt.isPresent()){
//            Simulation simulation = simulationOpt.get();
//            if(simulation.isDaily()){
//                log.warn("Found simulation "+simulation.getId()+" but it's daily, will not be stopped." );
//                return "{" +
//                        "\"message\": \"found simulation , but it's daily, will not be stopped\""+
//                        "}";
//            }
//            else{
//                simulation.setStopped(OffsetDateTime.now());
//                simulationRepository.save(simulation);
//                log.warn("Stopping simulation "+simulation.getId()+", please wait.");
//                return "{" +
//                        "\"message\": \"stopped simulation at "+ simulation.getStopped().toString()+"\""+
//                        "\"id_simulation\": " + simulation.getId() +
//                        "}";
//            }
//        }
//        else{
//            log.warn("Simulation "+id+" does not exists.");
//            return "{" +
//                    "\"message\": \"simulation doesn't exists\""+
//                    "}";
//        }
//    }
}
