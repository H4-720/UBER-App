package com.ridershare.location_service.controller;

import com.ridershare.location_service.dto.DriverLocationRequest;
import com.ridershare.location_service.dto.NearByDriverResponse;
import com.ridershare.location_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest driverLocationRequest) {
        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/near-by")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(
            @RequestParam double latitude, @RequestParam double longitude, @RequestParam (defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(locationService.getNearByDrivers(latitude,longitude,radius));
    }
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeDriverLocation(@PathVariable String driverId) {
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver Removed");
    }
}
