package com.rahim.pricingservice.util;

import com.rahim.pricingservice.exception.InvalidCaratException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rahim Ahmed
 * @created 23/03/2025
 */
@Getter
@Service
public class GoldCaratUtil {
    private final Map<Integer, BigDecimal> caratToPurity = new HashMap<>();
    private static final String CARAT_REGEX = "^([1-9]|1[0-9]|2[0-4])[Kk]?$";
    private static final Pattern pattern = Pattern.compile(CARAT_REGEX);

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 24; i++) {
            BigDecimal purity = BigDecimal.valueOf(i).divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);
            caratToPurity.put(i, purity);
        }
    }

    //TODO: Might need re-work of how and when I throw exceptions. Change required in test and service level
    public BigDecimal getPurity(String input) {
        if (!isValidGoldCarat(input)) {
            throw new InvalidCaratException("Invalid carat input: " + input);
        }
        try {
            String cleanInput = input.replaceAll("(?i)k", "").trim();
            int carat = Integer.parseInt(cleanInput);
            return caratToPurity.getOrDefault(carat, null);
        } catch (NumberFormatException e) {
            throw new InvalidCaratException("Invalid carat: " + input);
        }
    }

    public boolean isValidGoldCarat(String input) {
        if (input == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
