package com.celeghin.jvminfo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/simulate")
public class JvmInfoHealthSimulationController {
    private final JvmInfoHealthDepStatus dependencies;
    private final JvmInfoHealthLiveness liveness;

    public JvmInfoHealthSimulationController(JvmInfoHealthDepStatus dependencies, JvmInfoHealthLiveness liveness) {
        this.dependencies = dependencies;
        this.liveness = liveness;
    }

    @PostMapping("/db/{up}")
    public Map<String, Object> setDb(@PathVariable boolean up) {
        dependencies.setDbUp(up);
        Map<String, Object> result = new HashMap<>();
        result.put("dbEnabled", up);
        result.put("status", up ? "UP" : "DOWN");
        return result;
    }

    @PostMapping("/rabbit/{up}")
    public String setRabbit(@PathVariable boolean up) {
        dependencies.setRabbitUp(up);
        return "RabbitMQ set to " + (up ? "UP" : "DOWN");
    }

    @PostMapping("/alive/{alive}")
    public String setAlive(@PathVariable boolean alive) {
        liveness.setAlive(alive);
        return "Liveness set to " + alive;
    }

    @PostMapping("/heavyCPU")
    void heavyCPU() {
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            System.out.println("Starting CPU burner on " + cores + " cores...");
            ExecutorService pool = Executors.newFixedThreadPool(cores);

            for (int i = 0; i < cores; i++) {
                pool.submit(() -> {
                    // Infinite loop performing intensive math operations
                    while (true) {
                        Math.sin(Math.random());
                        Math.tan(Math.random());
                    }
                });
            }
        }
        catch (Exception e) {
            System.out.println("CPU burner failed!");
            System.out.println(e.getMessage());
        }
    }

    @PostMapping("/heavyMem")
    void heavyMem() {
        System.out.println("Starting memory burner...");
        List<byte[]> memoryLeaker = new ArrayList<>();

        try {
            while (true) {
                // Allocate 10 Megabytes per iteration
                byte[] chunk = new byte[10 * 1024 * 1024];
                memoryLeaker.add(chunk);
                long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
                long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
                System.out.println("Allocated 10MB. JVM Free: " + freeMem + "MB / Total: " + totalMem + "MB");
                Thread.sleep(100); // Pause briefly to watch the climb
            }
        } catch (OutOfMemoryError e) {
            System.err.println("Target reached: Out of Memory!");
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted.");
        }
    }

}
