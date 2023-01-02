package com.redex.application.sourcefiles;

import com.redex.application.core.model.simulation.Simulation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class LoadFiles {

    public void loadFilesAlgorithmAStar(ApplicationContext context){

		ClassPathResource flights = new ClassPathResource("/flight_plans_v01.txt");
		ClassPathResource airports = new ClassPathResource("/airports_v01.csv");
		LoadAirports loadAirports = context.getBean(LoadAirports.class);
		loadAirports.loadAirportsFromClassPathResource(airports, context);
		LoadFlights loadFlights = context.getBean(LoadFlights.class);
		loadFlights.loadFlightsFromClassPathResource(flights,context);

		LoadPersons loadPersons = context.getBean(LoadPersons.class);
		loadPersons.loadDummyPersons(context);

    }

	public void loadFilesDaily(ApplicationContext context, Simulation simulation){

		OffsetDateTime today = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		LoadFlightCargo loadFlightCargo = context.getBean(LoadFlightCargo.class);
		loadFlightCargo.generateFlightCargoDaily(today, simulation);

	}

}
