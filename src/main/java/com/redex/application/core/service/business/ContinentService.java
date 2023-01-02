package com.redex.application.core.service.business;

import com.redex.application.core.model.business.Continent;
import com.redex.application.core.repository.business.ContinentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContinentService {

    @Autowired
    private ContinentRepository continentRepository;

    public Continent getOrCreateByName(String ISO_code, String name){
        Optional<Continent> continentOpt = continentRepository.findByName(name);
        Continent continent = null;
        if(continentOpt.isPresent()){
            continent = continentOpt.get();
            return continent;
        }
        else{
            continent = new Continent(ISO_code, name);
            continentRepository.save(continent);
            return continent;
        }
    }
}
