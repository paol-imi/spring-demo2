package org.example.library.actuator;

import lombok.AllArgsConstructor;
import org.example.library.entity.Book;
import org.example.library.repository.BookRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for the library application.
 */
@Component
@AllArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {
    /**
     * The book repository.
     */
    @NonNull
    private final BookRepository bookRepository;

    /**
     * Check the health of the library application defined by the number of books in the library.
     *
     * @return the health of the library application
     */
    @Override
    public Health health() {
        // "count" is very slow for large tables, so we use a custom specification to check if any book exists.
        Specification<Book> anyBook = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        boolean bookExists = this.bookRepository.findOne(anyBook).isPresent();
        if (bookExists) {
            return Health.up()
                    .withDetail("bookCount", "at least one")
                    .build();
        } else {
            return Health.down()
                    .withDetail("reason", "No books in the library")
                    .build();
        }
    }
}