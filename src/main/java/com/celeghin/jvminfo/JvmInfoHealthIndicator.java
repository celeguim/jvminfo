package com.celeghin.jvminfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component("memory")
public class JvmInfoHealthIndicator implements HealthIndicator {
    private static final double THRESHOLD = 0.80; // 80%

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        MemoryMXBean bean1 = ManagementFactory.getMemoryMXBean();
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        long used = runtime.totalMemory() - runtime.freeMemory();
        double usage = (double) used / max;

        System.out.println("--------------- Health check for k8s probes");
        System.out.printf("Max non-heap memory %,d%n", bean1.getNonHeapMemoryUsage().getMax());
        System.out.printf("Used non-heap memory %,d%n", bean1.getNonHeapMemoryUsage().getUsed());
        System.out.printf("Max heap memory %,d%n", max);
        System.out.printf("Used heap memory %,d%n", used);
        System.out.printf("Used heap memory %,.2f%%", usage * 100);
        System.out.println();

        if (usage > THRESHOLD) {
            return Health.down()
                    .withDetail("memory.usage", String.format("%.2f%%", usage * 100))
                    .withDetail("memory.used", used)
                    .withDetail("memory.max", max)
                    .build();
        } else {
            return Health.up()
                    .withDetail("memory.usage", String.format("%.2f%%", usage * 100))
                    .withDetail("memory.used", used)
                    .withDetail("memory.max", max)
                    .build();
        }
    }

    // Liveness and Readiness
    @Autowired
    ApplicationAvailability applicationAvailability;
    ApplicationAvailability availability = new ApplicationAvailability() {
        @Override
        public <S extends AvailabilityState> S getState(Class<S> stateType, S defaultState) {
            return null;
        }

        @Override
        public <S extends AvailabilityState> S getState(Class<S> stateType) {
            return null;
        }

        @Override
        public <S extends AvailabilityState> AvailabilityChangeEvent<S> getLastChangeEvent(Class<S> stateType) {
            return null;
        }
    };

    LivenessState livenessState = availability.getLivenessState();
    ReadinessState readinessState = availability.getReadinessState();

}
