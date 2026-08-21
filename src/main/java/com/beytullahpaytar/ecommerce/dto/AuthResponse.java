package com.beytullahpaytar.ecommerce.dto;

public record AuthResponse(
        String token,
        String email,
        String role,
        long expiresAt
) {
}
