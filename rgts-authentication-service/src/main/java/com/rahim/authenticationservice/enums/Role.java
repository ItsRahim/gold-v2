package com.rahim.authenticationservice.enums;

import lombok.Getter;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }
}
