package com.marles.horarioappufps.repository;


import com.marles.horarioappufps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findUserByEmail(String email);
}
