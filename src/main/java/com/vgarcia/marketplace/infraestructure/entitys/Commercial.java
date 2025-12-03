package com.vgarcia.marketplace.infraestructure.entitys;

import com.vgarcia.marketplace.infraestructure.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "commercials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;


    @Column(precision = 10, scale = 2)
    private BigDecimal promotionalPrice;

    private boolean isPromotional;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage; // Ej: 0.15 para 15%

    private LocalDateTime promoStartDate;

    private LocalDateTime promoEndDate;

    // Tasa de impuesto aplicable (ej: 0.21 para 21%)
    @Column(precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private CurrencyCode currency;
}
