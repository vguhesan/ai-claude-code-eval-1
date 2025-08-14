package com.company.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.inventory.model.OperationType;
import com.company.inventory.model.Product;
import com.company.inventory.model.ProductDto;
import com.company.inventory.model.ProductMessage;
import com.company.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductConsumerService {

    private final SqsClient sqsClient;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private final SqsService sqsService;

    @Value("${aws.sqs.product.queue-name}")
    private String queueName;

    @Value("${app.sqs.polling.enabled:true}")
    private boolean pollingEnabled;

    @Scheduled(fixedDelayString = "${app.sqs.polling.product.fixed-delay:5000}")
    public void pollMessages() {
        if (!pollingEnabled) {
            return;
        }

        try {
            String queueUrl = sqsService.getQueueUrl(queueName);
            
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(10)
                    .build();

            ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
            List<Message> messages = receiveMessageResponse.messages();

            if (messages.isEmpty()) {
                log.debug("No messages received from {}", queueName);
                return;
            }

            log.info("Received {} messages from {}", messages.size(), queueName);
            
            for (Message message : messages) {
                try {
                    ProductMessage productMessage = objectMapper.readValue(message.body(), ProductMessage.class);
                    processMessage(productMessage);
                    
                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteMessageRequest);
                    
                } catch (JsonProcessingException e) {
                    log.error("Error deserializing product message: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Error processing product message: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error polling product messages: {}", e.getMessage());
        }
    }

    private void processMessage(ProductMessage productMessage) {
        if (productMessage == null || productMessage.getProduct() == null) {
            log.error("Invalid product message received");
            return;
        }

        ProductDto productDto = productMessage.getProduct();
        OperationType operationType = productMessage.getOperationType();

        log.info("Processing {} operation for product: {}", operationType, productDto.getProductId());

        try {
            switch (operationType) {
                case CREATE:
                    createProduct(productDto);
                    break;
                case UPDATE:
                    updateProduct(productDto);
                    break;
                case DELETE:
                    deleteProduct(productDto);
                    break;
                default:
                    log.warn("Unknown operation type: {}", operationType);
            }
        } catch (Exception e) {
            log.error("Error processing {} operation for product {}: {}", 
                     operationType, productDto.getProductId(), e.getMessage());
        }
    }

    private void createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        productRepository.save(product);
        log.info("Product created: {}", productDto.getProductId());
    }

    private void updateProduct(ProductDto productDto) {
        if (productRepository.existsById(productDto.getProductId())) {
            Product product = mapToEntity(productDto);
            productRepository.save(product);
            log.info("Product updated: {}", productDto.getProductId());
        } else {
            log.warn("Cannot update non-existent product: {}", productDto.getProductId());
        }
    }

    private void deleteProduct(ProductDto productDto) {
        productRepository.deleteById(productDto.getProductId());
        log.info("Product deleted: {}", productDto.getProductId());
    }

    private Product mapToEntity(ProductDto productDto) {
        return Product.builder()
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .price(productDto.getPrice())
                .size(productDto.getSize())
                .upcCode(productDto.getUpcCode())
                .amount(productDto.getAmount())
                .category(productDto.getCategory())
                .color(productDto.getColor())
                .weight(productDto.getWeight())
                .inStock(productDto.getInStock())
                .build();
    }
}