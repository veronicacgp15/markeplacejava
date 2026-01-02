package com.vgarcia.marketplace.infrastructure.entities;

import com.vgarcia.marketplace.infrastructure.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int currentStock;

    private int reservedStock;

    private String warehouseLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Version
    private Long version;

    @Embedded
    @Builder.Default
    private MetaData metadata = new MetaData();

    protected void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return id != null && Objects.equals(id, inventory.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "productId=" + (product != null ? product.getId() : "null") +
                ", currentStock=" + currentStock +
                ", reservedStock=" + reservedStock +
                '}';
    }
}
