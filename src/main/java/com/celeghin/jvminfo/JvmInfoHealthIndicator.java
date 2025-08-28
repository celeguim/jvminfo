package com.celeghin.jvminfo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("memoryHealth")
public class JvmInfoHealthIndicator implements HealthIndicator {
    private static final double THRESHOLD = 0.80; // 80%

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        long used = runtime.totalMemory() - runtime.freeMemory();
        double usage = (double) used / max;

        System.out.println(usage * 100);

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
}
