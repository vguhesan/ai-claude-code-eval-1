package com.company.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
    "aws.access-key=test",
    "aws.secret-key=test",
    "app.sqs.polling.enabled=false"
})
class InventoryApplicationTests {

    @Autowired
    private ApplicationContext context;
    
    @MockBean
    private SqsClient sqsClient;
    
    @Test
    void contextLoads() {
        // Mock SQS client responses for queue check and creation
        when(sqsClient.listQueues(any(ListQueuesRequest.class)))
                .thenReturn(ListQueuesResponse.builder().build());
                
        when(sqsClient.createQueue(any(CreateQueueRequest.class)))
                .thenReturn(CreateQueueResponse.builder()
                        .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/test-queue")
                        .build());
                
        when(sqsClient.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder()
                        .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/test-queue")
                        .build());
                
        assertNotNull(context);
    }
}