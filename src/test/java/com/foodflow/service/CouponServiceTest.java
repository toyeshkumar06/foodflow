package com.foodflow.service;

import com.foodflow.entity.*;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.CouponRepository;
import com.foodflow.repository.CouponUsageRepository;
import com.foodflow.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock private CouponRepository couponRepository;
    @Mock private CouponUsageRepository couponUsageRepository;
    @Mock private RestaurantRepository restaurantRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void percentageDiscount_isCappedAtMaxDiscountAmount() {
        Coupon coupon = Coupon.builder()
                .code("SAVE20").discountType(DiscountType.PERCENTAGE).discountValue(BigDecimal.valueOf(20))
                .maxDiscountAmount(BigDecimal.valueOf(100)).minBillAmount(BigDecimal.valueOf(200))
                .expiryDate(LocalDate.now().plusDays(30)).usageCount(0).firstOrderOnly(false).active(true)
                .build();
        when(couponRepository.findByCode("SAVE20")).thenReturn(Optional.of(coupon));

        Restaurant restaurant = Restaurant.builder().id(1L).build();

        BigDecimal discount = couponService.validateAndCalculateDiscount("SAVE20", restaurant, BigDecimal.valueOf(598), false);

        assertEquals(0, discount.compareTo(BigDecimal.valueOf(100)));
    }

    @Test
    void expiredCoupon_throwsBadRequest() {
        Coupon expired = Coupon.builder()
                .code("OLD").discountType(DiscountType.FLAT).discountValue(BigDecimal.valueOf(50))
                .minBillAmount(BigDecimal.ZERO).expiryDate(LocalDate.now().minusDays(1))
                .usageCount(0).firstOrderOnly(false).active(true)
                .build();
        when(couponRepository.findByCode("OLD")).thenReturn(Optional.of(expired));

        Restaurant restaurant = Restaurant.builder().id(1L).build();

        assertThrows(ApiException.class, () ->
                couponService.validateAndCalculateDiscount("OLD", restaurant, BigDecimal.valueOf(500), false));
    }
}