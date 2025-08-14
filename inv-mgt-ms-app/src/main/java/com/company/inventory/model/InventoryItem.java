package com.company.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "inventory_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Subselect("SELECT * FROM inventory_view")
public class InventoryItem {
    
    @Column(name = "item_type")
    private String itemType;
    
    @Id
    @Column(name = "item_id")
    private String itemId;
    
    @Column(name = "item_name")
    private String itemName;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "amount")
    private Integer amount;
    
    @Column(name = "available")
    private Boolean available;
}