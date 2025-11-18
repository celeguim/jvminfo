package com.celeghin.jvminfo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
}
