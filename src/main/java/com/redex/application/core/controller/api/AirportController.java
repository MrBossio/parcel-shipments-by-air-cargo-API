package com.redex.application.core.controller.api;
import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.User;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AirportController {
    @Autowired
    AirportRepository airportRepository;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public AirportController(AirportRepository airportRepository){
        this.airportRepository = airportRepository;
    }

    @GetMapping("/airports")
    public List<Airport> findAllAirports(){
        log.warn(String.valueOf(airportRepository.findAll().size()));
        return airportRepository.findAll();
    }
}
