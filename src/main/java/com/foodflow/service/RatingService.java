package com.foodflow.service;

import com.foodflow.dto.CollectionDtos.LikedDishResponse;
import com.foodflow.dto.RatingDtos.RateRequest;
import com.foodflow.dto.RatingDtos.RatingResponse;
import com.foodflow.entity.*;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final DeliveryAgentProfileRepository deliveryAgentProfileRepository;

    public RatingResponse rateRestaurant(Long orderId, RateRequest request, User customer) {
        Order order = getDeliveredOrderOrThrow(orderId, customer);
        Long restaurantId = order.getRestaurant().getId();

        Rating rating = saveRating(order, customer, RatingTargetType.RESTAURANT, restaurantId, request);

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        restaurant.setAverageRating(recalculateAverage(RatingTargetType.RESTAURANT, restaurantId));
        restaurantRepository.save(restaurant);

        return toResponse(rating, customer);
    }

    public RatingResponse rateFoodItem(Long orderId, Long foodItemId, RateRequest request, User customer) {
        Order order = getDeliveredOrderOrThrow(orderId, customer);

        Rating rating = saveRating(order, customer, RatingTargetType.FOOD_ITEM, foodItemId, request);

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> ApiException.notFound("Food item not found"));
        foodItem.setAverageRating(recalculateAverage(RatingTargetType.FOOD_ITEM, foodItemId));
        foodItemRepository.save(foodItem);

        return toResponse(rating, customer);
    }

    public RatingResponse rateDeliveryAgent(Long orderId, RateRequest request, User customer) {
        Order order = getDeliveredOrderOrThrow(orderId, customer);

        if (order.getDeliveryAgent() == null) {
            throw ApiException.badRequest("This order had no delivery agent assigned");
        }
        Long agentUserId = order.getDeliveryAgent().getId();

        Rating rating = saveRating(order, customer, RatingTargetType.DELIVERY_AGENT, agentUserId, request);

        deliveryAgentProfileRepository.findByUserId(agentUserId).ifPresent(profile -> {
            profile.setAverageRating(recalculateAverage(RatingTargetType.DELIVERY_AGENT, agentUserId));
            deliveryAgentProfileRepository.save(profile);
        });

        return toResponse(rating, customer);
    }

    // Dishes the customer rated 4 stars or higher — treated as their "liked" list
    public List<LikedDishResponse> getLikedDishes(User customer) {
        List<Rating> likedRatings = ratingRepository.findByCustomerIdAndTargetTypeAndStarsGreaterThanEqual(
                customer.getId(), RatingTargetType.FOOD_ITEM, 4);

        List<LikedDishResponse> result = new ArrayList<>();
        for (Rating r : likedRatings) {
            foodItemRepository.findById(r.getTargetId()).ifPresent(item ->
                    result.add(new LikedDishResponse(
                            item.getId(), item.getName(), item.getImageUrl(), item.getPrice(),
                            item.getRestaurant().getName(), item.getRestaurant().getId(), r.getStars()
                    )));
        }
        return result;
    }

    private Rating saveRating(Order order, User customer, RatingTargetType type, Long targetId, RateRequest request) {
        if (ratingRepository.existsByOrderIdAndTargetTypeAndTargetId(order.getId(), type, targetId)) {
            throw ApiException.conflict("You have already rated this for this order");
        }

        Rating rating = Rating.builder()
                .order(order)
                .customer(customer)
                .targetType(type)
                .targetId(targetId)
                .stars(request.getStars())
                .reviewText(request.getReviewText())
                .build();

        return ratingRepository.save(rating);
    }

    private Double recalculateAverage(RatingTargetType type, Long targetId) {
        Double avg = ratingRepository.findAverageStars(type, targetId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    private Order getDeliveredOrderOrThrow(Long orderId, User customer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw ApiException.forbidden("This is not your order");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw ApiException.badRequest("You can only rate an order after it has been delivered");
        }
        return order;
    }

    private RatingResponse toResponse(Rating r, User customer) {
        return new RatingResponse(r.getId(), r.getTargetType(), r.getTargetId(), r.getStars(),
                r.getReviewText(), customer.getName(), r.getCreatedAt());
    }
}