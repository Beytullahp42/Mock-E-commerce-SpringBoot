package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByAccountIdOrderByIdDesc(Long accountId);

    Optional<Order> findByIdAndAccountId(Long id, Long accountId);

    List<Order> findAllByOrderByIdDesc();
}
