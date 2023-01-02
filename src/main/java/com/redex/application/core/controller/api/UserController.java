package com.redex.application.core.controller.api;
import com.redex.application.core.repository.business.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import com.redex.application.core.model.business.User;

import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class UserController {

    private UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> findAll(){
        log.warn(String.valueOf(userRepository.findAll().size()));
        return userRepository.findAllComplete();
    }

    @GetMapping("/users/{dni}")
    public User findByDNI(@PathVariable String dni){
        List<User> users =  (List<User>) userRepository.findAllComplete();
        log.warn("El dni es: "+dni);
        User usernull=null;

        for (User user : users) {
            if (user.getPerson().getIdCard().equals(dni)) {
                return user;
            }
        }
        return usernull;
    }

    @GetMapping("/usersExist/{dni}")
    public Boolean existsDNI(@PathVariable String dni){
        List<User> users =  (List<User>) userRepository.findAllComplete();
        log.warn("El dni es: "+dni);
        for (User user : users) {
            if (user.getPerson().getIdCard().equals(dni)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/users/register")
    public Status registerUser(@Valid @RequestBody User  newUser) {
        List<User> users =  (List<User>) userRepository.findAllComplete();
        System.out.println("Lista de usuarios: "+users);
        System.out.println("New user: " + newUser.toString());
        for (User user : users) {
            System.out.println("Registered user: " + newUser.toString());
            if (user.equals(newUser)) {
                System.out.println("User Already exists!");
                return Status.USER_ALREADY_EXISTS;
            }
        }
        userRepository.save(newUser);
        return Status.SUCCESS;
    }

    @PostMapping("/users/login")
    public Object loginUser(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        List<User> users = userRepository.findAllComplete();
        //og.warn(user.toString());
        //log.warn(users.toString());


        for (User other : users) {
            //log.warn(String.valueOf(userRepository.findAll().size()));
            if ( (other.getEmail().equals(user.getEmail()) && (other.getPassword().equals(user.getPassword())))){
                //user.setLoggedIn(true);
                userRepository.save(user);
                response.setStatus( HttpServletResponse.SC_OK);
                return other;
            }
        }
        response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
        HashMap<String, String> map = new HashMap<>();
        map.put("msg", "Usuario o contraseña incorrecta");
        return map;
    }

    @PostMapping("/users/updatePassword")
    public Object updateResetPasswordToken(@RequestBody Map<String, String> json,
                                           HttpServletRequest request, HttpServletResponse response) {
        String token = String.valueOf(json.get("token"));
        String email = String.valueOf(json.get("email"));
        log.warn("Antes del findByEmail ");
        //System.out.println("Antes del findByEmail ");
        User customer = userRepository.findByEmail(email);
        if (customer != null) {
            System.out.println("User found: " + customer.toString());
            customer.setPassword(token);
            userRepository.save(customer);
            response.setStatus( HttpServletResponse.SC_OK);
            HashMap<String, String> map = new HashMap<>();
            map.put("msg", "Contraseña actualizada correctamente");
            return map;
        } else {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
            HashMap<String, String> map = new HashMap<>();
            map.put("msg", "No se pudo actualizar la contraseña porque el usuario no existe");
            return map;
        }
    }
    @DeleteMapping("/users/deleteall")
    public Status deleteUsers() {
        userRepository.deleteAll();
        return Status.SUCCESS;
    }

    @DeleteMapping("/users/delete/{id}")
    public Status deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return Status.SUCCESS;
    }
}