package com.celeghin.jvminfo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("customReadinessIndicator")
public class JvmInfoHealthReadiness implements HealthIndicator {
    public static JvmInfoHealthDepStatus status;

    public JvmInfoHealthReadiness(JvmInfoHealthDepStatus status) {
        JvmInfoHealthReadiness.status = status;
    }

    @Override
    public Health health() {
        System.out.println("--------------- customReadinessIndicator: health()");

        if (!status.isDbUp()) {
            return Health.outOfService().withDetail("database", "down").build();
        }

        if (!status.isRabbitUp()) {
            return Health.outOfService().withDetail("rabbitmq", "down").build();
        }

        return Health.up().withDetail("status", "ready").build();
    }

}
