package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.dto.UpdateOrderStatusDto;
import com.beytullahpaytar.ecommerce.models.Order;
import com.beytullahpaytar.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getAnyOrder(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,
                                               @Valid @RequestBody UpdateOrderStatusDto dto) {
        orderService.updateOrderStatus(id, dto.orderStatus());
        return ResponseEntity.ok("Order status updated successfully");
    }
}
