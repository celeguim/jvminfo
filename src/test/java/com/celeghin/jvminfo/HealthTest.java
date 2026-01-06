package com.celeghin.jvminfo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthTest {

    @LocalManagementPort
    int managementPort;

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    JvmInfoHealthIndicator health;

    @Test
    void shouldReturnUpForLivenessProbe() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "http://localhost:" + managementPort + "/actuator/health/liveness",
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldExposeCustomLivenessIndicator() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "http://localhost:" + managementPort + "/actuator/health/customLivenessIndicator",
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnUpForReadinessProbe() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "http://localhost:" + managementPort + "/actuator/health/readiness",
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldExposeCustomReadinessIndicator() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "http://localhost:" + managementPort + "/actuator/health/customReadinessIndicator",
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
