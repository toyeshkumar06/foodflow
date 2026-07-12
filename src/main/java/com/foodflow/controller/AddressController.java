package com.foodflow.controller;

import com.foodflow.dto.AddressDtos.AddressResponse;
import com.foodflow.dto.AddressDtos.CreateAddressRequest;
import com.foodflow.entity.User;
import com.foodflow.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public AddressResponse addAddress(@Valid @RequestBody CreateAddressRequest request,
                                       @AuthenticationPrincipal User customer) {
        return addressService.addAddress(request, customer);
    }

    @GetMapping
    public List<AddressResponse> getMyAddresses(@AuthenticationPrincipal User customer) {
        return addressService.getMyAddresses(customer);
    }
}