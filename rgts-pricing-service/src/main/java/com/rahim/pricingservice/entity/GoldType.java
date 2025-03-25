package com.rahim.pricingservice.entity;

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

  @Size(max = 3)
  @NotNull
  @Column(name = "carat", nullable = false, length = 3)
  private String carat;

  @Column(name = "weight", precision = 10, scale = 2)
  private BigDecimal weight;

  @NotNull
  @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
  private String description;

  public GoldType(String name, String carat, BigDecimal weight, String description) {
    this.name = name;
    this.carat = carat;
    this.weight = weight;
    this.description = description;
  }
}
