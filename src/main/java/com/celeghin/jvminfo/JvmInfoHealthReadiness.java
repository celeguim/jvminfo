package com.celeghin.jvminfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component("customReadinessIndicator")
public class JvmInfoHealthReadiness implements HealthIndicator {
    public static JvmInfoHealthDepStatus status;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    public JvmInfoHealthReadiness(JvmInfoHealthDepStatus status) {
        JvmInfoHealthReadiness.status = status;
    }

    @Override
    public Health health() {
        System.out.println("--------------- customReadinessIndicator: health()");

        if (!status.isDbUp()) {
            AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
            return Health.outOfService().withDetail("database", "down").build();
        }

        if (!status.isRabbitUp()) {
            return Health.outOfService().withDetail("rabbitmq", "down").build();
        }

        return Health.up().withDetail("status", "ready").build();
    }

}
