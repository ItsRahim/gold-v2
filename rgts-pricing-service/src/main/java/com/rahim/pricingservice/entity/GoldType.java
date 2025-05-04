package com.rahim.pricingservice.entity;

import com.rahim.pricingservice.converter.WeightUnitConverter;
import com.rahim.pricingservice.enums.WeightUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gold_types", schema = "rgts")
public class GoldType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "carat_id", nullable = false)
  private GoldPurity carat;

  @Column(name = "weight", precision = 10, scale = 2)
  private BigDecimal weight;

  @NotNull
  @Convert(converter = WeightUnitConverter.class)
  @Column(name = "unit", nullable = false, length = 10)
  private WeightUnit unit;

  @NotNull
  @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
  private String description;

  @NotNull
  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;
}
