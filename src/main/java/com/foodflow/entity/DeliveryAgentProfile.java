package com.foodflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_agent_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryAgentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    private boolean online = false;

    @Builder.Default
    private boolean busy = false;

    private Double currentLatitude;
    private Double currentLongitude;

    @Builder.Default
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Builder.Default
    private Double averageRating = 0.0;
}