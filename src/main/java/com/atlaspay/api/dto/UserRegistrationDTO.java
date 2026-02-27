package com.atlaspay.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    private String first_name;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters.")
    private String last_name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
    private String phone_no;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain uppercase, lowercase, number, and special character")
    private String password;

    @NotBlank(message = "Confirm password in required.")
    private String confirm_password;

    private String address;

    private String city;

    private String state;

    private String zip_code;

    private String country;
}
