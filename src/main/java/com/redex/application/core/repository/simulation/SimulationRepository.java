package com.redex.application.core.repository.simulation;

import com.redex.application.core.model.business.Continent;
import com.redex.application.core.model.simulation.AirportSimulation;
import com.redex.application.core.model.simulation.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface SimulationRepository  extends JpaRepository<Simulation, Long>{

    List<Simulation> findByDailyTrue();


}

