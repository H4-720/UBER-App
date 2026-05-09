package com.ridershare.ride_service.model;

import com.ridershare.ride_service.enums.RideStatus;
import io.micrometer.core.annotation.TimedSet;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
//import org.hibernate.validator.constraints.UUID;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String riderId;
    @Column(nullable = false)
    private String driverId;
    @Column(nullable = false)
    private double pickUpLatitude;
    @Column(nullable = false)
    private double pickUpLongitude;
    @Column(nullable = false)
    private String pickUpAddress;
    @Column(nullable = false)
    private double dropOffLatitude;
    @Column(nullable = false)
    private double dropOffLongitude;
    @Column(nullable = false)
    private String dropOffAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;
    private double estimatedFare;
    private double actualFare;

    @CreationTimestamp
    private LocalDateTime createAt;
    @UpdateTimestamp
    private LocalDateTime updateAt;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
