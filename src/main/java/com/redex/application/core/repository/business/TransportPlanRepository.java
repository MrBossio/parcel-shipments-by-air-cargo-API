package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.TransportPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long> {
}
