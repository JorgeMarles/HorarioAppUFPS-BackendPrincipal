package com.marles.horarioappufps.service;

import com.marles.horarioappufps.dto.request.UserUpdateDto;
import com.marles.horarioappufps.exception.CustomEntityNotFoundException;
import com.marles.horarioappufps.exception.UserException;
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
        return findUserByUid(uid).orElseThrow(() -> new UserNotFoundException(uid));
    }

    public User getUserByEmail(String email) {
        return findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("email", email));
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName("ROLE_" + roleName).orElseThrow(() -> new CustomEntityNotFoundException("Role", "name", roleName));
    }

    public void addRole(User user, String roleName) {
        user.getRoles().add(getRoleByName(roleName));
    }

    public void deleteRole(User user, String roleName) {
        user.getRoles().remove(getRoleByName(roleName));
    }

    public boolean containsRole(User user, String roleName) {
        return user.getRoles().contains(getRoleByName(roleName));
    }

    private User createUser(String uid, String email, String name) {
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setName(name == null ? "-" : name);

        user.setRoles(new HashSet<>());
        addRole(user, "USER");
        //Si es el primer usuario, es el super admin
        if (userRepository.count() == 0) {
            addRole(user, "ADMIN");
            addRole(user, "SUPERUSER");
        }

        return userRepository.save(user);
    }

    public User updateUser(String uid, UserUpdateDto userUpdateDto) {
        User user = getUserByUid(uid);
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());

        return userRepository.save(user);
    }

    public User toggleUserRole(String uid) {
        User user = getUserByUid(uid);
        if (containsRole(user, "SUPERADMIN")) {
            throw new UserException("No se puede degradar al Administrador Original");
        }
        if (containsRole(user, "ADMIN")) {
            deleteRole(user, "ADMIN");
        } else {
            addRole(user, "ADMIN");
        }
        return userRepository.save(user);
    }

    public User getOrCreateUser(String uid, String email, String name) {
        return findUserByUid(uid).orElseGet(() -> createUser(uid, email, name));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
