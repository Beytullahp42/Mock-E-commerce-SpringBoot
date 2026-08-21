package com.beytullahpaytar.ecommerce.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // Retained only so v2 can start against a school-version database volume.
    // Cart ownership now replaces the old completed-cart workflow.
    @Column(name = "is_completed", nullable = false)
    @JsonIgnore
    private Boolean legacyCompleted = false;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    @JsonIgnore
    private Account account;
}
