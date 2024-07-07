package com.sme.meeting_planner.controllers.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sme.meeting_planner.model.auth.AuthResult;
import com.sme.meeting_planner.model.auth.LoginObject;
import com.sme.meeting_planner.services.auth.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService service;

    @PostMapping(value = "login")
    public AuthResult login(@RequestBody LoginObject loginObject) {
        return service.login(loginObject);
    }
}
