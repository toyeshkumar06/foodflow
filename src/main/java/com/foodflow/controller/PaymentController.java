package com.foodflow.controller;

import com.foodflow.dto.PaymentDtos.*;
import com.foodflow.entity.User;
import com.foodflow.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public PaymentResponse initiatePayment(@PathVariable Long orderId,
                                            @Valid @RequestBody InitiatePaymentRequest request,
                                            @AuthenticationPrincipal User customer) {
        return paymentService.initiatePayment(orderId, request, customer);
    }

    @GetMapping("/{orderId}")
    public PaymentResponse getPayment(@PathVariable Long orderId, @AuthenticationPrincipal User customer) {
        return paymentService.getPaymentForOrder(orderId, customer);
    }
}