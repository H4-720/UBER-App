package com.ridershare.ride_service.dto;

import com.ridershare.ride_service.enums.RideStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {
    private String id;
    private String riderId;
    private String driverId;
    private double pickUpLatitude;
    private double pickUpLongitude;
    private String pickUpAddress;
    private double dropOffLatitude;
    private double dropOffLongitude;
    private String dropOffAddress;
    private RideStatus status;
    private double estimatedFare;
    private double actualFare;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
