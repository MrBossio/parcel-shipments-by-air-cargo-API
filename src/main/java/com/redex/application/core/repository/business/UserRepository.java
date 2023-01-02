package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT c FROM User c WHERE c.email = ?1")
    public User findByEmail(String email);
    //Optional<User> findByEmail(String email);

    @Query("SELECT c,p FROM User c INNER JOIN Person p ON c.id =p.idUser.id")
    public List<User> findAllComplete();
}
