package com.beytullahpaytar.ecommerce.repository;

import com.beytullahpaytar.ecommerce.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByAccountId(Long accountId);
}
