package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.Person;
import com.redex.application.core.model.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT c FROM Person c WHERE c.idCard = ?1")
    public Person findByDNI(String id_card);
}
