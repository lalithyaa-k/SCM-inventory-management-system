package com.inventory.service;

import com.inventory.model.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SyncService {
    
    @Autowired
    private InventoryService inventoryService;
    
    private final Random random = new Random();
    private final Map<String, Integer> syncHistory = new HashMap<>();
    
    public String syncWithWarehouse(String productId, String warehouseId) {
        try {
            int externalStock = random.nextInt(300) + 20;
            
            InventoryItem item = inventoryService.getInventory(productId, warehouseId).orElse(null);
            
            String result;
            if (item == null) {
                InventoryItem newItem = new InventoryItem(
                    productId, 
                    "Product " + productId,
                    warehouseId,
                    "Warehouse " + warehouseId,
                    externalStock,
                    10,
                    200
                );
                newItem.setLastSyncTime(LocalDateTime.now());
                inventoryService.addOrUpdateInventory(newItem);
                result = "✅ New inventory created from warehouse with stock: " + externalStock;
            } else {
                int oldStock = item.getQuantity();
                item.setQuantity(externalStock);
                item.setLastSyncTime(LocalDateTime.now());
                inventoryService.addOrUpdateInventory(item);
                result = String.format("🔄 Synced %s:%s from %d → %d", 
                    productId, warehouseId, oldStock, externalStock);
            }
            
            String key = productId + ":" + warehouseId;
            syncHistory.put(key, syncHistory.getOrDefault(key, 0) + 1);
            
            return result;
        } catch (Exception e) {
            return "❌ Sync failed: " + e.getMessage();
        }
    }
    
    public Map<String, Object> getSyncStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSyncs", syncHistory.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("uniqueItemsSynced", syncHistory.size());
        stats.put("lastSyncTime", LocalDateTime.now());
        return stats;
    }
}