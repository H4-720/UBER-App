package com.ridershare.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestEvent {
    private String rideId;
    private String riderId;
    private double pickUpLatitude;
    private double pickUpLongitude;
    private String pickUpAddress;
    private double dropOffLatitude;
    private double dropOffLongitude;
    private String dropOffAddress;
}
