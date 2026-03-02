package com.atlaspay.api.controller;

import com.atlaspay.api.dto.ApiResponse;
import com.atlaspay.api.dto.JWTresponseDTO;
import com.atlaspay.api.dto.UserLoginDTO;
import com.atlaspay.api.dto.UserRegistrationDTO;
import com.atlaspay.api.service.AuthenticationService;
import com.atlaspay.api.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Register a new user
     * POST /api/auth/register
     *
     * @param registrationDTO User registration details
     * @return JwtAuthenticationResponse with token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JWTresponseDTO>> register(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {
        System.out.print(registrationDTO);
        JWTresponseDTO response = authenticationService.register(registrationDTO);
        return ResponseUtil.created("User registered successfully", response);
    }

    /**
     * Login user and receive JWT token
     * POST /api/auth/login
     *
     * @param loginDTO User login credentials
     * @return JwtAuthenticationResponse with token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWTresponseDTO>> login(
            @Valid @RequestBody UserLoginDTO loginDTO) {

        JWTresponseDTO response = authenticationService.login(loginDTO);
        return ResponseUtil.success("Login successful", response);
    }
}
