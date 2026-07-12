package com.foodflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

public class AddressDtos {

    @Data
    public static class CreateAddressRequest {
        private String label;
        @NotBlank private String addressLine;
        @NotBlank private String city;
        private String pincode;
        private boolean isDefault;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @AllArgsConstructor
    public static class AddressResponse {
        private Long id;
        private String label;
        private String addressLine;
        private String city;
        private String pincode;
        private boolean isDefault;
        private Double latitude;
        private Double longitude;
    }
}