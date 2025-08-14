package com.company.inventory.controller;

import com.company.inventory.model.InventoryItem;
import com.company.inventory.repository.InventoryViewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "Unified inventory view endpoints")
public class InventoryController {

    private final InventoryViewRepository inventoryViewRepository;

    @GetMapping
    @Operation(summary = "Get all inventory items", description = "Retrieves a list of all inventory items (products and widgets)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory items")
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        return ResponseEntity.ok(inventoryViewRepository.findAll());
    }

    @GetMapping("/type/{itemType}")
    @Operation(summary = "Get inventory items by type", description = "Retrieves inventory items by their type (Product or Widget)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory items")
    public ResponseEntity<List<InventoryItem>> getItemsByType(
            @Parameter(description = "Item type (Product or Widget)", required = true) @PathVariable String itemType) {
        return ResponseEntity.ok(inventoryViewRepository.findByItemType(itemType));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get inventory items by category", description = "Retrieves inventory items by category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory items")
    public ResponseEntity<List<InventoryItem>> getItemsByCategory(
            @Parameter(description = "Item category", required = true) @PathVariable String category) {
        return ResponseEntity.ok(inventoryViewRepository.findByCategory(category));
    }

    @GetMapping("/available/{available}")
    @Operation(summary = "Get inventory items by availability", description = "Retrieves inventory items by their availability status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory items")
    public ResponseEntity<List<InventoryItem>> getItemsByAvailability(
            @Parameter(description = "Availability status", required = true) @PathVariable boolean available) {
        return ResponseEntity.ok(inventoryViewRepository.findByAvailable(available));
    }

    @GetMapping("/maxPrice/{maxPrice}")
    @Operation(summary = "Get inventory items below a price threshold", description = "Retrieves inventory items with price less than or equal to the specified maximum")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory items")
    public ResponseEntity<List<InventoryItem>> getItemsWithMaxPrice(
            @Parameter(description = "Maximum price", required = true) @PathVariable BigDecimal maxPrice) {
        return ResponseEntity.ok(inventoryViewRepository.findByPriceLessThanEqual(maxPrice));
    }
}