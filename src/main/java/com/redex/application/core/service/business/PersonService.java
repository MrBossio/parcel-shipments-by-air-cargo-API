package com.redex.application.core.service.business;

import com.redex.application.core.model.business.Person;
import com.redex.application.core.model.business.User;
import com.redex.application.core.repository.business.PersonRepository;
import com.redex.application.core.repository.business.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    private UserRepository userRepository;

    public Person[] createDummyPerson(){

        User user1 = new User();
        Person person1 = new Person(2, "John Doe", "12345678", user1);
        user1.setPerson(person1);
        user1.setPassword("xxxxxxxx");
        user1.setUsername("johndoe");
        user1.setEmail("john@doe.com");

        User user2 = new User();
        Person person2 = new Person(2, "Richard Roe", "87654321", user2);
        user2.setPerson(person2);
        user2.setPassword("xxxxxxxx");
        user2.setUsername("richarddoe");
        user2.setEmail("richard@roe.com");

        userRepository.save(user1);
        userRepository.save(user2);

        Person[] persons = new Person[2];
        persons[0] = person1;
        persons[1] = person2;

        return persons;

    }

}
