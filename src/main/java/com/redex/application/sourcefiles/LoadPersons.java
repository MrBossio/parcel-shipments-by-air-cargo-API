package com.redex.application.sourcefiles;

import com.redex.application.core.service.business.PersonService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoadPersons {
    public void loadDummyPersons(ApplicationContext context){
        PersonService personService = context.getBean(PersonService.class);
        personService.createDummyPerson();
    }
}
