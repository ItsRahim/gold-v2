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
@Table(name = "price_history", schema = "rgts")
public class PriceHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "gold_type_id", nullable = false)
  private GoldType goldType;

  @Column(name = "price", precision = 10, scale = 2)
  private BigDecimal price;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
