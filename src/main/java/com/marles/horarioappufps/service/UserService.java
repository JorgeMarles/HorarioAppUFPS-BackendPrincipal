package com.marles.horarioappufps.service;

import com.marles.horarioappufps.exception.UserNotFoundException;
import com.marles.horarioappufps.model.Role;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.repository.RoleRepository;
import com.marles.horarioappufps.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Optional<User> findUserByUid(String uid) {
        return userRepository.findById(uid);
    }

    private Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User getUserByUid(String uid) {
        return findUserByUid(uid).orElseThrow(()->new UserNotFoundException(uid));
    }

    public User getUserByEmail(String email) {
        return findUserByEmail(email).orElseThrow(()->new UserNotFoundException("email", email));
    }

    private User createUser(String uid, String email) {
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);

        user.setRoles(new HashSet<>());
        Role role = roleRepository.findByName("ROLE_USER").orElse(null);
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    public User getOrCreateUser(String uid, String email) {
        return findUserByUid(uid).orElseGet(() -> createUser(uid, email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
