package com.inventory.service;

import com.inventory.model.InventoryItem;
import com.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository repository;
    
    public InventoryItem addOrUpdateInventory(InventoryItem item) {
        Optional<InventoryItem> existingItem = repository.findByProductAndWarehouse(
            item.getProductId(), item.getWarehouseId()
        );
        
        if (existingItem.isPresent()) {
            InventoryItem existing = existingItem.get();
            item.setVersion(existing.getVersion() + 1);
            item.setCreatedAt(existing.getCreatedAt());
        }
        
        item.setUpdatedAt(LocalDateTime.now());
        return repository.save(item);
    }
    
    public Optional<InventoryItem> getInventory(String productId, String warehouseId) {
        return repository.findByProductAndWarehouse(productId, warehouseId);
    }
    
    public List<InventoryItem> getAllInventory() {
        return repository.findAll();
    }
    
    public InventoryItem updateQuantity(String productId, String warehouseId, int quantity) {
        Optional<InventoryItem> itemOpt = repository.findByProductAndWarehouse(productId, warehouseId);
        if (itemOpt.isPresent()) {
            InventoryItem item = itemOpt.get();
            item.setQuantity(quantity);
            item.setVersion(item.getVersion() + 1);
            item.setUpdatedAt(LocalDateTime.now());
            return repository.save(item);
        }
        throw new RuntimeException("Inventory item not found");
    }
    
    public boolean deleteInventory(String productId, String warehouseId) {
        if (repository.exists(productId, warehouseId)) {
            repository.delete(productId, warehouseId);
            return true;
        }
        return false;
    }
    
    public long getTotalItems() {
        return repository.count();
    }
    
    public List<InventoryItem> getLowStockItems() {
        return repository.findByStatus("LOW_STOCK");
    }
    
    public List<InventoryItem> getInventoryByProduct(String productId) {
        return repository.findByProductId(productId);
    }
    
    public List<InventoryItem> getInventoryByWarehouse(String warehouseId) {
        return repository.findByWarehouseId(warehouseId);
    }
}