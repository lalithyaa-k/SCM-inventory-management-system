package com.inventory.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryItem {
    private String id;
    private String productId;
    private String productName;
    private String warehouseId;
    private String warehouseName;
    private int quantity;
    private int minThreshold;
    private int maxThreshold;
    private int version;
    private LocalDateTime lastSyncTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryItem() {
        this.id = UUID.randomUUID().toString();
        this.version = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "IN_STOCK";
    }

    public InventoryItem(String productId, String productName, String warehouseId, 
                         String warehouseName, int quantity, int minThreshold, int maxThreshold) {
        this();
        this.productId = productId;
        this.productName = productName;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        updateStatus();
    }

    public void updateStatus() {
        if (quantity <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (quantity <= minThreshold) {
            this.status = "LOW_STOCK";
        } else if (quantity >= maxThreshold) {
            this.status = "OVER_STOCK";
        } else {
            this.status = "IN_STOCK";
        }
    }

    // Getters
    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public int getQuantity() { return quantity; }
    public int getMinThreshold() { return minThreshold; }
    public int getMaxThreshold() { return maxThreshold; }
    public int getVersion() { return version; }
    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        updateStatus();
        this.updatedAt = LocalDateTime.now();
    }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
    public void setMaxThreshold(int maxThreshold) { this.maxThreshold = maxThreshold; }
    public void setVersion(int version) { this.version = version; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}