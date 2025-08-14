package com.company.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsService {
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    
    @Value("${aws.sqs.product.queue-name}")
    private String productQueueName;
    
    @Value("${aws.sqs.widget.queue-name}")
    private String widgetQueueName;

    @Value("${aws.region}")
    private String awsRegion;
    
    public <T> SendMessageResponse sendMessageToQueue(T message, String queueName) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            String queueUrl = getQueueUrl(queueName);
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();
            
            SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);
            log.info("Message sent to queue {}, message ID: {}", queueName, response.messageId());
            return response;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message: {}", e.getMessage());
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
    
    public String getQueueUrl(String queueName) {
        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
        
        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest);
        return getQueueUrlResponse.queueUrl();
    }
    
    public boolean queueExists(String queueName) {
        try {
            ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder()
                    .queueNamePrefix(queueName)
                    .build();
            
            ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
            List<String> queueUrls = listQueuesResponse.queueUrls();
            
            for (String url : queueUrls) {
                if (url.endsWith("/" + queueName)) {
                    log.info("Queue {} exists", queueName);
                    return true;
                }
            }
            
            log.info("Queue {} does not exist", queueName);
            return false;
        } catch (Exception e) {
            log.error("Error checking if queue {} exists: {}", queueName, e.getMessage());
            return false;
        }
    }
    
    public String getProductQueueName() {
        return productQueueName;
    }
    
    public String getWidgetQueueName() {
        return widgetQueueName;
    }
    
    /**
     * Creates an SQS queue with the specified name if it doesn't already exist
     * 
     * @param queueName the name of the queue to create
     * @return the URL of the created or existing queue
     */
    public String createQueueIfNotExists(String queueName) {
        try {
            if (!queueExists(queueName)) {
                log.info("Creating queue: {}", queueName);
                
                // Define queue attributes
                CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                        .queueName(queueName)
                        .attributes(Map.of(
                            QueueAttributeName.VISIBILITY_TIMEOUT, "30",
                            QueueAttributeName.MESSAGE_RETENTION_PERIOD, "345600"  // 4 days
                        ))
                        .build();
                
                CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
                log.info("Successfully created queue: {}, URL: {}", queueName, createQueueResponse.queueUrl());
                return createQueueResponse.queueUrl();
            } else {
                log.info("Queue {} already exists, skipping creation", queueName);
                return getQueueUrl(queueName);
            }
        } catch (Exception e) {
            log.error("Error creating queue {}: {}", queueName, e.getMessage());
            throw new RuntimeException("Failed to create queue: " + queueName, e);
        }
    }
    
    /**
     * Initializes the SQS queues for products and widgets
     */
    @PostConstruct
    public void initializeQueues() {
        try {
            log.info("Initializing SQS queues");
            createQueueIfNotExists(productQueueName);
            createQueueIfNotExists(widgetQueueName);
            log.info("SQS queue initialization completed");
        } catch (Exception e) {
            log.error("Error initializing SQS queues: {}", e.getMessage());
        }
    }
}