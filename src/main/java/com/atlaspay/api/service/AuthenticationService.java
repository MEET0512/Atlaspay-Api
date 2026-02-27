package com.atlaspay.api.service;

import com.atlaspay.api.dto.JWTresponseDTO;
import com.atlaspay.api.dto.UserLoginDTO;
import com.atlaspay.api.dto.UserRegistrationDTO;

public interface AuthenticationService {

    JWTresponseDTO register(UserRegistrationDTO registrationDTO);

    JWTresponseDTO login(UserLoginDTO loginDTO);
}
