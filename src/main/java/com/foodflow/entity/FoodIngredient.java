package com.foodflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_ingredients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantityRequired; // how much of this ingredient ONE unit of the food item consumes
}