package com.company.inventory.service;

import com.company.inventory.model.OperationType;
import com.company.inventory.model.Product;
import com.company.inventory.model.ProductDto;
import com.company.inventory.model.ProductMessage;
import com.company.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final SqsService sqsService;
    
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductDto> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::mapToDto);
    }
    
    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductDto> getProductByUpcCode(String upcCode) {
        return productRepository.findByUpcCode(upcCode)
                .map(this::mapToDto);
    }
    
    public List<ProductDto> getProductsByInStock(boolean inStock) {
        return productRepository.findByInStock(inStock).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public ProductMessage createProduct(ProductDto productDto) {
        ProductMessage message = ProductMessage.builder()
                .product(productDto)
                .operationType(OperationType.CREATE)
                .build();
        
        sqsService.sendMessageToQueue(message, sqsService.getProductQueueName());
        return message;
    }
    
    public ProductMessage updateProduct(Integer id, ProductDto productDto) {
        productDto.setProductId(id);
        
        ProductMessage message = ProductMessage.builder()
                .product(productDto)
                .operationType(OperationType.UPDATE)
                .build();
        
        sqsService.sendMessageToQueue(message, sqsService.getProductQueueName());
        return message;
    }
    
    public ProductMessage deleteProduct(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            ProductDto productDto = mapToDto(product.get());
            
            ProductMessage message = ProductMessage.builder()
                    .product(productDto)
                    .operationType(OperationType.DELETE)
                    .build();
            
            sqsService.sendMessageToQueue(message, sqsService.getProductQueueName());
            return message;
        }
        throw new IllegalArgumentException("Product not found with id: " + id);
    }
    
    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .size(product.getSize())
                .upcCode(product.getUpcCode())
                .amount(product.getAmount())
                .category(product.getCategory())
                .color(product.getColor())
                .weight(product.getWeight())
                .inStock(product.getInStock())
                .build();
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