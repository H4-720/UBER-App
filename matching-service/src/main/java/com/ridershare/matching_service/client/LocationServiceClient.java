package com.ridershare.matching_service.client;

import com.ridershare.matching_service.dto.NearByDriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "location-service", url = "${location.service.url}")
public interface LocationServiceClient {
    @GetMapping("/api/v1/location/near-by")
    List<NearByDriverResponse> getNearByDrivers(@RequestParam Double lat, @RequestParam Double lon, @RequestParam double radius);
}
