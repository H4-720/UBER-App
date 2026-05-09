package com.ridershare.location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
