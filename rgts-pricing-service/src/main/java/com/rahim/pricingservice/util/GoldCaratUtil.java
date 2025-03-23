package com.rahim.pricingservice.util;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
public class GoldCaratUtil {
    private final Map<Integer, Double> caratToPurity = new HashMap<>();
    private static final String CARAT_REGEX = "^([1-9]|1[0-9]|2[0-4])[Kk]?$";
    private static final Pattern pattern = Pattern.compile(CARAT_REGEX);

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 24; i++) {
            caratToPurity.put(i, i / 24.0);
        }
    }

    public Double getPurity(String input) {
        if (!isValidGoldCarat(input)) {
            throw new IllegalArgumentException("Invalid carat input: " + input);
        }
        try {
            String cleanInput = input.replaceAll("(?i)k", "").trim();
            int carat = Integer.parseInt(cleanInput);
            return caratToPurity.getOrDefault(carat, null);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid carat: " + input);
        }
    }

    private boolean isValidGoldCarat(String input) {
        if (input == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
