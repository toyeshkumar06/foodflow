package com.foodflow.controller;

import com.foodflow.dto.CouponDtos.*;
import com.foodflow.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public CouponResponse createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return couponService.createCoupon(request);
    }

    @GetMapping
    public List<CouponResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }
}