package com.redex.application.core.model.business;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user")
@SQLDelete(sql = "UPDATE user SET active = 0 WHERE id = ?")
@Where(clause = "active = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class User extends BaseEntity{
    
    @OneToOne(mappedBy = "idUser",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private Person person;

    @Column(name = "username")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Override
    public boolean equals(Object o) {
        //if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) || Objects.equals(getPerson().getIdCard(), user.person.getIdCard()) ;
    }

    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                ", username=" + username +
                ", password=" + password +
                ", email=" + email +
                ", person=" + person +
                '}';
    }

}
