package com.foodflow.service;

import com.foodflow.dto.OrderDtos.*;
import com.foodflow.entity.*;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.CartItemRepository;
import com.foodflow.repository.DeliveryAgentProfileRepository;
import com.foodflow.repository.OrderItemRepository;
import com.foodflow.repository.OrderRepository;
import com.foodflow.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final AddressService addressService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryAssignmentService deliveryAssignmentService;
    private final DeliveryAgentProfileRepository deliveryAgentProfileRepository;
    private final EtaService etaService;
    private final SurgePricingService surgePricingService;
    private final CouponService couponService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    private static final BigDecimal BASE_DELIVERY_CHARGE = BigDecimal.valueOf(40);

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
            OrderStatus.PLACED, OrderStatus.ACCEPTED, OrderStatus.PREPARING,
            OrderStatus.READY_FOR_PICKUP, OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY
    );

    private static final Set<OrderStatus> OWNER_ALLOWED_TARGETS =
            EnumSet.of(OrderStatus.ACCEPTED, OrderStatus.REJECTED, OrderStatus.PREPARING,
                    OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED);

    private static final Set<OrderStatus> AGENT_ALLOWED_TARGETS =
            EnumSet.of(OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY, OrderStatus.DELIVERED);

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.PLACED, EnumSet.of(OrderStatus.ACCEPTED, OrderStatus.REJECTED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.ACCEPTED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.PREPARING, EnumSet.of(OrderStatus.READY_FOR_PICKUP));
        ALLOWED_TRANSITIONS.put(OrderStatus.READY_FOR_PICKUP, EnumSet.of(OrderStatus.PICKED_UP));
        ALLOWED_TRANSITIONS.put(OrderStatus.PICKED_UP, EnumSet.of(OrderStatus.ON_THE_WAY));
        ALLOWED_TRANSITIONS.put(OrderStatus.ON_THE_WAY, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.REJECTED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    public OrderResponse placeOrder(PlaceOrderRequest request, User customer) {
        List<CartItem> cartItems = cartItemRepository.findByCustomerId(customer.getId());
        if (cartItems.isEmpty()) {
            throw ApiException.badRequest("Your cart is empty");
        }

        Restaurant restaurant = cartItems.get(0).getRestaurant();
        if (restaurant.getStatus() != RestaurantStatus.OPEN) {
            throw ApiException.badRequest("Restaurant is currently not accepting orders");
        }

        Address address = addressService.getOwnedAddressOrThrow(request.getAddressId(), customer);

        if (restaurant.getLatitude() == null || restaurant.getLongitude() == null) {
            throw ApiException.badRequest("This restaurant hasn't set its location yet, so delivery can't be calculated");
        }
        if (address.getLatitude() == null || address.getLongitude() == null) {
            throw ApiException.badRequest("This address is missing location coordinates");
        }

        double distanceKm = GeoUtils.distanceKm(
                restaurant.getLatitude(), restaurant.getLongitude(),
                address.getLatitude(), address.getLongitude());

        int etaMinutes = etaService.calculateEtaMinutes(restaurant.getAvgPrepTimeMinutes(), distanceKm);

        long activeOrders = orderRepository.countByStatusIn(ACTIVE_STATUSES);
        long onlineAgents = deliveryAgentProfileRepository.countByOnlineTrue();
        BigDecimal surgeMultiplier = surgePricingService.calculateSurgeMultiplier(activeOrders, onlineAgents);

        BigDecimal itemsTotal = cartItems.stream()
                .map(i -> i.getFoodItem().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCode = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            boolean isFirstOrder = orderRepository.findByCustomerIdOrderByIdDesc(customer.getId()).isEmpty();
            discountAmount = couponService.validateAndCalculateDiscount(
                    request.getCouponCode(), restaurant, itemsTotal, isFirstOrder);
            appliedCode = request.getCouponCode().toUpperCase();
        }

        BigDecimal deliveryCharge = BASE_DELIVERY_CHARGE.multiply(surgeMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = itemsTotal.subtract(discountAmount).add(deliveryCharge);

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .deliveryAddressLine(address.getAddressLine())
                .deliveryCity(address.getCity())
                .deliveryPincode(address.getPincode())
                .itemsTotal(itemsTotal)
                .deliveryCharge(deliveryCharge)
                .discountAmount(discountAmount)
                .appliedCouponCode(appliedCode)
                .grandTotal(grandTotal)
                .status(OrderStatus.PLACED)
                .distanceKm(distanceKm)
                .etaMinutes(etaMinutes)
                .surgeMultiplier(surgeMultiplier)
                .agentConfirmed(false)
                .build();
        orderRepository.save(order);

        for (CartItem ci : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .foodItem(ci.getFoodItem())
                    .foodNameSnapshot(ci.getFoodItem().getName())
                    .priceAtOrderTime(ci.getFoodItem().getPrice())
                    .quantity(ci.getQuantity())
                    .build();
            orderItemRepository.save(orderItem);
        }

        if (appliedCode != null) {
            couponService.recordUsage(appliedCode, customer, order);
        }

        cartService.clearCart(customer);

        notificationService.notify(customer, "Order Confirmed",
                "Your order #" + order.getId() + " from " + restaurant.getName() + " has been placed.",
                NotificationType.ORDER_CONFIRMED);

        return toResponse(order);
    }

    public void cancelOrder(Long orderId, User customer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw ApiException.forbidden("This is not your order");
        }
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.ACCEPTED) {
            throw ApiException.badRequest("Order cannot be cancelled once preparation has started");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        paymentService.refundIfPaid(orderId);
    }

    public OrderResponse updateStatusByOwner(Long orderId, OrderStatus newStatus, User owner) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        if (!order.getRestaurant().getOwner().getId().equals(owner.getId())) {
            throw ApiException.forbidden("This order does not belong to your restaurant");
        }
        if (!OWNER_ALLOWED_TARGETS.contains(newStatus)) {
            throw ApiException.badRequest("Restaurant owners cannot set status to " + newStatus);
        }
        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.PREPARING) {
            deliveryAssignmentService.assignNearestAgent(order);
        }

        orderRepository.save(order);

        if (newStatus == OrderStatus.ACCEPTED) {
            notificationService.notify(order.getCustomer(), "Order Accepted",
                    "Your order #" + order.getId() + " has been accepted by the restaurant.",
                    NotificationType.ORDER_ACCEPTED);
        } else if (newStatus == OrderStatus.PREPARING) {
            notificationService.notify(order.getCustomer(), "Preparing Your Food",
                    "Your order #" + order.getId() + " is being prepared.",
                    NotificationType.PREPARING);
        }

        return toResponse(order);
    }

    public OrderResponse updateStatusByAgent(Long orderId, OrderStatus newStatus, User agent) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        if (order.getDeliveryAgent() == null || !order.getDeliveryAgent().getId().equals(agent.getId())) {
            throw ApiException.forbidden("This order is not assigned to you");
        }
        if (!AGENT_ALLOWED_TARGETS.contains(newStatus)) {
            throw ApiException.badRequest("Delivery agents cannot set status to " + newStatus);
        }
        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            finalizeDelivery(order, agent);
        }

        orderRepository.save(order);

        if (newStatus == OrderStatus.ON_THE_WAY) {
            notificationService.notify(order.getCustomer(), "Out For Delivery",
                    "Your order #" + order.getId() + " is on the way.",
                    NotificationType.OUT_FOR_DELIVERY);
        } else if (newStatus == OrderStatus.DELIVERED) {
            notificationService.notify(order.getCustomer(), "Delivered",
                    "Your order #" + order.getId() + " has been delivered. Enjoy your meal!",
                    NotificationType.DELIVERED);
        }

        return toResponse(order);
    }

    public void acceptAssignment(Long orderId, User agent) {
        Order order = getAssignedOrderOrThrow(orderId, agent);
        order.setAgentConfirmed(true);
        orderRepository.save(order);
    }

    public void rejectAssignment(Long orderId, User agent) {
        Order order = getAssignedOrderOrThrow(orderId, agent);
        deliveryAssignmentService.reassignAfterRejection(order, agent);
        orderRepository.save(order);
    }

    public OrderResponse getCurrentOrderForAgent(User agent) {
        return orderRepository.findByDeliveryAgentIdAndStatusIn(agent.getId(), ACTIVE_STATUSES)
                .stream().findFirst().map(this::toResponse).orElse(null);
    }

    public List<OrderResponse> getOrderHistoryForAgent(User agent) {
        return orderRepository.findByDeliveryAgentIdAndStatus(agent.getId(), OrderStatus.DELIVERED)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getMyOrders(User customer) {
        return orderRepository.findByCustomerIdOrderByIdDesc(customer.getId()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getRestaurantOrders(Long restaurantId, User owner) {
        return orderRepository.findByRestaurantIdOrderByIdDesc(restaurantId).stream()
                .filter(o -> o.getRestaurant().getOwner().getId().equals(owner.getId()))
                .map(this::toResponse).collect(Collectors.toList());
    }

    private void finalizeDelivery(Order order, User agent) {
        BigDecimal earning = BigDecimal.valueOf(30)
                .add(BigDecimal.valueOf(order.getDistanceKm() != null ? order.getDistanceKm() : 0).multiply(BigDecimal.valueOf(5)))
                .setScale(2, RoundingMode.HALF_UP);
        order.setAgentEarning(earning);

        deliveryAgentProfileRepository.findByUserId(agent.getId()).ifPresent(profile -> {
            profile.setBusy(false);
            profile.setTotalEarnings(profile.getTotalEarnings().add(earning));
            deliveryAgentProfileRepository.save(profile);
        });
    }

    private Order getAssignedOrderOrThrow(Long orderId, User agent) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        if (order.getDeliveryAgent() == null || !order.getDeliveryAgent().getId().equals(agent.getId())) {
            throw ApiException.forbidden("This order is not assigned to you");
        }
        return order;
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        Set<OrderStatus> allowedNext = ALLOWED_TRANSITIONS.get(current);
        if (allowedNext == null || !allowedNext.contains(next)) {
            throw ApiException.badRequest(
                    "Cannot move order from " + current + " to " + next + ". Allowed next: " + allowedNext);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(i -> new OrderItemResponse(i.getFoodNameSnapshot(), i.getPriceAtOrderTime(), i.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(), order.getRestaurant().getName(), items, order.getItemsTotal(),
                order.getDeliveryCharge(), order.getDiscountAmount(), order.getAppliedCouponCode(),
                order.getGrandTotal(), order.getStatus(), order.getDeliveryAddressLine(), order.getCreatedAt(),
                order.getEtaMinutes(), order.getDistanceKm(), order.getSurgeMultiplier(),
                order.getDeliveryAgent() != null ? order.getDeliveryAgent().getName() : null,
                order.isAgentConfirmed()
        );
    }
}