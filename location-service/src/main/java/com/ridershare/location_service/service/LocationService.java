package com.ridershare.location_service.service;

import com.ridershare.location_service.config.RedisConfig;
import com.ridershare.location_service.dto.DriverLocationRequest;
import com.ridershare.location_service.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;

    public List<NearByDriverResponse> getNearByDrivers(double latitude, double longitude, double radius) {
        log.info("finding nearby drivers lat: {}, long: {}, {}km", latitude, longitude, radius);
        Circle searchArea = new Circle(
                new Point(longitude,latitude),
                new Distance(radius, Metrics.KILOMETERS)
        );
        GeoResults<RedisGeoCommands.GeoLocation<String>> result = this.redisTemplate.opsForGeo().radius(
                RedisConfig.DRIVERS_GEO_KEY,
                searchArea,
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance().sortAscending().limit(10)
        );
        List<NearByDriverResponse> nearByDriverResponseList = new ArrayList<>();
        if (result != null) {
            result.forEach(geoLocation -> {
                RedisGeoCommands.GeoLocation<String> location = (RedisGeoCommands.GeoLocation<String>) result.getContent();
                nearByDriverResponseList.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getAverageDistance().getValue()
                ));
            });
        }
        log.info("found {} drivers nearby", nearByDriverResponseList.size());
        return nearByDriverResponseList;
    }

    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
        log.info("Updating driver location {}", driverLocationRequest.getDriverId());
        Point driverPoint = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );
        redisTemplate.opsForGeo().add(
                RedisConfig.DRIVERS_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId()
        );
        log.info("Updated driver location {}", driverLocationRequest.getDriverId());
    }

    public void removeDriver(String driverId) {
        log.info("removing driver {}", driverId);
        redisTemplate.opsForGeo().remove(
                RedisConfig.DRIVERS_GEO_KEY,
                driverId);
    }
}
