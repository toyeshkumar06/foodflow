package com.foodflow.service;

import com.foodflow.dto.PaymentDtos.InitiatePaymentRequest;
import com.foodflow.dto.PaymentDtos.PaymentResponse;
import com.foodflow.entity.*;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.OrderRepository;
import com.foodflow.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponse initiatePayment(Long orderId, InitiatePaymentRequest request, User customer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw ApiException.forbidden("This is not your order");
        }
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw ApiException.conflict("Payment has already been initiated for this order");
        }

        // Simulated: COD stays PENDING until delivery; everything else is instant SUCCESS.
        // A real gateway (Razorpay/Stripe) would replace this block later.
        PaymentStatus status = (request.getMethod() == PaymentMethod.CASH_ON_DELIVERY)
                ? PaymentStatus.PENDING
                : PaymentStatus.SUCCESS;

        Payment payment = Payment.builder()
                .order(order)
                .method(request.getMethod())
                .status(status)
                .amount(order.getGrandTotal())
                .transactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .build();

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    public PaymentResponse getPaymentForOrder(Long orderId, User customer) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> ApiException.notFound("No payment found for this order"));

        if (!payment.getOrder().getCustomer().getId().equals(customer.getId())) {
            throw ApiException.forbidden("This is not your order");
        }
        return toResponse(payment);
    }

    // Called internally by OrderService when an order gets cancelled
    public void refundIfPaid(Long orderId) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        });
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrder().getId(), p.getMethod(), p.getStatus(),
                p.getAmount(), p.getTransactionRef(), p.getCreatedAt());
    }
}