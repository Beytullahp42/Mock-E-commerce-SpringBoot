package com.beytullahpaytar.ecommerce.dto;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

public record CartItemDto(
        @NotNull
        Long itemId,
        @Positive
        int quantity
) {
}

