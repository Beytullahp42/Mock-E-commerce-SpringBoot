package com.beytullahpaytar.ecommerce.controller;

import com.beytullahpaytar.ecommerce.auth.AccountDetails;
import com.beytullahpaytar.ecommerce.dto.CartItemDto;
import com.beytullahpaytar.ecommerce.models.Cart;
import com.beytullahpaytar.ecommerce.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal AccountDetails account) {
        return ResponseEntity.ok(cartService.getCart(account));
    }

    @PostMapping
    public ResponseEntity<String> addItem(@AuthenticationPrincipal AccountDetails account,
                                          @Valid @RequestBody CartItemDto dto) {
        cartService.addItemToCart(account, dto);
        return ResponseEntity.ok("Item added to cart");
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal AccountDetails account) {
        cartService.clearCart(account);
        return ResponseEntity.ok("Cart cleared");
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeItem(@AuthenticationPrincipal AccountDetails account,
                                             @PathVariable Long cartItemId) {
        cartService.removeItemFromCart(account, cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }
}
