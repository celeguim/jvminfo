package com.celeghin.jvminfo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("customLivenessIndicator")
public class JvmInfoHealthLiveness implements HealthIndicator {
    private volatile boolean alive = true;

    @Override
    public Health health() {

        System.out.println("--------------- customLivenessIndicator: health()");
        return alive ? Health.up().build() : Health.down().build();
    }

    public void setAlive(boolean alive) {

        this.alive = alive;
    }

}
