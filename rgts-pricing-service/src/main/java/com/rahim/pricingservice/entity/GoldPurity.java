package com.rahim.pricingservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gold_purities", schema = "rgts")
public class GoldPurity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 10)
  @NotNull
  @Column(name = "label", nullable = false, length = 10)
  private String label;

  @NotNull
  @Column(name = "numerator", nullable = false)
  private Integer numerator;

  @NotNull
  @Column(name = "denominator", nullable = false)
  private Integer denominator;

  @ColumnDefault("false")
  @Column(name = "is_base")
  private Boolean isBase;
}
