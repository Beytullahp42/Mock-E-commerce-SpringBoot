package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByImageUrl(String imageUrl);
}
