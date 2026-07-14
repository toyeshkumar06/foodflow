package com.foodflow.repository;

import com.foodflow.entity.Rating;
import com.foodflow.entity.RatingTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByTargetTypeAndTargetId(RatingTargetType targetType, Long targetId);

    boolean existsByOrderIdAndTargetTypeAndTargetId(Long orderId, RatingTargetType targetType, Long targetId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.targetType = :targetType AND r.targetId = :targetId")
    Double findAverageStars(RatingTargetType targetType, Long targetId);
}