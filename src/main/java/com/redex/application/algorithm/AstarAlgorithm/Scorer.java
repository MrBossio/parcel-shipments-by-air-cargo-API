package com.redex.application.algorithm.AstarAlgorithm;

import com.redex.application.core.model.business.Shipment;
import org.springframework.context.ApplicationContext;

import java.time.LocalTime;

public interface Scorer {
    double computeCost(Node from, Node to, Shipment shipment);
}
