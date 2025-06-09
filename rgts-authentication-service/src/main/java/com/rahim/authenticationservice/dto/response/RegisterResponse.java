package com.rahim.authenticationservice.dto.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
@Data
@SuperBuilder
public class RegisterResponse extends BaseResponse {
    private UUID id;
    private String username;
    private String email;
}
