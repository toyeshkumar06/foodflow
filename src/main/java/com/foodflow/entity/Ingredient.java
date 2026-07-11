package com.foodflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Cheese", "Tomato"

    @Column(nullable = false)
    private String unit; // e.g. "grams", "pieces", "ml" — just a label, not enforced

    @Column(nullable = false)
    @Builder.Default
    private Double quantityAvailable = 0.0; // how much stock the restaurant currently has

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}