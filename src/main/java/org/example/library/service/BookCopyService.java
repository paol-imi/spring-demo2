package org.example.library.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.example.library.dto.BookDTO.BookEDTO;
import org.example.library.entity.Book;
import org.example.library.entity.BookCopy;
import org.example.library.entity.Location;
import org.example.library.lib.RepositoryException;
import org.example.library.lib.Tuple;
import org.example.library.mapper.BookMapper;
import org.example.library.metrics.BookMetrics;
import org.example.library.repository.BookCopyRepository;
import org.example.library.repository.BookRepository;
import org.example.library.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for the Book entity.
 */
@Service
@AllArgsConstructor
public class BookCopyService {
    /**
     * The logger for this class.
     */
    public static final Logger logger = LoggerFactory.getLogger(BookCopyService.class);

    /**
     * The metrics for the Book entity.
     */
    private final BookMetrics metrics;

    /**
     * The repository for the Book entity.
     */
    private final @NonNull BookCopyRepository bookCopyRepository;

    /**
     * The repository for the Location entity.
     */
    private final @NonNull LocationRepository locationRepository;

    /**
     * The repository for the Book entity.
     */
    private final @NonNull BookRepository bookRepository;

    /**
     * The mapper for the Book entity.
     */
    private final @NonNull BookMapper bookMapper;

    /**
     * Update the quantity of a book at a location.
     * Positive values add copies, negative values remove copies.
     *
     * @param locationId     the id of the location
     * @param bookId         the id of the book
     * @param quantityChange the change in quantity (positive to add, negative to remove)
     * @return the updated quantity of the book at the location
     * @throws LocationService.LocationNotFoundException if the location is not found
     * @throws BookService.BookNotFoundException         if the book is not found
     * @throws InsufficientCopiesException               if there are not enough copies to remove
     */
    @NonNull
    @Transactional
    public Integer updateBookCopyQuantity(@NonNull Long locationId, @NonNull Long bookId, @NonNull Integer quantityChange) throws LocationService.LocationNotFoundException, BookService.BookNotFoundException, InsufficientCopiesException {
        BookCopy bookCopy = this.bookCopyRepository.findById(new BookCopy.BookCopyId(locationId, bookId)).orElse(null);
        // Create a new book copy if it does not exist.
        if (bookCopy == null) {
            // Find the location by its ID and throw an exception if it is not found.
            Location location = this.locationRepository.findById(locationId)
                    .orElseThrow(() -> new LocationService.LocationNotFoundException(locationId));

            // Find the book by its ID and throw an exception if it is not found.
            Book book = this.bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookService.BookNotFoundException(bookId));

            bookCopy = new BookCopy(book, location, 0);
        }

        // If the quantity is not enough, throw an exception.
        if (bookCopy.getQuantity() + quantityChange < 0) {
            throw new InsufficientCopiesException(locationId, bookId, bookCopy.getQuantity(), -quantityChange);
        }

        // Log and update the metrics.
        logger.info("Updating book copy quantity at location {} for book {} by {}", locationId, bookId, quantityChange);
        if (quantityChange > 0) {
            this.metrics.recordBookRestocked(bookCopy.getBook().getGenre(), bookCopy.getBook().getAuthor(), quantityChange);
        } else {
            this.metrics.recordBookSold(bookCopy.getBook().getGenre(), bookCopy.getBook().getAuthor(), -quantityChange);
        }

        // Update the quantity of the book at the location and save it.
        bookCopy.setQuantity(bookCopy.getQuantity() + quantityChange);
        BookCopy savedBookCopy = this.bookCopyRepository.save(bookCopy);
        return savedBookCopy.getQuantity();
    }

    /**
     * Get the quantity of a book at a location.
     *
     * @param locationId the id of the location
     * @param bookId     the id of the book
     * @return the quantity of the book at the location
     * @throws LocationService.LocationNotFoundException if the location is not found
     * @throws BookService.BookNotFoundException         if the book is not found
     */
    @NonNull
    @Transactional(readOnly = true)
    public Integer getBookCopyQuantity(@NonNull Long locationId, @NonNull Long bookId) throws LocationService.LocationNotFoundException, BookService.BookNotFoundException {
        if (!this.locationRepository.existsById(locationId)) {
            throw new LocationService.LocationNotFoundException(locationId);
        }

        if (!this.bookRepository.existsById(bookId)) {
            throw new BookService.BookNotFoundException(bookId);
        }

        return this.bookCopyRepository.getByLocationIdAndBookId(locationId, bookId).getQuantity();
    }

    /**
     * Get a list of all books in the library at a location using a pageable.
     *
     * @param locationId the id of the location
     * @param pageable   the pagination information
     * @return a page of books with quantities at the location
     * @throws LocationService.LocationNotFoundException if the location is not found
     */
    @NonNull
    @Transactional(readOnly = true)
    public Page<Tuple<BookEDTO, Integer>> getBooksWithQuantitiesAtLocation(@NonNull Long locationId, Pageable pageable) throws LocationService.LocationNotFoundException {
        if (!this.locationRepository.existsById(locationId)) {
            throw new LocationService.LocationNotFoundException(locationId);
        }

        // Map the books with quantities to DTOs.
        return this.bookCopyRepository.findBooksWithQuantitiesByLocationId(locationId, pageable)
                .map(tuple -> new Tuple<>(this.bookMapper.toEDto(tuple.key()), tuple.value()));
    }

    /**
     * Exception thrown when there are not enough copies of a book to remove.
     */
    public static class InsufficientCopiesException extends RepositoryException.Conflict {
        /**
         * Creates a new InsufficientCopiesException with the given location, book, quantity, and requested quantity.
         *
         * @param locationId        the id of the location
         * @param bookId            the id of the book
         * @param quantity          the quantity of books at the location
         * @param requestedQuantity the requested quantity
         */
        public InsufficientCopiesException(@NotNull Long locationId, @NotNull Long bookId, @NotNull Integer quantity, @NotNull Integer requestedQuantity) {
            super("Insufficient copies of book " + bookId + " at location " + locationId + ". Found " + quantity + ", requested " + requestedQuantity);
        }
    }
}