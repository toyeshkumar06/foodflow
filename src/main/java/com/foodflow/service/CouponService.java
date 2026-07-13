package com.foodflow.service;

import com.foodflow.dto.CouponDtos.CouponResponse;
import com.foodflow.dto.CouponDtos.CreateCouponRequest;
import com.foodflow.entity.*;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.CouponRepository;
import com.foodflow.repository.CouponUsageRepository;
import com.foodflow.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final RestaurantRepository restaurantRepository;

    public CouponResponse createCoupon(CreateCouponRequest request) {
        if (couponRepository.findByCode(request.getCode()).isPresent()) {
            throw ApiException.conflict("A coupon with this code already exists");
        }

        Restaurant restaurant = null;
        if (request.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> ApiException.notFound("Restaurant not found"));
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minBillAmount(request.getMinBillAmount() != null ? request.getMinBillAmount() : BigDecimal.ZERO)
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .expiryDate(LocalDate.parse(request.getExpiryDate()))
                .usageLimit(request.getUsageLimit())
                .usageCount(0)
                .firstOrderOnly(request.isFirstOrderOnly())
                .restaurant(restaurant)
                .active(true)
                .build();

        couponRepository.save(coupon);
        return toResponse(coupon);
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Every rule the original spec asked for, checked in order, each with its own clear error.
    public BigDecimal validateAndCalculateDiscount(String code, Restaurant restaurant,
                                                     BigDecimal itemsTotal, boolean isFirstOrderForCustomer) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> ApiException.notFound("Invalid coupon code"));

        if (!coupon.isActive()) {
            throw ApiException.badRequest("This coupon is no longer active");
        }
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw ApiException.badRequest("This coupon has expired");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw ApiException.conflict("This coupon has reached its usage limit");
        }
        if (coupon.isFirstOrderOnly() && !isFirstOrderForCustomer) {
            throw ApiException.badRequest("This coupon is valid only on your first order");
        }
        if (coupon.getRestaurant() != null && !coupon.getRestaurant().getId().equals(restaurant.getId())) {
            throw ApiException.badRequest("This coupon is not valid for this restaurant");
        }
        if (itemsTotal.compareTo(coupon.getMinBillAmount()) < 0) {
            throw ApiException.badRequest("Minimum bill amount for this coupon is " + coupon.getMinBillAmount());
        }

        BigDecimal discount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = itemsTotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else {
            discount = coupon.getDiscountValue();
        }

        if (discount.compareTo(itemsTotal) > 0) {
            discount = itemsTotal;
        }

        return discount;
    }

    public void recordUsage(String code, User customer, Order order) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> ApiException.notFound("Invalid coupon code"));

        CouponUsage usage = CouponUsage.builder().coupon(coupon).user(customer).order(order).build();
        couponUsageRepository.save(usage);

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);
    }

    private CouponResponse toResponse(Coupon c) {
        return new CouponResponse(
                c.getId(), c.getCode(), c.getDescription(), c.getDiscountType(), c.getDiscountValue(),
                c.getMinBillAmount(), c.getMaxDiscountAmount(), c.getExpiryDate(), c.getUsageLimit(),
                c.getUsageCount(), c.isFirstOrderOnly(),
                c.getRestaurant() != null ? c.getRestaurant().getId() : null, c.isActive()
        );
    }
}