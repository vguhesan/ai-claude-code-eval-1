package com.company.inventory.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetDto {

    private String widgetId;

    @NotBlank(message = "Widget name cannot be blank")
    private String widgetName;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Size cannot be blank")
    private String size;

    @NotBlank(message = "UPC code cannot be blank")
    private String upcCode;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private Integer amount;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Material cannot be blank")
    private String material;

    @NotNull(message = "Weight cannot be null")
    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @NotNull(message = "Availability status cannot be null")
    private Boolean isAvailable;
}