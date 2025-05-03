package com.rahim.pricingservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gold_prices", schema = "rgts")
public class GoldPrice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "price_id", nullable = false)
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gold_type_id")
  private GoldType goldType;

  @NotNull
  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
