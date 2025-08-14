package com.company.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @NotBlank
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull
    @Positive
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotBlank
    @Column(name = "size", nullable = false)
    private String size;

    @NotBlank
    @Column(name = "upc_code", nullable = false, unique = true)
    private String upcCode;

    @NotNull
    @Min(0)
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @NotBlank
    @Column(name = "category", nullable = false)
    private String category;

    @NotBlank
    @Column(name = "color", nullable = false)
    private String color;

    @NotNull
    @Positive
    @Column(name = "weight", nullable = false)
    private BigDecimal weight;

    @NotNull
    @Column(name = "in_stock", nullable = false)
    private Boolean inStock;
}