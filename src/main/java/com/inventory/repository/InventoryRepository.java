package com.inventory.repository;

import com.inventory.model.InventoryItem;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InventoryRepository {
    private final Map<String, InventoryItem> database = new ConcurrentHashMap<>();

    private String generateKey(String productId, String warehouseId) {
        return productId + ":" + warehouseId;
    }

    public InventoryItem save(InventoryItem item) {
        String key = generateKey(item.getProductId(), item.getWarehouseId());
        database.put(key, item);
        return item;
    }

    public Optional<InventoryItem> findByProductAndWarehouse(String productId, String warehouseId) {
        return Optional.ofNullable(database.get(generateKey(productId, warehouseId)));
    }

    public List<InventoryItem> findAll() {
        return new ArrayList<>(database.values());
    }

    public List<InventoryItem> findByProductId(String productId) {
        return database.values().stream()
                .filter(item -> item.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    public List<InventoryItem> findByWarehouseId(String warehouseId) {
        return database.values().stream()
                .filter(item -> item.getWarehouseId().equals(warehouseId))
                .collect(Collectors.toList());
    }

    public List<InventoryItem> findByStatus(String status) {
        return database.values().stream()
                .filter(item -> item.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public void delete(String productId, String warehouseId) {
        String key = generateKey(productId, warehouseId);
        database.remove(key);
    }

    public boolean exists(String productId, String warehouseId) {
        return database.containsKey(generateKey(productId, warehouseId));
    }

    public long count() {
        return database.size();
    }
}