package com.foodflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}