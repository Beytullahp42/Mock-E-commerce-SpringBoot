package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.auth.AccountDetails;
import com.beytullahpaytar.ecommerce.dto.OrderDto;
import com.beytullahpaytar.ecommerce.models.*;
import com.beytullahpaytar.ecommerce.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        AccountRepository accountRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order createOrder(AccountDetails accountDetails, OrderDto orderDto) {
        Account account = accountRepository.findById(accountDetails.getAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
        Cart cart = cartService.getCart(accountDetails);
        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = new Order();
        order.setAccount(account);
        order.setName(orderDto.name());
        order.setSurname(orderDto.surname());
        order.setAddress(orderDto.address());
        order.setEmail(account.getEmail());
        order.setPhoneNumber(orderDto.phoneNumber());
        order.setOrderStatus("PENDING");

        double total = 0.0;
        List<OrderItem> snapshots = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            Item item = cartItem.getItem();
            OrderItem snapshot = new OrderItem();
            snapshot.setName(item.getName());
            snapshot.setDescription(item.getDescription());
            snapshot.setUnitPrice(item.getPrice());
            snapshot.setQuantity(cartItem.getQuantity());
            snapshot.setImageUrl(item.getImageUrl());
            snapshot.setOrder(order);
            snapshots.add(snapshot);
            total += item.getPrice() * cartItem.getQuantity();
        }

        order.setOrderItems(snapshots);
        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        cartService.clearCart(accountDetails);
        return saved;
    }

    public List<Order> getOrders(AccountDetails accountDetails) {
        return orderRepository.findAllByAccountIdOrderByIdDesc(accountDetails.getAccountId());
    }

    public Order getOrder(AccountDetails accountDetails, Long id) {
        return orderRepository.findByIdAndAccountId(id, accountDetails.getAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    public Order getAnyOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = getAnyOrder(id);
        order.setOrderStatus(status);
        orderRepository.save(order);
    }
}
