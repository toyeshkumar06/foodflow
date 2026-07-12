package com.foodflow.service;

import com.foodflow.dto.AddressDtos.AddressResponse;
import com.foodflow.dto.AddressDtos.CreateAddressRequest;
import com.foodflow.entity.Address;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressResponse addAddress(CreateAddressRequest request, User customer) {
        Address address = Address.builder()
                .label(request.getLabel())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .pincode(request.getPincode())
                .isDefault(request.isDefault())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(customer)
                .build();
        addressRepository.save(address);
        return toResponse(address);
    }

    public List<AddressResponse> getMyAddresses(User customer) {
        return addressRepository.findByUserId(customer.getId()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public Address getOwnedAddressOrThrow(Long addressId, User customer) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> ApiException.notFound("Address not found"));
        if (!address.getUser().getId().equals(customer.getId())) {
            throw ApiException.forbidden("This address does not belong to you");
        }
        return address;
    }

    private AddressResponse toResponse(Address a) {
        return new AddressResponse(a.getId(), a.getLabel(), a.getAddressLine(), a.getCity(), a.getPincode(),
                a.isDefault(), a.getLatitude(), a.getLongitude());
    }
}