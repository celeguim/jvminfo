package com.celeghin.jvminfo;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("customLiveness")
public class CustomHealth implements HealthIndicator {
    private final WorkerPool workerPool;

    public WorkerLivenessIndicator(WorkerPool workerPool) {
        this.workerPool = workerPool;
    }

    @Override
    public Health health() {
        return workerPool.isRunning()
                ? Health.up().build()
                : Health.down().withDetail("error", "Worker pool stopped").build();
    }

}
