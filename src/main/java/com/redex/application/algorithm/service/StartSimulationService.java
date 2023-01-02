package com.redex.application.algorithm.service;

import com.redex.application.core.model.simulation.Simulation;
import com.redex.application.core.repository.simulation.SimulationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class StartSimulationService {
    @Autowired
    private SimulationRepository simulationRepository;
    @Autowired
    private  ResultsSimulationByDayService resultsSimulationByDayService;

    private final Logger log = LoggerFactory.getLogger(StartSimulationService.class);


    public String startSimulation5D(Long epochdatetime){
        //start simulation
        Simulation simulation = new Simulation();
        simulationRepository.save(simulation);
        resultsSimulationByDayService.calculateSimulation5D(epochdatetime, simulation);
        return "{\"id_simulation\": " + simulation.getId() + "}";
    }

    public String startsimulationEternal(Long epochdatetime){
        //start simulation
        Simulation simulation = new Simulation();
        simulationRepository.save(simulation);
        resultsSimulationByDayService.calculateSimulationEternal(epochdatetime, simulation);
        return "{\"id_simulation\": " + simulation.getId() + "}";
    }

    public String stopSimulation5DById(Long id){
        //checks if simulation exists and is not the daily simulation
        Optional<Simulation> simulationOpt = simulationRepository.findById(id);
        if(simulationOpt.isPresent()){
            Simulation simulation = simulationOpt.get();
            if(simulation.isDaily()){
                log.warn("Found simulation "+simulation.getId()+" but it's daily, will not be stopped." );
                return "{" +
                        "\"message\": \"found simulation , but it's daily, will not be stopped\""+
                        "}";
            }
            else{
                simulation.setStopped(OffsetDateTime.now());
                simulationRepository.save(simulation);
                log.warn("Stopping simulation "+simulation.getId()+", please wait.");
                return "{" +
                        "\"message\": \"stopped simulation at "+ simulation.getStopped().toString()+"\""+
                        "\"id_simulation\": " + simulation.getId() +
                        "}";
            }
        }
        else{
            log.warn("Simulation "+id+" does not exists.");
            return "{" +
                    "\"message\": \"simulation doesn't exists\""+
                    "}";
        }
    }

}
