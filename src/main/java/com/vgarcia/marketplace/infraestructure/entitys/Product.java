package com.vgarcia.marketplace.infraestructure.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_sku",
                columnList = "sku",
                unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El SKU no puede estar vacío.")
    @Size(max = 50, message = "El SKU no puede exceder los 50 caracteres.")
    @Column(unique = true, nullable = false, length = 50)
    private String sku;


    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String name;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Commercial commercial;


    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Inventory inventory;

    @Lob
    @Column(name = "similarity_vector")
    private String similarityVectorJson;

    @Embedded
    @Builder.Default
    private MetaData metadata = new MetaData();

    protected void setCategory(Category category) {
        this.category = category;
    }

    public void assignCommercialInfo(Commercial newCommercial) {
        if (this.commercial != null) {
            this.commercial.setProduct(null);
        }
        this.commercial = newCommercial;
        if (newCommercial != null) {
            newCommercial.setProduct(this);
        }
    }


    public void assignInventory(Inventory newInventory) {
        if (this.inventory != null) {
            this.inventory.setProduct(null);
        }
        this.inventory = newInventory;
        if (newInventory != null) {
            newInventory.setProduct(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return sku != null && Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return sku != null ?
                sku.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
