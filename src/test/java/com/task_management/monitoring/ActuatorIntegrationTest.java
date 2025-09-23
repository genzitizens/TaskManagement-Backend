package com.task_management.monitoring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ActuatorIntegrationTest {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @Autowired
    private PrometheusScrapeEndpoint prometheusScrapeEndpoint;

    @Test
    void healthEndpointReportsUpStatus() {
        assertEquals(Status.UP, healthEndpoint.health().getStatus());
    }

    @Test
    void customTaskMetricsAreRegistered() {
        assertNotNull(metricsEndpoint.metric("task_management.tasks.created", null));
    }

    @Test
    void prometheusRegistryExportsCustomCounter() {
        String scrape = prometheusScrapeEndpoint.scrape();
        assertTrue(scrape.contains("task_management_tasks_created_total"));
    }
}
