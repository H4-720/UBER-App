package com.ridershare.ride_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String TOPIC = "ride.requested";

    @Bean
    public NewTopic rideRequestTopic() {
        return TopicBuilder.name(TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic rideMatchTopic() {
        return TopicBuilder.name(TOPIC).partitions(3).replicas(1).build();
    }
}
