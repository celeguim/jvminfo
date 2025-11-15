package com.celeghin.jvminfo;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class JvmInfoHealthDepStatus {
    private final AtomicBoolean dbUp = new AtomicBoolean(true);
    private final AtomicBoolean rabbitUp = new AtomicBoolean(true);

    public boolean isDbUp() {
        return dbUp.get();
    }

    public void setDbUp(boolean value) {
        dbUp.set(value);
    }

    public boolean isRabbitUp() {
        return rabbitUp.get();
    }

    public void setRabbitUp(boolean value) {
        rabbitUp.set(value);
    }
}
