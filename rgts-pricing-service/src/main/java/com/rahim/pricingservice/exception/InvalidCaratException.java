package com.rahim.pricingservice.exception;

import com.rahim.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class InvalidCaratException extends ApiException {
    public InvalidCaratException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
