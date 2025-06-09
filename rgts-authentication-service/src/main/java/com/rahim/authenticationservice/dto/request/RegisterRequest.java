package com.rahim.authenticationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Data
public class RegisterRequest {
    private String email;
    private String username;
    private String password;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
