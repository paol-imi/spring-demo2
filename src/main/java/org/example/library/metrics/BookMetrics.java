package org.example.library.metrics;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.example.library.entity.Genre;
import org.springframework.stereotype.Component;

/**
 * Metrics class for tracking book sales and restocking.
 */
@Component
@AllArgsConstructor
public class BookMetrics {
    /**
     * MeterRegistry object.
     */
    private final MeterRegistry meterRegistry;

    /**
     * Record books sold with genre and author tags.
     *
     * @param genre    - genre of the book
     * @param author   - author of the book
     * @param quantity - quantity of books sold
     */
    public void recordBookSold(Genre genre, String author, Integer quantity) {
        DistributionSummary.builder("library.books.sold")
                .description("Total number of books sold over time")
                .tag("genre", genre.toString())
                .tag("author", author)
                .register(meterRegistry)
                .record(quantity);
    }

    /**
     * Record books restocked with genre and author tags.
     *
     * @param genre    - genre of the book
     * @param author   - author of the book
     * @param quantity - quantity of books restocked
     */
    public void recordBookRestocked(Genre genre, String author, Integer quantity) {
        DistributionSummary.builder("library.books.restocked")
                .description("Total number of books restocked over time")
                .tag("genre", genre.toString())
                .tag("author", author)
                .register(meterRegistry)
                .record(quantity);
    }
}
