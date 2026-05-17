package com.ridershare.ride_service.service;

import com.ridershare.ride_service.config.KafkaConfig;
import com.ridershare.ride_service.dto.RideRequest;
import com.ridershare.ride_service.dto.RideResponse;
import com.ridershare.ride_service.enums.RideStatus;
import com.ridershare.ride_service.event.RideRequestEvent;
import com.ridershare.ride_service.model.Ride;
import com.ridershare.ride_service.repository.RideRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestEvent> kafkaTemplate;

    public RideResponse requestRide(@Valid RideRequest rideRequest) {
        log.info("Ride request received from rider: {}", rideRequest.getRiderId());

        Ride ride = new Ride();
        ride.setRiderId(rideRequest.getRiderId());
        ride.setPickUpLatitude(rideRequest.getPickUpLatitude());
        ride.setPickUpLongitude(rideRequest.getPickUpLongitude());
        ride.setPickUpAddress(rideRequest.getPickUpAddress());
        ride.setDropOffLatitude(rideRequest.getDropOffLatitude());
        ride.setDropOffLongitude(rideRequest.getDropOffLongitude());
        ride.setDropOffAddress(rideRequest.getDropOffAddress());
        ride.setStatus(RideStatus.REQUESTED);
        //:TODO: incomplete method
        ride.setEstimatedFare(calculateEstimateFare(rideRequest));

        Ride savedRide = rideRepository.save(ride);

        // Event for ride using kafka
        RideRequestEvent rideRequestEvent = new RideRequestEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickUpLatitude(),
                savedRide.getPickUpLongitude(),
                savedRide.getPickUpAddress(),
                savedRide.getDropOffLatitude(),
                savedRide.getDropOffLongitude(),
                savedRide.getDropOffAddress()
        );
        kafkaTemplate.send(KafkaConfig.TOPIC,savedRide.getId(), rideRequestEvent);
        log.info("Ride request sent to rider: {}", savedRide.getRiderId());

        // update status
        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);

        return mapToResponse(savedRide);
    }
    public void updateRideWithDriver(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not fount"));
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }
    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not fount"));
        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started. Current status is " + ride.getStatus());
        }
        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartAt(LocalDateTime.now());
        rideRepository.save(ride);
        return mapToResponse(ride);
    }

    public RideResponse completeRide(@Valid String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not fount"));
        if (ride.getStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride cannot be completed. Current status is " + ride.getStatus());
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());
        rideRepository.save(ride);
        return mapToResponse(ride);
    }

    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not fount"));
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
        return mapToResponse(ride);
    }

    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not fount"));
        return mapToResponse(ride);
    }
    public List<RideResponse> getRidesByRider(String rideId) {
        return rideRepository.findByRideIdOrderByCreatedAtDesc(rideId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    private double calculateEstimateFare(@Valid RideRequest rideRequest) {
        double lat1 = Math.toRadians(rideRequest.getPickUpLatitude());
        double lon1 = Math.toRadians(rideRequest.getPickUpLongitude());

        double lat2 = Math.toRadians(rideRequest.getDropOffLatitude());
        double lon2 = Math.toRadians(rideRequest.getDropOffLongitude());
        double  dlat = lat2 - lat1;
        double  dlon = lon2 - lon1;

        double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distanceKm = 6371 * c;

        double fare = 50 + (distanceKm * 12);
        return Math.round(fare*100.0)/100.0;
    }

    private RideResponse mapToResponse(Ride savedRide) {
        RideResponse rideResponse = new RideResponse();
        rideResponse.setId(savedRide.getId());
        rideResponse.setRiderId(savedRide.getRiderId());
        rideResponse.setDriverId(savedRide.getDriverId());
        rideResponse.setPickUpLatitude(savedRide.getPickUpLatitude());
        rideResponse.setPickUpLongitude(savedRide.getPickUpLongitude());
        rideResponse.setPickUpAddress(savedRide.getPickUpAddress());
        rideResponse.setDropOffLatitude(savedRide.getDropOffLatitude());
        rideResponse.setDropOffLongitude(savedRide.getDropOffLongitude());
        rideResponse.setDropOffAddress(savedRide.getDropOffAddress());
        rideResponse.setStatus(savedRide.getStatus());
        rideResponse.setEstimatedFare(savedRide.getEstimatedFare());
        rideResponse.setActualFare(savedRide.getActualFare());
        rideResponse.setCreateAt(savedRide.getCreateAt());
        rideResponse.setUpdateAt(savedRide.getUpdateAt());
        rideResponse.setStartAt(rideResponse.getStartAt());
        rideResponse.setEndAt(rideResponse.getEndAt());
        return rideResponse;
    }
}
