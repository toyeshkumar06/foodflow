package com.foodflow.service;

import com.foodflow.entity.Order;
import com.foodflow.entity.OrderStatus;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests the order-cancellation business rule directly, without needing a running
// server or database — this is what "unit test" means: isolate ONE piece of logic.
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartService cartService;
    @Mock private AddressService addressService;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private DeliveryAssignmentService deliveryAssignmentService;
    @Mock private DeliveryAgentProfileRepository deliveryAgentProfileRepository;
    @Mock private EtaService etaService;
    @Mock private SurgePricingService surgePricingService;
    @Mock private CouponService couponService;
    @Mock private PaymentService paymentService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void cancelOrder_throwsWhenAlreadyPreparing() {
        User customer = User.builder().id(1L).build();
        Order order = Order.builder().id(10L).customer(customer).status(OrderStatus.PREPARING).build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        ApiException ex = assertThrows(ApiException.class, () -> orderService.cancelOrder(10L, customer));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void cancelOrder_succeedsWhenPlaced_andTriggersRefundCheck() {
        User customer = User.builder().id(1L).build();
        Order order = Order.builder().id(11L).customer(customer).status(OrderStatus.PLACED).build();
        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(11L, customer);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(paymentService).refundIfPaid(11L);
    }
}