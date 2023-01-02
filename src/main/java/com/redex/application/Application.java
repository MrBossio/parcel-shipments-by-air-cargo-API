package com.redex.application;

import com.redex.application.core.model.simulation.Simulation;
import com.redex.application.core.service.simulation.SimulationService;
import com.redex.application.sourcefiles.LoadFiles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@EnableAsync
@SpringBootApplication
public class Application implements AsyncConfigurer {

	public static void main(String[] args) throws IOException {
		ApplicationContext context = SpringApplication.run(Application.class, args);

		// get or create simulation for daily operations
		SimulationService simulationService = context.getBean(SimulationService.class);
		Simulation simulation = simulationService.getOrCreateDailyOperation();

		LoadFiles loadFiles = new LoadFiles();
		loadFiles.loadFilesAlgorithmAStar(context);
		loadFiles.loadFilesDaily(context, simulation);

		System.out.println("All systems online");
	}



//	for cors restrictions
	@Bean
	public WebMvcConfigurer corsConfigurer(){
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
			}
		};
	}



}
