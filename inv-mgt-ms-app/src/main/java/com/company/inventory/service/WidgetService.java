package com.company.inventory.service;

import com.company.inventory.model.OperationType;
import com.company.inventory.model.Widget;
import com.company.inventory.model.WidgetDto;
import com.company.inventory.model.WidgetMessage;
import com.company.inventory.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WidgetService {
    
    private final WidgetRepository widgetRepository;
    private final SqsService sqsService;
    
    public List<WidgetDto> getAllWidgets() {
        return widgetRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<WidgetDto> getWidgetById(String id) {
        return widgetRepository.findById(id)
                .map(this::mapToDto);
    }
    
    public List<WidgetDto> getWidgetsByCategory(String category) {
        return widgetRepository.findByCategory(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<WidgetDto> getWidgetByUpcCode(String upcCode) {
        return widgetRepository.findByUpcCode(upcCode)
                .map(this::mapToDto);
    }
    
    public List<WidgetDto> getWidgetsByAvailability(boolean isAvailable) {
        return widgetRepository.findByIsAvailable(isAvailable).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public WidgetMessage createWidget(WidgetDto widgetDto) {
        WidgetMessage message = WidgetMessage.builder()
                .widget(widgetDto)
                .operationType(OperationType.CREATE)
                .build();
        
        sqsService.sendMessageToQueue(message, sqsService.getWidgetQueueName());
        return message;
    }
    
    public WidgetMessage updateWidget(String id, WidgetDto widgetDto) {
        widgetDto.setWidgetId(id);
        
        WidgetMessage message = WidgetMessage.builder()
                .widget(widgetDto)
                .operationType(OperationType.UPDATE)
                .build();
        
        sqsService.sendMessageToQueue(message, sqsService.getWidgetQueueName());
        return message;
    }
    
    public WidgetMessage deleteWidget(String id) {
        Optional<Widget> widget = widgetRepository.findById(id);
        if (widget.isPresent()) {
            WidgetDto widgetDto = mapToDto(widget.get());
            
            WidgetMessage message = WidgetMessage.builder()
                    .widget(widgetDto)
                    .operationType(OperationType.DELETE)
                    .build();
            
            sqsService.sendMessageToQueue(message, sqsService.getWidgetQueueName());
            return message;
        }
        throw new IllegalArgumentException("Widget not found with id: " + id);
    }
    
    private WidgetDto mapToDto(Widget widget) {
        return WidgetDto.builder()
                .widgetId(widget.getWidgetId())
                .widgetName(widget.getWidgetName())
                .price(widget.getPrice())
                .size(widget.getSize())
                .upcCode(widget.getUpcCode())
                .amount(widget.getAmount())
                .category(widget.getCategory())
                .material(widget.getMaterial())
                .weight(widget.getWeight())
                .isAvailable(widget.getIsAvailable())
                .build();
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