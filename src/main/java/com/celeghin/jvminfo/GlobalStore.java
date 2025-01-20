package com.celeghin.jvminfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalStore {
    private static final List<byte[]> memoryConsumer = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(GlobalStore.class);
 
    public void add() {
    	memoryConsumer.add(new byte[10 * 1024 * 1024]);
    }

    public void print() {
    	System.out.println("Allocated 10MB, Total: " + (memoryConsumer.size() * 10) + "MB");
    	logger.debug("Allocated 10MB, Total: " + (memoryConsumer.size() * 10) + "MB");
    }
    
    public int get() {
    	return (memoryConsumer.size() * 10);
    }
}

