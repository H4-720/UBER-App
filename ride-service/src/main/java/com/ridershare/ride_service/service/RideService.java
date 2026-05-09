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

    private double calculateEstimateFare(@Valid RideRequest rideRequest) {
        return 0;
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
