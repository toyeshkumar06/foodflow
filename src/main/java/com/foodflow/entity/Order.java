package com.foodflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;

    private String deliveryAddressLine;
    private String deliveryCity;
    private String deliveryPincode;

    @Column(nullable = false)
    private BigDecimal itemsTotal;

    @Column(nullable = false)
    private BigDecimal deliveryCharge;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String appliedCouponCode;

    @Column(nullable = false)
    private BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private Integer etaMinutes;
    private Double distanceKm;
    private BigDecimal surgeMultiplier;
    private BigDecimal agentEarning;

    @Builder.Default
    private boolean agentConfirmed = false;

    @ElementCollection
    @CollectionTable(name = "order_rejected_agents", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "agent_id")
    @Builder.Default
    private Set<Long> rejectedAgentIds = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}