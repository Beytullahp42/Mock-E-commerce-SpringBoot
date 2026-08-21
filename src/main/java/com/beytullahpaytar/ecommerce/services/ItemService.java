package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.dto.ItemDto;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.repository.CartItemRepository;
import com.beytullahpaytar.ecommerce.repository.ItemRepository;
import com.beytullahpaytar.ecommerce.repository.OrderItemRepository;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Setter
    Path path = Paths.get("upload-dir");

    public ItemService(ItemRepository itemRepository,
                       CartItemRepository cartItemRepository,
                       OrderItemRepository orderItemRepository) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public ResponseEntity<String> addItem(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPrice(dto.price());
        item.setImageUrl(handleImageUpload(dto.imageUrl()));
        itemRepository.save(item);
        return ResponseEntity.ok("Item added successfully");
    }

    @Transactional
    public ResponseEntity<String> updateItem(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        String oldImage = item.getImageUrl();
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPrice(dto.price());

        if (!Objects.equals(dto.imageUrl(), oldImage)) {
            item.setImageUrl(handleImageUpload(dto.imageUrl()));
        }

        itemRepository.save(item);
        if (!Objects.equals(item.getImageUrl(), oldImage)) {
            cleanupImageIfUnused(oldImage);
        }
        return ResponseEntity.ok("Item updated successfully");
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item requireItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = requireItem(id);
        cartItemRepository.deleteAllByItemId(id);
        itemRepository.delete(item);
        itemRepository.flush();
        cleanupImageIfUnused(item.getImageUrl());
    }

    public String handleImageUpload(String filename) {
        String newFilename = filename.replaceFirst("tempFile", "");
        Path filePath = path.resolve(filename);
        Path newFilePath = path.resolve(newFilename);

        try {
            Files.copy(filePath, newFilePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to rename file: " + e.getMessage());
        }
        return newFilename;
    }

    public void handleImageDelete(String filename) {
        Path filePath = path.resolve(filename);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    private void cleanupImageIfUnused(String filename) {
        if (!orderItemRepository.existsByImageUrl(filename)) {
            handleImageDelete(filename);
        }
    }
}
