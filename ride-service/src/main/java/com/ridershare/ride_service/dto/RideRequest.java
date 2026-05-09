package com.ridershare.ride_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {
    @NotEmpty(message = "Rider Id is required")
    private String riderId;
    @NotNull(message = "Pickup latitude is required")
    private double pickUpLatitude;
    @NotNull(message = "Pickup longitude is required")
    private double pickUpLongitude;
    @NotNull(message = "Pickup Address is required")
    private String pickUpAddress;
    @NotNull(message = "Dropoff Address is required")
    private String dropOffAddress;
    @NotNull(message = "Dropoff Latitude is required")
    private double dropOffLatitude;
    @NotNull(message = "Dropoff Longitude is required")
    private double dropOffLongitude;
}
