package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.ItemDto;
import com.beytullahpaytar.ecommerce.models.Item;
import com.beytullahpaytar.ecommerce.services.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
public class AdminItemController {
    private final ItemService itemService;

    public AdminItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.requireItem(id));
    }

    @PostMapping
    public ResponseEntity<String> addItem(@Valid @RequestBody ItemDto dto) {
        return itemService.addItem(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDto dto) {
        return itemService.updateItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
