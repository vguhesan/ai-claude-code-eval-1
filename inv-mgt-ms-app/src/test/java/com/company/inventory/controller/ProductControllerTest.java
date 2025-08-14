package com.company.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.inventory.model.OperationType;
import com.company.inventory.model.ProductDto;
import com.company.inventory.model.ProductMessage;
import com.company.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private ProductDto testProduct;
    private ProductMessage testProductMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        // Setup test data
        testProduct = ProductDto.builder()
                .productId(1)
                .productName("Test Product")
                .price(new BigDecimal("19.99"))
                .size("Medium")
                .upcCode("123456789012")
                .amount(10)
                .category("Test")
                .color("Red")
                .weight(new BigDecimal("1.5"))
                .inStock(true)
                .build();

        testProductMessage = ProductMessage.builder()
                .product(testProduct)
                .operationType(OperationType.CREATE)
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        // Arrange
        List<ProductDto> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productName", is("Test Product")))
                .andExpect(jsonPath("$[0].price", is(19.99)));
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", is("Test Product")))
                .andExpect(jsonPath("$.price", is(19.99)));
    }

    @Test
    void getProductById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsInCategory() throws Exception {
        // Arrange
        List<ProductDto> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("Test")).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products/category/Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productName", is("Test Product")));
    }

    @Test
    void createProduct_ShouldCreateProductAndReturnMessage() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductDto.class))).thenReturn(testProductMessage);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.operationType", is("CREATE")))
                .andExpect(jsonPath("$.product.productName", is("Test Product")));
    }

    @Test
    void updateProduct_ShouldUpdateProductAndReturnMessage() throws Exception {
        // Arrange
        ProductMessage updateMessage = ProductMessage.builder()
                .product(testProduct)
                .operationType(OperationType.UPDATE)
                .build();
        when(productService.updateProduct(eq(1), any(ProductDto.class))).thenReturn(updateMessage);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType", is("UPDATE")))
                .andExpect(jsonPath("$.product.productName", is("Test Product")));
    }

    @Test
    void deleteProduct_WithExistingId_ShouldDeleteProductAndReturnMessage() throws Exception {
        // Arrange
        ProductMessage deleteMessage = ProductMessage.builder()
                .product(testProduct)
                .operationType(OperationType.DELETE)
                .build();
        when(productService.deleteProduct(1)).thenReturn(deleteMessage);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType", is("DELETE")))
                .andExpect(jsonPath("$.product.productName", is("Test Product")));
    }

    @Test
    void deleteProduct_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(999)).thenThrow(new IllegalArgumentException("Product not found"));

        // Act & Assert
        mockMvc.perform(delete("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}