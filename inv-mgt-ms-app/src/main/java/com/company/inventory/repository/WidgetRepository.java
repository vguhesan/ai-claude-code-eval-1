package com.company.inventory.repository;

import com.company.inventory.model.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, String> {
    
    List<Widget> findByCategory(String category);
    
    Optional<Widget> findByUpcCode(String upcCode);
    
    List<Widget> findByIsAvailable(boolean isAvailable);
}