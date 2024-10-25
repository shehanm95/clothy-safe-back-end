package com.eastern.clothy.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "product_name", nullable = false)
    private String productName; // Product name

    @Column(name = "price", nullable = false)
    private Double price; // Product price

    @Column(name = "category", nullable = false)
    private String category; // Product category

    @Column(name = "product_image_link", nullable = false)
    private String productImageLink; // Product image URL

}