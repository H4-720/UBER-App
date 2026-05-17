package com.ridershare.matching_service.service;

import com.ridershare.matching_service.client.LocationServiceClient;
import com.ridershare.matching_service.dto.NearByDriverResponse;
import com.ridershare.matching_service.event.RideMatchedEvent;
import com.ridershare.matching_service.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {
    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS = 5.0;

    public void matchDriverForRide(RideRequestedEvent event) {
        List<NearByDriverResponse> nearByDriverResponses = locationServiceClient.getNearByDrivers(event.getPickupLatitude(),event.getPickupLongitude(),DEFAULT_SEARCH_RADIUS);
        if (nearByDriverResponses.isEmpty()) {
            log.info("No near-by drivers found for ride");
            return;
        }

//        find nearby driver
        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDriverResponses);
        if (bestDriver.isEmpty()) {
            log.info("No best driver found for ride");
            return;
        }
        NearByDriverResponse assignedDriver = bestDriver.get();

//        published to kafka
        RideMatchedEvent rideMatchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );
        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), rideMatchedEvent);
        log.info("Ride matched event sent to topic");

    }
    private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> nearByDriverResponses) {
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;
        return nearByDriverResponses.stream().max(Comparator.comparingDouble(
                driver -> {
                    double distanceScore = 1.0/distanceWeight;
                    double simulatedRating = 4.0 +Math.random();
                    return (distanceScore * distanceWeight) + (simulatedRating * ratingWeight);
                }
        ));
    }
}
