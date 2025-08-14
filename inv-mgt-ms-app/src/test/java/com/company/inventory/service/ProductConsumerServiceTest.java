package com.company.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.inventory.model.OperationType;
import com.company.inventory.model.Product;
import com.company.inventory.model.ProductDto;
import com.company.inventory.model.ProductMessage;
import com.company.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductConsumerServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SqsService sqsService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ProductConsumerService productConsumerService;

    private ProductDto testProductDto;
    private ProductMessage testProductMessage;
    private String testProductMessageJson;
    private String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);

        // Configure service properties
        ReflectionTestUtils.setField(productConsumerService, "queueName", "test-queue");
        ReflectionTestUtils.setField(productConsumerService, "pollingEnabled", true);

        // Setup test data
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

        testProductMessage = ProductMessage.builder()
                .product(testProductDto)
                .operationType(OperationType.CREATE)
                .build();

        testProductMessageJson = objectMapper.writeValueAsString(testProductMessage);

        // Configure mocks
        when(sqsService.getQueueUrl(anyString())).thenReturn(queueUrl);
    }

    @Test
    void pollMessages_WhenNoMessages_ShouldDoNothing() {
        // Arrange
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(new ArrayList<>())
                        .build());

        // Act
        productConsumerService.pollMessages();

        // Assert
        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
        verify(productRepository, never()).save(any(Product.class));
        verify(productRepository, never()).deleteById(anyInt());
    }

    @Test
    void pollMessages_WithCreateMessage_ShouldCreateProduct() {
        // Arrange
        Message message = Message.builder()
                .body(testProductMessageJson)
                .messageId("test-message-id")
                .receiptHandle("test-receipt-handle")
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(Collections.singletonList(message))
                        .build());

        // Act
        productConsumerService.pollMessages();

        // Assert
        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
        
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        
        Product savedProduct = productCaptor.getValue();
        assertEquals(testProductDto.getProductId(), savedProduct.getProductId());
        assertEquals(testProductDto.getProductName(), savedProduct.getProductName());
    }

    @Test
    void pollMessages_WithUpdateMessage_ShouldUpdateProduct() throws JsonProcessingException {
        // Arrange
        ProductMessage updateMessage = ProductMessage.builder()
                .product(testProductDto)
                .operationType(OperationType.UPDATE)
                .build();
        String updateMessageJson = objectMapper.writeValueAsString(updateMessage);

        Message message = Message.builder()
                .body(updateMessageJson)
                .messageId("test-message-id")
                .receiptHandle("test-receipt-handle")
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(Collections.singletonList(message))
                        .build());

        when(productRepository.existsById(testProductDto.getProductId())).thenReturn(true);

        // Act
        productConsumerService.pollMessages();

        // Assert
        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
        
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        
        Product updatedProduct = productCaptor.getValue();
        assertEquals(testProductDto.getProductId(), updatedProduct.getProductId());
        assertEquals(testProductDto.getProductName(), updatedProduct.getProductName());
    }

    @Test
    void pollMessages_WithDeleteMessage_ShouldDeleteProduct() throws JsonProcessingException {
        // Arrange
        ProductMessage deleteMessage = ProductMessage.builder()
                .product(testProductDto)
                .operationType(OperationType.DELETE)
                .build();
        String deleteMessageJson = objectMapper.writeValueAsString(deleteMessage);

        Message message = Message.builder()
                .body(deleteMessageJson)
                .messageId("test-message-id")
                .receiptHandle("test-receipt-handle")
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(Collections.singletonList(message))
                        .build());

        // Act
        productConsumerService.pollMessages();

        // Assert
        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
        verify(productRepository, times(1)).deleteById(testProductDto.getProductId());
    }

    @Test
    void pollMessages_WithMultipleMessages_ShouldProcessAll() throws JsonProcessingException {
        // Arrange
        ProductMessage updateMessage = ProductMessage.builder()
                .product(testProductDto)
                .operationType(OperationType.UPDATE)
                .build();
        String updateMessageJson = objectMapper.writeValueAsString(updateMessage);

        ProductDto product2 = ProductDto.builder()
                .productId(2)
                .productName(testProductDto.getProductName())
                .price(testProductDto.getPrice())
                .size(testProductDto.getSize())
                .upcCode(testProductDto.getUpcCode())
                .amount(testProductDto.getAmount())
                .category(testProductDto.getCategory())
                .color(testProductDto.getColor())
                .weight(testProductDto.getWeight())
                .inStock(testProductDto.getInStock())
                .build();
        ProductMessage deleteMessage = ProductMessage.builder()
                .product(product2)
                .operationType(OperationType.DELETE)
                .build();
        String deleteMessageJson = objectMapper.writeValueAsString(deleteMessage);

        Message message1 = Message.builder()
                .body(testProductMessageJson)
                .messageId("test-message-id-1")
                .receiptHandle("test-receipt-handle-1")
                .build();

        Message message2 = Message.builder()
                .body(updateMessageJson)
                .messageId("test-message-id-2")
                .receiptHandle("test-receipt-handle-2")
                .build();

        Message message3 = Message.builder()
                .body(deleteMessageJson)
                .messageId("test-message-id-3")
                .receiptHandle("test-receipt-handle-3")
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder()
                        .messages(Arrays.asList(message1, message2, message3))
                        .build());

        when(productRepository.existsById(testProductDto.getProductId())).thenReturn(true);

        // Act
        productConsumerService.pollMessages();

        // Assert
        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
        verify(sqsClient, times(3)).deleteMessage(any(DeleteMessageRequest.class));
        verify(productRepository, times(2)).save(any(Product.class));
        verify(productRepository, times(1)).deleteById(2);
    }
}