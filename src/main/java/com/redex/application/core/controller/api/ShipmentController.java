package com.redex.application.core.controller.api;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.model.business.User;
import com.redex.application.core.repository.business.ShipmentRepository;
import com.redex.application.core.repository.business.UserRepository;
import com.redex.application.sourcefiles.LoadShipments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")

public class ShipmentController {
    private ShipmentRepository shipmentRepository;
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private LoadShipments loadShipments;

    public ShipmentController(ShipmentRepository shipmentRepository, LoadShipments loadShipments){

        this.shipmentRepository = shipmentRepository;
        this.loadShipments = loadShipments;
    }

    @PostMapping("/shipment/register")
    public ResponseEntity<Shipment> registerShipment(@RequestBody Map<String, String> json) {
        String userSender = String.valueOf(json.get("userSender"));
        String userReceiver = String.valueOf(json.get("userReceiver"));
        String airportOrigin = String.valueOf(json.get("airportOrigin"));
        String airportDestination = String.valueOf(json.get("airportDestination"));
        Integer quantity = Integer.valueOf(json.get("quantity"));

        Shipment shipment= loadShipments.loadShipmentFromRegisteredData( userSender, userReceiver,  airportOrigin, airportDestination, quantity);
        if(shipment != null)
            return ResponseEntity.ok(shipment);
        else return ResponseEntity.notFound().build();
    }
}
