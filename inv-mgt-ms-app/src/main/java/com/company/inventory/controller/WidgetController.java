package com.company.inventory.controller;

import com.company.inventory.model.WidgetDto;
import com.company.inventory.model.WidgetMessage;
import com.company.inventory.service.WidgetService;
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
@RequestMapping("/api/widgets")
@RequiredArgsConstructor
@Tag(name = "Widget API", description = "Widget management endpoints")
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping
    @Operation(summary = "Get all widgets", description = "Retrieves a list of all widgets")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved widgets")
    public ResponseEntity<List<WidgetDto>> getAllWidgets() {
        return ResponseEntity.ok(widgetService.getAllWidgets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get widget by ID", description = "Retrieves a widget by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the widget"),
            @ApiResponse(responseCode = "404", description = "Widget not found", content = @Content)
    })
    public ResponseEntity<WidgetDto> getWidgetById(
            @Parameter(description = "Widget ID", required = true) @PathVariable String id) {
        return widgetService.getWidgetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get widgets by category", description = "Retrieves widgets by category")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved widgets")
    public ResponseEntity<List<WidgetDto>> getWidgetsByCategory(
            @Parameter(description = "Widget category", required = true) @PathVariable String category) {
        return ResponseEntity.ok(widgetService.getWidgetsByCategory(category));
    }

    @GetMapping("/upc/{upcCode}")
    @Operation(summary = "Get widget by UPC code", description = "Retrieves a widget by its UPC code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the widget"),
            @ApiResponse(responseCode = "404", description = "Widget not found", content = @Content)
    })
    public ResponseEntity<WidgetDto> getWidgetByUpcCode(
            @Parameter(description = "Widget UPC code", required = true) @PathVariable String upcCode) {
        return widgetService.getWidgetByUpcCode(upcCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available/{isAvailable}")
    @Operation(summary = "Get widgets by availability status", description = "Retrieves widgets by their availability status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved widgets")
    public ResponseEntity<List<WidgetDto>> getWidgetsByAvailability(
            @Parameter(description = "Availability status", required = true) @PathVariable boolean isAvailable) {
        return ResponseEntity.ok(widgetService.getWidgetsByAvailability(isAvailable));
    }

    @PostMapping
    @Operation(summary = "Create a new widget", description = "Creates a new widget and sends it to the queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the widget", 
                    content = @Content(schema = @Schema(implementation = WidgetMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid widget data", content = @Content)
    })
    public ResponseEntity<WidgetMessage> createWidget(@Valid @RequestBody WidgetDto widget) {
        WidgetMessage message = widgetService.createWidget(widget);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a widget", description = "Updates an existing widget and sends it to the queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the widget", 
                    content = @Content(schema = @Schema(implementation = WidgetMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid widget data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Widget not found", content = @Content)
    })
    public ResponseEntity<WidgetMessage> updateWidget(
            @Parameter(description = "Widget ID", required = true) @PathVariable String id, 
            @Valid @RequestBody WidgetDto widget) {
        WidgetMessage message = widgetService.updateWidget(id, widget);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a widget", description = "Deletes a widget by its ID and sends the deletion message to the queue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sent delete request", 
                    content = @Content(schema = @Schema(implementation = WidgetMessage.class))),
            @ApiResponse(responseCode = "404", description = "Widget not found", content = @Content)
    })
    public ResponseEntity<WidgetMessage> deleteWidget(
            @Parameter(description = "Widget ID", required = true) @PathVariable String id) {
        try {
            WidgetMessage message = widgetService.deleteWidget(id);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}