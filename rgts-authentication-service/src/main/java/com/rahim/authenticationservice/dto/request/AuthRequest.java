package com.rahim.authenticationservice.dto.request;

import lombok.Data;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Data
public class AuthRequest {
    private String username;
    private String password;
}
