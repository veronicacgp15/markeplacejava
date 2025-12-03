package com.vgarcia.marketplace.infraestructure.entitys;

import com.vgarcia.marketplace.infraestructure.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "commercials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commercial {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal promotionalPrice;

    private boolean isPromotional;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    private LocalDateTime promoStartDate;

    private LocalDateTime promoEndDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private CurrencyCode currency;

    @Embedded
    @Builder.Default
    private MetaData metadata = new MetaData();

    protected void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commercial that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Commercial{" +
                "productId=" + (product != null ? product.getId() : "null") +
                ", basePrice=" + basePrice +
                '}';
    }
}
