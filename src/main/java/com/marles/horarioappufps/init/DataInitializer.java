package com.marles.horarioappufps.init;

import com.marles.horarioappufps.model.Role;
import com.marles.horarioappufps.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        initRoles();
    }

    private final String[] ROLES = new String[]{"ROLE_SUPERADMIN", "ROLE_ADMIN", "ROLE_USER"};

    private void initRoles() {
        if (roleRepository.count() == 0) {
            for(String role : ROLES) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        }
    }
}
