package com.vgarcia.marketplace.infraestructure.entitys;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;


@Entity
@Table(name = "categories")
@Data
@ToString(exclude = "products")
@NoArgsConstructor
@AllArgsConstructor

public class Category{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;




}
