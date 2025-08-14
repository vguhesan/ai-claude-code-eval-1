package com.company.inventory.repository;

import com.company.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    List<Product> findByCategory(String category);
    
    Optional<Product> findByUpcCode(String upcCode);
    
    List<Product> findByInStock(boolean inStock);
}