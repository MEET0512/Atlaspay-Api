package com.atlaspay.api.service;

import com.atlaspay.api.dto.JWTresponseDTO;
import com.atlaspay.api.dto.UserLoginDTO;
import com.atlaspay.api.dto.UserRegistrationDTO;
import com.atlaspay.api.dto.UserResponseDTO;
import com.atlaspay.api.exception.DuplicateResourceException;
import com.atlaspay.api.exception.InvalidCredentialsException;
import com.atlaspay.api.model.User;
import com.atlaspay.api.model.user_status;
import com.atlaspay.api.repository.UserRepository;
import com.atlaspay.api.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
public class AuthenticationServiceImplement implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;


    @Override
    public JWTresponseDTO register(UserRegistrationDTO registrationDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(registrationDTO.getPhone())) {
            throw new DuplicateResourceException("Phone number already registered");
        }

        // Validate passwords match
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        // Create new user
        User user =  User.builder()
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .phone(registrationDTO.getPhone())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .address(registrationDTO.getAddress())
                .city(registrationDTO.getCity())
                .state(registrationDTO.getState())
                .zipCode(registrationDTO.getZipCode())
                .country(registrationDTO.getCountry())
                .status(user_status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtils.generateToken(savedUser.getEmail());

        return JWTresponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getTokenExperationTime())
                .user(mapToDTO(savedUser))
                .build();

    }

    /**
     * Login user with email and password
     *
     * @param loginDTO
     * @return
     */
    @Override
    public JWTresponseDTO login(UserLoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Authenticate user
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
        } catch (Exception ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail());

        return JWTresponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getTokenExperationTime())
                .user(mapToDTO(user))
                .build();
    }


    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .country(user.getCountry())
                .status(String.valueOf(user.getStatus()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
