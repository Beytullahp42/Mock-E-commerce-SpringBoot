package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.auth.AccountDetails;
import com.beytullahpaytar.ecommerce.dto.OrderDto;
import com.beytullahpaytar.ecommerce.models.Order;
import com.beytullahpaytar.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal AccountDetails account,
                                             @Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.createOrder(account, orderDto));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@AuthenticationPrincipal AccountDetails account) {
        return ResponseEntity.ok(orderService.getOrders(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@AuthenticationPrincipal AccountDetails account, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(account, id));
    }
}
