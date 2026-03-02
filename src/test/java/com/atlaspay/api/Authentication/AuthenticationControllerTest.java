package com.atlaspay.api.Authentication;

import com.atlaspay.api.AtlaspayApplication;
import com.atlaspay.api.config.SecurityConfig;
import com.atlaspay.api.controller.AuthController;
import com.atlaspay.api.dto.*;
import com.atlaspay.api.exception.DuplicateResourceException;
import com.atlaspay.api.exception.InvalidCredentialsException;
import com.atlaspay.api.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AtlaspayApplication.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController Tests")
public class AuthenticationController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDTO validRegistrationDTO;
    private UserLoginDTO validLoginDTO;
    private JWTresponseDTO jwtResponse;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup valid registration DTO
        validRegistrationDTO = UserRegistrationDTO.builder()
                .first_name("John")
                .last_name("Doe")
                .email("john.doe@example.com")
                .phone_no("+1234567890")
                .password("SecurePassword123!")
                .confirm_password("SecurePassword123!")
                .address("123 Main Street")
                .city("New York")
                .state("NY")
                .zip_code("10001")
                .country("USA")
                .build();

        // Setup valid login DTO
        validLoginDTO = UserLoginDTO.builder()
                .email("john.doe@example.com")
                .password("SecurePassword123!")
                .build();

        // Setup user response DTO
        userResponseDTO = UserResponseDTO.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main Street")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .country("USA")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        // Setup JWT response
        jwtResponse = jwtResponse.builder()
                .accessToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTYzNDU2NzM5MCwiZXhwIjoxNjM0NjUzNzkwfQ.test-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userResponseDTO)
                .build();
    }

    // ==================== Registration Tests ====================

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegister_Success() throws Exception {
        // Arrange
        when(authService.register(any(UserRegistrationDTO.class)))
                .thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect( jsonPath("$.success").value(true))
                .andExpect( jsonPath("$.message").value("User registered successfully"))
                .andExpect( jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect( jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect( jsonPath("$.data.expiresIn").value(86400000L))
                .andExpect( jsonPath("$.data.user.userId").value(1))
                .andExpect( jsonPath("$.data.user.email").value("john.doe@example.com"))
                .andExpect( jsonPath("$.data.user.firstName").value("John"))
                .andExpect( jsonPath("$.data.user.lastName").value("Doe"));
    }

    @Test
    @DisplayName("Should fail registration when email already exists")
    void testRegister_DuplicateEmail() throws Exception {
        // Arrange
        when(authService.register(any(UserRegistrationDTO.class)))
                .thenThrow(new DuplicateResourceException("Email already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDTO)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Email already registered"))
                .andExpect( jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"));
    }

    @Test
    @DisplayName("Should fail registration when phone already exists")
    void testRegister_DuplicatePhone() throws Exception {
        // Arrange
        when(authService.register(any(UserRegistrationDTO.class)))
                .thenThrow(new DuplicateResourceException("Phone number already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationDTO)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Phone number already registered"));
    }

    @Test
    @DisplayName("Should fail registration when passwords don't match")
    void testRegister_PasswordsMismatch() throws Exception {
        // Arrange
        UserRegistrationDTO invalidDTO = UserRegistrationDTO.builder()
                .first_name("John")
                .last_name("Doe")
                .email("john.doe@example.com")
                .phone_no("+1234567890")
                .password("SecurePassword123!")
                .confirm_password("DifferentPassword123!")
                .build();

        when(authService.register(any(UserRegistrationDTO.class)))
                .thenThrow(new InvalidCredentialsException("Passwords do not match"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    @DisplayName("Should fail registration with missing first name")
    void testRegister_MissingFirstName() throws Exception {
        // Arrange
        UserRegistrationDTO invalidDTO = UserRegistrationDTO.builder()
                .first_name(null)
                .last_name("Doe")
                .email("john.doe@example.com")
                .phone_no("+1234567890")
                .password("SecurePassword123!")
                .confirm_password("SecurePassword123!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Validation failed"))
                .andExpect( jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect( jsonPath("$.data.firstName").exists());
    }

    @Test
    @DisplayName("Should fail registration with invalid email format")
    void testRegister_InvalidEmailFormat() throws Exception {
        // Arrange
        UserRegistrationDTO invalidDTO = UserRegistrationDTO.builder()
                .first_name("John")
                .last_name("Doe")
                .email("invalid-email")
                .phone_no("+1234567890")
                .password("SecurePassword123!")
                .confirm_password("SecurePassword123!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.data.email").exists());
    }

    @Test
    @DisplayName("Should fail registration with weak password")
    void testRegister_WeakPassword() throws Exception {
        // Arrange - Password without special character
        UserRegistrationDTO invalidDTO = UserRegistrationDTO.builder()
                .first_name("John")
                .last_name("Doe")
                .email("john.doe@example.com")
                .phone_no("+1234567890")
                .password("weak")
                .confirm_password("weak")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.data.password").exists());
    }

    @Test
    @DisplayName("Should fail registration with invalid phone format")
    void testRegister_InvalidPhoneFormat() throws Exception {
        // Arrange
        UserRegistrationDTO invalidDTO = UserRegistrationDTO.builder()
                .first_name("John")
                .last_name("Doe")
                .email("john.doe@example.com")
                .phone_no("123")
                .password("SecurePassword123!")
                .confirm_password("SecurePassword123!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.data.phone").exists());
    }

    @Test
    @DisplayName("Should fail registration with missing required fields")
    void testRegister_MissingRequiredFields() throws Exception {
        // Arrange - Empty object
        String emptyJson = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Validation failed"));
    }

    // ==================== Login Tests ====================

    @Test
    @DisplayName("Should successfully login user with valid credentials")
    void testLogin_Success() throws Exception {
        // Arrange
        when(authService.login(any(UserLoginDTO.class)))
                .thenReturn(jwtResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect( content().contentType(MediaType.APPLICATION_JSON))
                .andExpect( jsonPath("$.success").value(true))
                .andExpect( jsonPath("$.message").value("Login successful"))
                .andExpect( jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect( jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect( jsonPath("$.data.user.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Should fail login with invalid email")
    void testLogin_InvalidEmail() throws Exception {
        // Arrange
        UserLoginDTO invalidDTO = UserLoginDTO.builder()
                .email("nonexistent@example.com")
                .password("SecurePassword123!")
                .build();

        when(authService.login(any(UserLoginDTO.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Invalid email or password"))
                .andExpect( jsonPath("$.errorCode").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("Should fail login with invalid password")
    void testLogin_InvalidPassword() throws Exception {
        // Arrange
        UserLoginDTO invalidDTO = UserLoginDTO.builder()
                .email("john.doe@example.com")
                .password("WrongPassword123!")
                .build();

        when(authService.login(any(UserLoginDTO.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should fail login with missing email")
    void testLogin_MissingEmail() throws Exception {
        // Arrange
        UserLoginDTO invalidDTO = UserLoginDTO.builder()
                .email(null)
                .password("SecurePassword123!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.data.email").exists());
    }

    @Test
    @DisplayName("Should fail login with missing password")
    void testLogin_MissingPassword() throws Exception {
        // Arrange
        UserLoginDTO invalidDTO = UserLoginDTO.builder()
                .email("john.doe@example.com")
                .password(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.data.password").exists());
    }

    @Test
    @DisplayName("Should fail login with invalid email format")
    void testLogin_InvalidEmailFormat() throws Exception {
        // Arrange
        UserLoginDTO invalidDTO = UserLoginDTO.builder()
                .email("invalid-email")
                .password("SecurePassword123!")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.success").value(false))
                .andExpect( jsonPath("$.data.email").exists());
    }

    @Test
    @DisplayName("Should return proper error response with all fields")
    void testLogin_ErrorResponseStructure() throws Exception {
        // Arrange
        when(authService.login(any(UserLoginDTO.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        // Act
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Assert - Check complete response structure
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);

        assertThat(apiResponse.isSuccess()).isFalse();
        assertThat(apiResponse.getMessage()).isNotEmpty();
        assertThat(apiResponse.getErrorCode()).isNotEmpty();
        assertThat(apiResponse.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle malformed JSON in request")
    void testRegister_MalformedJSON() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle empty request body")
    void testLogin_EmptyBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
