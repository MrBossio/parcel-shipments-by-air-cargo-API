package com.redex.application.algorithm.controller.api;
import com.redex.application.algorithm.dto.ResultSimulationByDayDTO;
import com.redex.application.algorithm.service.ResultsSimulationByDayService;
import com.redex.application.algorithm.service.StartSimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AStarController {

    private final Logger log = LoggerFactory.getLogger(AStarController.class);
    //attributes, constructor, methods

    private ResultsSimulationByDayService resultsSimulationByDayService;
    private StartSimulationService startSimulationService;

    public AStarController(ResultsSimulationByDayService resultsSimulationByDayService,
                           StartSimulationService startSimulationService){

        this.resultsSimulationByDayService = resultsSimulationByDayService;
        this.startSimulationService = startSimulationService;
    }

    @PostMapping("/startsimulation5d")
    public ResponseEntity<String> startSimulation5D(
            @RequestBody Map<String, String> json){
        Long epochtime = Long.valueOf(json.get("startdatetime"));
        String response = startSimulationService.startSimulation5D(epochtime);
        if(response != null)
            return ResponseEntity.ok(response);
        else return ResponseEntity.notFound().build();
    }

    @PostMapping("/startsimulationEternal")
    public ResponseEntity<String> startsimulationEternal(
            @RequestBody Map<String, String> json){
        Long epochtime = Long.valueOf(json.get("startdatetime"));
        String response = startSimulationService.startsimulationEternal(epochtime);
        if(response != null)
            return ResponseEntity.ok(response);
        else return ResponseEntity.notFound().build();
    }

    @GetMapping("/getsimulationbyday5d")
    public ResponseEntity<ResultSimulationByDayDTO> resultSimulationByDay5D(
            @RequestParam("datetime") Long epochtime,
            @RequestParam("simulation") Long simulation){
        ResultSimulationByDayDTO response = resultsSimulationByDayService.resultSimulationByDay5D(epochtime, simulation);
        if(response != null)
            return ResponseEntity.ok(response);
        else return ResponseEntity.notFound().build();
    }

    @PostMapping("/stopsimulation5d")
    public ResponseEntity<String> stopsSimulation5D(
            @RequestBody Map<String, String> json){
        Long simulationId = Long.valueOf(json.get("simulation"));
        String response = startSimulationService.stopSimulation5DById(simulationId);
        if(response != null)
            return ResponseEntity.ok(response);
        else return ResponseEntity.notFound().build();
    }

}
