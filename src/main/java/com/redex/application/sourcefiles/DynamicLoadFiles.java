package com.redex.application.sourcefiles;

import com.redex.application.core.model.business.Airport;
import com.redex.application.core.model.business.Person;
import com.redex.application.core.model.business.Shipment;
import com.redex.application.core.repository.business.AirportRepository;
import com.redex.application.core.repository.business.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DynamicLoadFiles {

	@Autowired
	LoadShipments loadShipments;
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private AirportRepository airportRepository;
	private final Logger log = LoggerFactory.getLogger(DynamicLoadFiles.class);

    public List<Shipment> dynamicLoadShipmentsAlgorithmAStar(Long timestamp){

		List<Shipment> allShipments = new ArrayList<>();
		List<Person> persons = personRepository.findAll();
		List<Airport> airportList = airportRepository.findAll();
		try{
//			File fileResource = ResourceUtils.getFile("classpath: ./envios_historicos_v01");
//			log.info("Found "+fileResource.listFiles().length+" shipment files");
//			for(File file : fileResource.listFiles()) {
//				ClassPathResource shipmentsFile = new ClassPathResource("./envios_historicos_v01/" +file.getName());
//				List<Shipment> shipmentsByFile = new ArrayList<>();
//				shipmentsByFile = loadShipments.dynamicLoadShipmentsFromClassPathResource(shipmentsFile, airportList, persons, timestamp);
//				allShipments.addAll(shipmentsByFile);
//			}

			String scannedPackage = "scheduled_shipments_v01/*";
//			String scannedPackage = "otros_envios/*";
			PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
			Resource[] resources = scanner.getResources(scannedPackage);
			if (resources == null || resources.length == 0)
				log.warn("Warning: could not find any resources in this scanned package: " + scannedPackage);
			else {
				log.info("Found "+resources.length+" shipment files");
				for (Resource shipmentsFile : resources) {
					log.info(shipmentsFile.getFilename());
//					// Read the file content (I used BufferedReader, but there are other solutions for that):
//					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
//					String line = null;
//					while ((line = bufferedReader.readLine()) != null) {
//						// ...
//						// ...
//					}
//					bufferedReader.close();
					List<Shipment> shipmentsByFile = new ArrayList<>();
					shipmentsByFile = loadShipments.dynamicLoadShipmentsFromClassPathResource(shipmentsFile, airportList, persons, timestamp);
					allShipments.addAll(shipmentsByFile);

				}
			}
        }
        catch (Exception e){
			System.out.println(e);
		}

		return allShipments;

    }

	public List<Shipment> dynamicLoadShipmentsAlgorithmAStarEternal(Long timestamp){

		List<Shipment> allShipments = new ArrayList<>();
		List<Person> persons = personRepository.findAll();
		List<Airport> airportList = airportRepository.findAll();
		try{
//			File fileResource = ResourceUtils.getFile("classpath: ./envios_historicos_v01");
//			log.info("Found "+fileResource.listFiles().length+" shipment files");
//			for(File file : fileResource.listFiles()) {
//				ClassPathResource shipmentsFile = new ClassPathResource("./envios_historicos_v01/" +file.getName());
//				List<Shipment> shipmentsByFile = new ArrayList<>();
//				shipmentsByFile = loadShipments.dynamicLoadShipmentsFromClassPathResource(shipmentsFile, airportList, persons, timestamp);
//				allShipments.addAll(shipmentsByFile);
//			}

			String scannedPackage = "scheduled_shipments_v01/*";
//			String scannedPackage = "otros_envios/*";
			PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
			Resource[] resources = scanner.getResources(scannedPackage);
			if (resources == null || resources.length == 0)
				log.warn("Warning: could not find any resources in this scanned package: " + scannedPackage);
			else {
				log.info("Found "+resources.length+" shipment files");
				for (Resource shipmentsFile : resources) {
					log.info(shipmentsFile.getFilename());
//					// Read the file content (I used BufferedReader, but there are other solutions for that):
//					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
//					String line = null;
//					while ((line = bufferedReader.readLine()) != null) {
//						// ...
//						// ...
//					}
//					bufferedReader.close();
					List<Shipment> shipmentsByFile = new ArrayList<>();
					shipmentsByFile = loadShipments.dynamicLoadShipmentsFromClassPathResourceEternal(shipmentsFile, airportList, persons, timestamp);
					allShipments.addAll(shipmentsByFile);

				}
			}
		}
		catch (Exception e){
			System.out.println(e);
		}

		return allShipments;

	}

}
