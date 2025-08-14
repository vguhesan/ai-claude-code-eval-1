package com.company.inventory.controller;

import com.company.inventory.model.ProductDto;
import com.company.inventory.model.ProductMessage;
import com.company.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieves products by category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @Parameter(description = "Product category", required = true) @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/upc/{upcCode}")
    @Operation(summary = "Get product by UPC code", description = "Retrieves a product by its UPC code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductDto> getProductByUpcCode(
            @Parameter(description = "Product UPC code", required = true) @PathVariable String upcCode) {
        return productService.getProductByUpcCode(upcCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inStock/{inStock}")
    @Operation(summary = "Get products by stock status", description = "Retrieves products by their in-stock status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    public ResponseEntity<List<ProductDto>> getProductsByInStock(
            @Parameter(description = "In stock status", required = true) @PathVariable boolean inStock) {
        return ResponseEntity.ok(productService.getProductsByInStock(inStock));
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product and sends it to the queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the product", 
                    content = @Content(schema = @Schema(implementation = ProductMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content)
    })
    public ResponseEntity<ProductMessage> createProduct(@Valid @RequestBody ProductDto product) {
        ProductMessage message = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product and sends it to the queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the product", 
                    content = @Content(schema = @Schema(implementation = ProductMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductMessage> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id, 
            @Valid @RequestBody ProductDto product) {
        ProductMessage message = productService.updateProduct(id, product);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID and sends the deletion message to the queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sent delete request", 
                    content = @Content(schema = @Schema(implementation = ProductMessage.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductMessage> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id) {
        try {
            ProductMessage message = productService.deleteProduct(id);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}