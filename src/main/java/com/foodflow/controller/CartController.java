package com.foodflow.controller;

import com.foodflow.dto.CartDtos.*;
import com.foodflow.entity.User;
import com.foodflow.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public CartResponse addToCart(@Valid @RequestBody AddToCartRequest request,
                                   @AuthenticationPrincipal User customer) {
        cartService.addToCart(request, customer);
        return cartService.getCart(customer);
    }

    @PatchMapping("/items/{cartItemId}")
    public CartResponse updateQuantity(@PathVariable Long cartItemId,
                                        @Valid @RequestBody UpdateCartItemRequest request,
                                        @AuthenticationPrincipal User customer) {
        cartService.updateQuantity(cartItemId, request.getQuantity(), customer);
        return cartService.getCart(customer);
    }

    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeFromCart(@PathVariable Long cartItemId,
                                        @AuthenticationPrincipal User customer) {
        cartService.removeFromCart(cartItemId, customer);
        return cartService.getCart(customer);
    }

    @DeleteMapping
    public void clearCart(@AuthenticationPrincipal User customer) {
        cartService.clearCart(customer);
    }

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal User customer) {
        return cartService.getCart(customer);
    }
}