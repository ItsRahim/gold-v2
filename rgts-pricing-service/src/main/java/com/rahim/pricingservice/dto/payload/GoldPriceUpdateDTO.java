package com.rahim.pricingservice.dto.payload;

import com.rahim.common.util.DateUtil;
import com.rahim.proto.protobuf.price.GoldPriceInfo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.*;

/**
 * @author Rahim Ahmed
 * @created 21/03/2025
 */
@Getter
@Setter
@Builder
public class GoldPriceUpdateDTO {
  private String source;
  private BigDecimal price;
  private OffsetDateTime timestamp;
  private String formattedDateTime;

  public static GoldPriceUpdateDTO fromProtobuf(GoldPriceInfo goldPriceInfo) {
    OffsetDateTime offsetDateTime;
    try {
      offsetDateTime = OffsetDateTime.parse(goldPriceInfo.getDatetime());
    } catch (Exception e) {
      try {
        Instant instant = Instant.parse(goldPriceInfo.getDatetime());
        offsetDateTime = instant.atOffset(ZoneOffset.UTC);
      } catch (Exception ex) {
        throw new IllegalArgumentException(
            "Failed to parse datetime string: " + goldPriceInfo.getDatetime(), ex);
      }
    }

    BigDecimal price =
        BigDecimal.valueOf(goldPriceInfo.getPrice()).setScale(2, RoundingMode.HALF_UP);

    String formattedDate = DateUtil.formatOffsetDateTime(offsetDateTime);

    return GoldPriceUpdateDTO.builder()
        .source(goldPriceInfo.getSource())
        .price(price)
        .timestamp(offsetDateTime)
        .formattedDateTime(formattedDate)
        .build();
  }
}
