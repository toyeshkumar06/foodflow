package com.foodflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Builder.Default
    private BigDecimal minBillAmount = BigDecimal.ZERO;

    private BigDecimal maxDiscountAmount; // only meaningful for PERCENTAGE type

    @Column(nullable = false)
    private LocalDate expiryDate;

    private Integer usageLimit; // null = unlimited

    @Builder.Default
    private Integer usageCount = 0;

    @Builder.Default
    private boolean firstOrderOnly = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant; // null = valid at every restaurant

    @Builder.Default
    private boolean active = true;
}