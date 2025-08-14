package com.company.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.inventory.model.OperationType;
import com.company.inventory.model.Widget;
import com.company.inventory.model.WidgetDto;
import com.company.inventory.model.WidgetMessage;
import com.company.inventory.repository.WidgetRepository;
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
public class WidgetConsumerService {

    private final SqsClient sqsClient;
    private final WidgetRepository widgetRepository;
    private final ObjectMapper objectMapper;
    private final SqsService sqsService;

    @Value("${aws.sqs.widget.queue-name}")
    private String queueName;

    @Value("${app.sqs.polling.enabled:true}")
    private boolean pollingEnabled;

    @Scheduled(fixedDelayString = "${app.sqs.polling.widget.fixed-delay:5000}")
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
                    WidgetMessage widgetMessage = objectMapper.readValue(message.body(), WidgetMessage.class);
                    processMessage(widgetMessage);
                    
                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteMessageRequest);
                    
                } catch (JsonProcessingException e) {
                    log.error("Error deserializing widget message: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Error processing widget message: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error polling widget messages: {}", e.getMessage());
        }
    }

    private void processMessage(WidgetMessage widgetMessage) {
        if (widgetMessage == null || widgetMessage.getWidget() == null) {
            log.error("Invalid widget message received");
            return;
        }

        WidgetDto widgetDto = widgetMessage.getWidget();
        OperationType operationType = widgetMessage.getOperationType();

        log.info("Processing {} operation for widget: {}", operationType, widgetDto.getWidgetId());

        try {
            switch (operationType) {
                case CREATE:
                    createWidget(widgetDto);
                    break;
                case UPDATE:
                    updateWidget(widgetDto);
                    break;
                case DELETE:
                    deleteWidget(widgetDto);
                    break;
                default:
                    log.warn("Unknown operation type: {}", operationType);
            }
        } catch (Exception e) {
            log.error("Error processing {} operation for widget {}: {}", 
                     operationType, widgetDto.getWidgetId(), e.getMessage());
        }
    }

    private void createWidget(WidgetDto widgetDto) {
        Widget widget = mapToEntity(widgetDto);
        widgetRepository.save(widget);
        log.info("Widget created: {}", widgetDto.getWidgetId());
    }

    private void updateWidget(WidgetDto widgetDto) {
        if (widgetRepository.existsById(widgetDto.getWidgetId())) {
            Widget widget = mapToEntity(widgetDto);
            widgetRepository.save(widget);
            log.info("Widget updated: {}", widgetDto.getWidgetId());
        } else {
            log.warn("Cannot update non-existent widget: {}", widgetDto.getWidgetId());
        }
    }

    private void deleteWidget(WidgetDto widgetDto) {
        widgetRepository.deleteById(widgetDto.getWidgetId());
        log.info("Widget deleted: {}", widgetDto.getWidgetId());
    }

    private Widget mapToEntity(WidgetDto widgetDto) {
        return Widget.builder()
                .widgetId(widgetDto.getWidgetId())
                .widgetName(widgetDto.getWidgetName())
                .price(widgetDto.getPrice())
                .size(widgetDto.getSize())
                .upcCode(widgetDto.getUpcCode())
                .amount(widgetDto.getAmount())
                .category(widgetDto.getCategory())
                .material(widgetDto.getMaterial())
                .weight(widgetDto.getWeight())
                .isAvailable(widgetDto.getIsAvailable())
                .build();
    }
}