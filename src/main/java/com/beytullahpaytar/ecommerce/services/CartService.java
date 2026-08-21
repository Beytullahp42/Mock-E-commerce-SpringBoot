package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.auth.AccountDetails;
import com.beytullahpaytar.ecommerce.dto.CartItemDto;
import com.beytullahpaytar.ecommerce.models.*;
import com.beytullahpaytar.ecommerce.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final AccountRepository accountRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ItemRepository itemRepository,
                       AccountRepository accountRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Cart getCart(AccountDetails accountDetails) {
        return cartRepository.findByAccountId(accountDetails.getAccountId())
                .orElseGet(() -> createCart(accountDetails.getAccountId()));
    }

    @Transactional
    public void addItemToCart(AccountDetails accountDetails, CartItemDto cartItemDto) {
        Cart cart = getCart(accountDetails);
        Item item = itemRepository.findById(cartItemDto.itemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        CartItem cartItem = cartItemRepository.findByItemIdAndCartId(item.getId(), cart.getId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setCart(cart);
            cartItem.setQuantity(cartItemDto.quantity());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDto.quantity());
        }
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeItemFromCart(AccountDetails accountDetails, Long cartItemId) {
        Cart cart = getCart(accountDetails);
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId());
        if (cartItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(AccountDetails accountDetails) {
        Cart cart = getCart(accountDetails);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private Cart createCart(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
        Cart cart = new Cart();
        cart.setAccount(account);
        return cartRepository.save(cart);
    }
}
