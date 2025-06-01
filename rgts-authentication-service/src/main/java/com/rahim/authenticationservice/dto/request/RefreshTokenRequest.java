package com.rahim.authenticationservice.dto.request;

import lombok.Data;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
