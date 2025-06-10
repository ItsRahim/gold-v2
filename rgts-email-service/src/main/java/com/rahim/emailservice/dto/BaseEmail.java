package com.rahim.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @created 10/06/2025
 * @author Rahim Ahmed
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEmail {
    String firstName;
    String lastName;
    String username;
}
