package com.company.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SqsServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SqsService sqsService;

    private static final String PRODUCT_QUEUE_NAME = "test-product-queue";
    private static final String WIDGET_QUEUE_NAME = "test-widget-queue";
    private static final String AWS_REGION = "us-east-1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(sqsService, "productQueueName", PRODUCT_QUEUE_NAME);
        ReflectionTestUtils.setField(sqsService, "widgetQueueName", WIDGET_QUEUE_NAME);
        ReflectionTestUtils.setField(sqsService, "awsRegion", AWS_REGION);
    }

    @Test
    void queueExists_WhenQueueExists_ShouldReturnTrue() {
        // Arrange
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/" + PRODUCT_QUEUE_NAME;
        ListQueuesResponse listQueuesResponse = ListQueuesResponse.builder()
                .queueUrls(queueUrl)
                .build();
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(listQueuesResponse);

        // Act
        boolean result = sqsService.queueExists(PRODUCT_QUEUE_NAME);

        // Assert
        assertTrue(result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
    }

    @Test
    void queueExists_WhenQueueDoesNotExist_ShouldReturnFalse() {
        // Arrange
        ListQueuesResponse listQueuesResponse = ListQueuesResponse.builder()
                .queueUrls(new ArrayList<>())
                .build();
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(listQueuesResponse);

        // Act
        boolean result = sqsService.queueExists(PRODUCT_QUEUE_NAME);

        // Assert
        assertFalse(result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
    }

    @Test
    void queueExists_WhenExceptionOccurs_ShouldReturnFalse() {
        // Arrange
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenThrow(new RuntimeException("Test exception"));

        // Act
        boolean result = sqsService.queueExists(PRODUCT_QUEUE_NAME);

        // Assert
        assertFalse(result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
    }

    @Test
    void createQueueIfNotExists_WhenQueueDoesNotExist_ShouldCreateQueue() {
        // Arrange
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/" + PRODUCT_QUEUE_NAME;
        
        // Mock queue does not exist
        ListQueuesResponse listQueuesResponse = ListQueuesResponse.builder()
                .queueUrls(new ArrayList<>())
                .build();
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(listQueuesResponse);
        
        // Mock queue creation
        CreateQueueResponse createQueueResponse = CreateQueueResponse.builder()
                .queueUrl(queueUrl)
                .build();
        when(sqsClient.createQueue(any(CreateQueueRequest.class))).thenReturn(createQueueResponse);

        // Act
        String result = sqsService.createQueueIfNotExists(PRODUCT_QUEUE_NAME);

        // Assert
        assertEquals(queueUrl, result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
        verify(sqsClient).createQueue(any(CreateQueueRequest.class));
    }

    @Test
    void createQueueIfNotExists_WhenQueueExists_ShouldNotCreateQueue() {
        // Arrange
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/" + PRODUCT_QUEUE_NAME;
        
        // Mock queue exists
        ListQueuesResponse listQueuesResponse = ListQueuesResponse.builder()
                .queueUrls(queueUrl)
                .build();
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(listQueuesResponse);
        
        // Mock get queue URL
        GetQueueUrlResponse getQueueUrlResponse = GetQueueUrlResponse.builder()
                .queueUrl(queueUrl)
                .build();
        when(sqsClient.getQueueUrl(any(GetQueueUrlRequest.class))).thenReturn(getQueueUrlResponse);

        // Act
        String result = sqsService.createQueueIfNotExists(PRODUCT_QUEUE_NAME);

        // Assert
        assertEquals(queueUrl, result);
        verify(sqsClient).listQueues(any(ListQueuesRequest.class));
        verify(sqsClient).getQueueUrl(any(GetQueueUrlRequest.class));
        verify(sqsClient, never()).createQueue(any(CreateQueueRequest.class));
    }

    @Test
    void initializeQueues_ShouldCreateBothQueues() {
        // Arrange
        String productQueueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/" + PRODUCT_QUEUE_NAME;
        String widgetQueueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/" + WIDGET_QUEUE_NAME;
        
        // Mock queues don't exist
        ListQueuesResponse emptyQueuesResponse = ListQueuesResponse.builder()
                .queueUrls(new ArrayList<>())
                .build();
        when(sqsClient.listQueues(any(ListQueuesRequest.class))).thenReturn(emptyQueuesResponse);
        
        // Mock queue creation
        when(sqsClient.createQueue(any(CreateQueueRequest.class)))
                .thenReturn(
                    CreateQueueResponse.builder().queueUrl(productQueueUrl).build(),
                    CreateQueueResponse.builder().queueUrl(widgetQueueUrl).build()
                );

        // Act
        sqsService.initializeQueues();

        // Assert
        verify(sqsClient, times(2)).listQueues(any(ListQueuesRequest.class));
        verify(sqsClient, times(2)).createQueue(any(CreateQueueRequest.class));
    }

    @Test
    void getQueueUrl_ShouldReturnQueueUrl() {
        // Arrange
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/" + PRODUCT_QUEUE_NAME;
        GetQueueUrlResponse getQueueUrlResponse = GetQueueUrlResponse.builder()
                .queueUrl(queueUrl)
                .build();
        when(sqsClient.getQueueUrl(any(GetQueueUrlRequest.class))).thenReturn(getQueueUrlResponse);

        // Act
        String result = sqsService.getQueueUrl(PRODUCT_QUEUE_NAME);

        // Assert
        assertEquals(queueUrl, result);
        verify(sqsClient).getQueueUrl(any(GetQueueUrlRequest.class));
    }
}