package com.company.inventory.repository;

import com.company.inventory.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InventoryViewRepository extends JpaRepository<InventoryItem, String> {
    
    List<InventoryItem> findByItemType(String itemType);
    
    List<InventoryItem> findByCategory(String category);
    
    List<InventoryItem> findByAvailable(boolean available);
    
    List<InventoryItem> findByPriceLessThanEqual(BigDecimal maxPrice);
}