package com.rahim.pricingservice.dto;

import com.rahim.proto.protobuf.GoldPriceInfo;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Rahim Ahmed
 * @created 21/03/2025
 */
@Getter
@Setter
@Builder
@ToString
public class GoldPriceUpdateDTO {
    private String source;
    private BigDecimal price;
    private Instant timestamp;
    private String formattedDateTime;

    public static GoldPriceUpdateDTO fromProtobuf(GoldPriceInfo goldPriceInfo) {
        Instant instant;
        try {
            instant = Instant.parse(goldPriceInfo.getDatetime());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse datetime string: " + goldPriceInfo.getDatetime(), e);
        }

        BigDecimal price = BigDecimal.valueOf(goldPriceInfo.getPrice()).setScale(2, RoundingMode.HALF_UP);

        String formattedDate = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("UTC"))
                .format(instant);

        return GoldPriceUpdateDTO.builder()
                .source(goldPriceInfo.getSource())
                .price(price)
                .timestamp(instant)
                .formattedDateTime(formattedDate)
                .build();
    }
}
