package com.inventory.controller;

import com.inventory.model.InventoryItem;
import com.inventory.service.InventoryService;
import com.inventory.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private SyncService syncService;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Inventory Management System");
        status.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }
    
    @GetMapping("/inventory/{productId}/{warehouseId}")
    public ResponseEntity<?> getInventory(
            @PathVariable String productId,
            @PathVariable String warehouseId) {
        Optional<InventoryItem> item = inventoryService.getInventory(productId, warehouseId);
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inventory item not found"));
    }
    
    @PostMapping("/inventory")
    public ResponseEntity<Map<String, Object>> addInventory(@RequestBody InventoryItem item) {
        InventoryItem saved = inventoryService.addOrUpdateInventory(item);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inventory saved successfully");
        response.put("item", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/inventory/{productId}/{warehouseId}/quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable String productId,
            @PathVariable String warehouseId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Quantity is required"));
            }
            InventoryItem updated = inventoryService.updateQuantity(productId, warehouseId, quantity);
            return ResponseEntity.ok(Map.of(
                "message", "Quantity updated successfully",
                "item", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/inventory/{productId}/{warehouseId}")
    public ResponseEntity<?> deleteInventory(
            @PathVariable String productId,
            @PathVariable String warehouseId) {
        boolean deleted = inventoryService.deleteInventory(productId, warehouseId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Inventory deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inventory item not found"));
    }
    
    @PostMapping("/sync/{productId}/{warehouseId}")
    public ResponseEntity<Map<String, String>> syncInventory(
            @PathVariable String productId,
            @PathVariable String warehouseId) {
        String result = syncService.syncWithWarehouse(productId, warehouseId);
        return ResponseEntity.ok(Map.of("message", result));
    }
    
    @GetMapping("/inventory/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", inventoryService.getTotalItems());
        stats.put("lowStockCount", inventoryService.getLowStockItems().size());
        stats.put("syncStats", syncService.getSyncStats());
        return ResponseEntity.ok(stats);
    }
}