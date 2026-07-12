package com.foodflow.repository;

import com.foodflow.entity.DeliveryAgentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAgentProfileRepository extends JpaRepository<DeliveryAgentProfile, Long> {
    Optional<DeliveryAgentProfile> findByUserId(Long userId);
    List<DeliveryAgentProfile> findByOnlineTrueAndBusyFalse();
    long countByOnlineTrue();
}