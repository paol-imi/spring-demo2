package org.example.library.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Metrics class for tracking method execution time.
 */
@Component
@AllArgsConstructor
public class PerformanceMetrics {
    /**
     * MeterRegistry object.
     */
    private final MeterRegistry meterRegistry;

    /**
     * Record method execution time with class and method tags.
     *
     * @param className     - class name
     * @param methodName    - method name
     * @param executionTime - execution time of the method
     */
    public void recordMethodExecutionTime(String className, String methodName, long executionTime) {
        Timer.builder("library.method.execution.time")
                .description("Execution time of methods")
                .tag("class", className)
                .tag("method", methodName)
                .register(this.meterRegistry)
                .record(executionTime, TimeUnit.MILLISECONDS);
    }
}
