package com.foodflow.service;

import com.foodflow.dto.CartDtos.*;
import com.foodflow.entity.CartItem;
import com.foodflow.entity.FoodItem;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.CartItemRepository;
import com.foodflow.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;

    public void addToCart(AddToCartRequest request, User customer) {
        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> ApiException.notFound("Food item not found"));

        if (!foodItem.isAvailable()) {
            throw ApiException.badRequest("This item is currently unavailable");
        }

        List<CartItem> existingCart = cartItemRepository.findByCustomerId(customer.getId());

        if (!existingCart.isEmpty() &&
                !existingCart.get(0).getRestaurant().getId().equals(foodItem.getRestaurant().getId())) {
            throw ApiException.conflict(
                    "Your cart has items from a different restaurant. Clear your cart before ordering here.");
        }

        var existingItem = cartItemRepository.findByCustomerIdAndFoodItemId(customer.getId(), foodItem.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .customer(customer)
                    .foodItem(foodItem)
                    .restaurant(foodItem.getRestaurant())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(item);
        }
    }

    public void updateQuantity(Long cartItemId, int quantity, User customer) {
        CartItem item = getOwnedCartItemOrThrow(cartItemId, customer);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    public void removeFromCart(Long cartItemId, User customer) {
        CartItem item = getOwnedCartItemOrThrow(cartItemId, customer);
        cartItemRepository.delete(item);
    }

    public void clearCart(User customer) {
        cartItemRepository.deleteByCustomerId(customer.getId());
    }

    public CartResponse getCart(User customer) {
        List<CartItem> items = cartItemRepository.findByCustomerId(customer.getId());

        if (items.isEmpty()) {
            return new CartResponse(null, null, List.of(), BigDecimal.ZERO);
        }

        List<CartItemResponse> itemResponses = items.stream().map(i -> new CartItemResponse(
                i.getId(), i.getFoodItem().getId(), i.getFoodItem().getName(), i.getFoodItem().getPrice(),
                i.getQuantity(), i.getFoodItem().getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
        )).collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items.get(0).getRestaurant().getId(), items.get(0).getRestaurant().getName(),
                itemResponses, total);
    }

    private CartItem getOwnedCartItemOrThrow(Long cartItemId, User customer) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> ApiException.notFound("Cart item not found"));
        if (!item.getCustomer().getId().equals(customer.getId())) {
            throw ApiException.forbidden("This cart item does not belong to you");
        }
        return item;
    }
}