package com.company.inventory.service;

import com.company.inventory.model.OperationType;
import com.company.inventory.model.Product;
import com.company.inventory.model.ProductDto;
import com.company.inventory.model.ProductMessage;
import com.company.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SqsService sqsService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up test data
        testProduct = Product.builder()
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

        testProductDto = ProductDto.builder()
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
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        // Act
        List<ProductDto> result = productService.getAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testProductDto.getProductName(), result.get(0).getProductName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<ProductDto> result = productService.getProductById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProductDto.getProductName(), result.get().getProductName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void getProductById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<ProductDto> result = productService.getProductById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        // Arrange
        when(productRepository.findByCategory("Test")).thenReturn(Arrays.asList(testProduct));

        // Act
        List<ProductDto> result = productService.getProductsByCategory("Test");

        // Assert
        assertEquals(1, result.size());
        assertEquals(testProductDto.getProductName(), result.get(0).getProductName());
        verify(productRepository, times(1)).findByCategory("Test");
    }

    @Test
    void createProduct_ShouldSendMessageToQueue() {
        // Arrange
        SendMessageResponse mockResponse = SendMessageResponse.builder().messageId("test-message-id").build();
        when(sqsService.sendMessageToQueue(any(ProductMessage.class), anyString())).thenReturn(mockResponse);
        when(sqsService.getProductQueueName()).thenReturn("test-queue");

        // Act
        ProductMessage result = productService.createProduct(testProductDto);

        // Assert
        assertNotNull(result);
        assertEquals(OperationType.CREATE, result.getOperationType());
        assertEquals(testProductDto, result.getProduct());
        verify(sqsService, times(1)).sendMessageToQueue(any(ProductMessage.class), anyString());
    }

    @Test
    void updateProduct_ShouldSendMessageToQueue() {
        // Arrange
        SendMessageResponse mockResponse = SendMessageResponse.builder().messageId("test-message-id").build();
        when(sqsService.sendMessageToQueue(any(ProductMessage.class), anyString())).thenReturn(mockResponse);
        when(sqsService.getProductQueueName()).thenReturn("test-queue");

        // Act
        ProductMessage result = productService.updateProduct(1, testProductDto);

        // Assert
        assertNotNull(result);
        assertEquals(OperationType.UPDATE, result.getOperationType());
        assertEquals(testProductDto, result.getProduct());
        verify(sqsService, times(1)).sendMessageToQueue(any(ProductMessage.class), anyString());
    }

    @Test
    void deleteProduct_WithExistingProduct_ShouldSendMessageToQueue() {
        // Arrange
        SendMessageResponse mockResponse = SendMessageResponse.builder().messageId("test-message-id").build();
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(sqsService.sendMessageToQueue(any(ProductMessage.class), anyString())).thenReturn(mockResponse);
        when(sqsService.getProductQueueName()).thenReturn("test-queue");

        // Act
        ProductMessage result = productService.deleteProduct(1);

        // Assert
        assertNotNull(result);
        assertEquals(OperationType.DELETE, result.getOperationType());
        assertEquals(testProductDto.getProductId(), result.getProduct().getProductId());
        verify(sqsService, times(1)).sendMessageToQueue(any(ProductMessage.class), anyString());
    }

    @Test
    void deleteProduct_WithNonExistingProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(999));
        verify(sqsService, never()).sendMessageToQueue(any(ProductMessage.class), anyString());
    }
}