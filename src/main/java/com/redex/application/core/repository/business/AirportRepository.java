package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByOACI(String OACI_code);
    @Query("SELECT c FROM Airport c WHERE c.airportCity like '%?1%'")
    public User findByAirportCity(String airportCity);

}