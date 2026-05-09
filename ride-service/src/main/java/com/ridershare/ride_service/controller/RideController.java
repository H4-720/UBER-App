package com.ridershare.ride_service.controller;

import com.ridershare.ride_service.dto.RideRequest;
import com.ridershare.ride_service.dto.RideResponse;
import com.ridershare.ride_service.repository.RideRepository;
import com.ridershare.ride_service.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ride")
@Slf4j
@RequiredArgsConstructor
public class RideController {
    private final RideRepository rideRepository;
    private RideService rideService;

    @PostMapping("request")
    public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RideRequest rideRequest) {
        log.info("Ride request received from rider: {}", rideRequest.getRiderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.requestRide(rideRequest));
    }
    @GetMapping("/get-ride-by-id")
    public ResponseEntity<RideResponse> getRideById(@RequestParam String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }
    @GetMapping("/ride-by-rider-id")
    public ResponseEntity<List<RideResponse>> getRideByRiderId(@RequestParam String riderId) {
        return ResponseEntity.ok(rideService.getRideByRiderId(riderId));
    }
    @GetMapping("/ride-start")
    public ResponseEntity<RideResponse> startRide(@RequestParam String rideId) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }
    @GetMapping("/ride-complete")
    public ResponseEntity<RideResponse> completeRide(@RequestParam String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }
    @GetMapping("/ride-cancel")
    public ResponseEntity<RideResponse> cancelRide(@RequestParam String rideId) {
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }
}
