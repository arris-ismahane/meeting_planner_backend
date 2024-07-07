package com.sme.meeting_planner.services.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.sme.meeting_planner.config.JwtUtils;
import com.sme.meeting_planner.model.auth.Admin;
import com.sme.meeting_planner.model.auth.AuthResult;
import com.sme.meeting_planner.model.auth.LoginObject;
import com.sme.meeting_planner.model.enums.Role;
import com.sme.meeting_planner.repositories.AdminRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AdminRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils utils;

    @PostConstruct
    private void init() {
        var superAdmin = repository.findOneByUsername("admin");
        if (superAdmin == null) {
            log.info("Creating a new super admin");
            superAdmin = new Admin();
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setRoles(List.of(Role.ADMIN));
            superAdmin.setUsername("admin");
            superAdmin.setPassword(passwordEncoder.encode("admin"));
            superAdmin.setFirstName("Admin");
            superAdmin.setLastName("Admin");
            repository.save(superAdmin);
        } else {
            log.info("A super admin has been found");
        }
    }

    public AuthResult login(LoginObject loginObject) {
        var user = repository.findOneByUsername(loginObject.username().toLowerCase());
        if (user == null || !passwordEncoder.matches(loginObject.password(), user.getPassword())) {
            throw new RuntimeJsonMappingException("invalidUsernameOrPassword");
        }
        String jwt = utils.createToken(user);
        return new AuthResult(user, jwt);
    }

}
