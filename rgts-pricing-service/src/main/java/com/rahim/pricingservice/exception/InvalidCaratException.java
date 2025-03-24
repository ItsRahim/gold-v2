package com.rahim.pricingservice.exception;

import com.rahim.common.exception.base.BadRequestException;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class InvalidCaratException extends BadRequestException {
    public InvalidCaratException(String message) {
        super(message);
    }
}
