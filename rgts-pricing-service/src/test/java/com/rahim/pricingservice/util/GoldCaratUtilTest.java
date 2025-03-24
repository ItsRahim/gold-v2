package com.rahim.pricingservice.util;

import com.rahim.pricingservice.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
public class GoldCaratUtilTest extends BaseTest {

    @Autowired
    private GoldCaratUtil goldCaratUtil;

    @Test
    public void shouldReturnMapWithAllGoldCaratPurities() {
        Map<Integer, BigDecimal> caratToPurity = goldCaratUtil.getCaratToPurity();
        assertThat(caratToPurity).isNotNull();
        assertThat(caratToPurity.size()).isEqualTo(24);
    }

    @Test
    public void shouldReturnPurityForValidCarat() {
        BigDecimal expectedPurity = BigDecimal.valueOf(0.92);
        BigDecimal purity = goldCaratUtil.getPurity("22K");
        assertThat(purity).isEqualTo(expectedPurity);
    }

    @Test
    public void shouldReturnPurityForValidCaratWithLowercaseK() {
        BigDecimal expectedPurity = BigDecimal.valueOf(0.92);
        BigDecimal purity = goldCaratUtil.getPurity("22k");
        assertThat(purity).isEqualTo(expectedPurity);
    }

    @Test
    public void shouldReturnPurityForValidCaratWithoutK() {
        BigDecimal expectedPurity = BigDecimal.valueOf(0.92);
        BigDecimal purity = goldCaratUtil.getPurity("22");
        assertThat(purity).isEqualTo(expectedPurity);
    }

    @Test
    public void shouldThrowExceptionForInvalidCaratInput() {
        assertThatThrownBy(() -> goldCaratUtil.getPurity("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> goldCaratUtil.getPurity("2.5K"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> goldCaratUtil.getPurity("25K"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> goldCaratUtil.getPurity("-24K"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldReturnTrueForValidGoldCarat() {
        assertThat(goldCaratUtil.isValidGoldCarat("19K")).isTrue();
        assertThat(goldCaratUtil.isValidGoldCarat("5K")).isTrue();
        assertThat(goldCaratUtil.isValidGoldCarat("1k")).isTrue();
    }

    @Test
    public void shouldReturnFalseForInvalidGoldCarat() {
        assertThat(goldCaratUtil.isValidGoldCarat("-.5K")).isFalse();
        assertThat(goldCaratUtil.isValidGoldCarat("1.5K")).isFalse();
    }
}
