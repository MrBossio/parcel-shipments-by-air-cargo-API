package com.redex.application.core.repository.simulation;


import com.redex.application.core.model.simulation.AirportSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AirportSimulationRepository extends JpaRepository<AirportSimulation, Long> {
    List<AirportSimulation> findByTimestampEqualsAndSimulationEqualsOrderById(
            OffsetDateTime timestamp, Long simulation
    );
}
