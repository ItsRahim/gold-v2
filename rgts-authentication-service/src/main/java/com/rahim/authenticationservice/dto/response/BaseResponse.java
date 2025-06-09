package com.rahim.authenticationservice.dto.response;

import com.rahim.authenticationservice.dto.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base response class for all API responses
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private String message;
    private ResponseStatus status;
}
