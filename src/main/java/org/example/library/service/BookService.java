package org.example.library.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.example.library.dto.BookDTO;
import org.example.library.dto.BookDTO.BookEDTO;
import org.example.library.entity.Book;
import org.example.library.lib.RepositoryException;
import org.example.library.mapper.BookMapper;
import org.example.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for the Book entity.
 */
@Service
@AllArgsConstructor
public class BookService {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    /**
     * The repository for the Book entity.
     */
    private final @NonNull BookRepository bookRepository;

    /**
     * The mapper for the Book entity.
     */
    private final @NonNull BookMapper bookMapper;

    /**
     * Get a single book by its id.
     *
     * @param id the id of the book
     * @return an optional with the book if found, empty otherwise
     */
    public @NonNull Optional<BookEDTO> getBookById(@NonNull Long id) {
        return this.bookRepository.findById(id).map(this.bookMapper::toEDto);
    }

    /**
     * Get a list of all books in the library using a specification and pageable.
     *
     * @param bookSpecification the specification to filter the books
     * @param pageable          the pagination information
     * @return a page of books
     */
    public @NonNull Page<BookEDTO> getBooks(@Nullable Specification<Book> bookSpecification, @NonNull Pageable pageable) {
        return this.bookRepository.findAll(bookSpecification, pageable).map(this.bookMapper::toEDto);
    }

    /**
     * Create a new book in the library.
     *
     * @param bookDTO the book to create
     * @return the created book
     * @throws BookAlreadyExistsException if the book already exists
     */
    @Transactional
    public @NonNull BookEDTO createBook(@NonNull BookDTO bookDTO) throws BookAlreadyExistsException {
        if (this.bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new BookAlreadyExistsException(bookDTO.getIsbn());
        }

        Book book = this.bookMapper.toEntity(bookDTO);
        Book savedBook = this.bookRepository.save(book);
        return this.bookMapper.toEDto(savedBook);
    }

    /**
     * Update an existing book in the library.
     *
     * @param id      the id of the book to update
     * @param bookDTO the book data to update
     * @return the updated book
     * @throws BookNotFoundException if the book is not found
     */
    @Transactional
    public @NonNull BookEDTO updateBook(@NonNull Long id, @NonNull BookDTO bookDTO) throws BookNotFoundException {
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        this.bookMapper.updateBook(bookDTO, book);
        return this.bookMapper.toEDto(book);
    }

    /**
     * Delete a book from the library.
     *
     * @param id the id of the book to delete
     * @throws BookNotFoundException if the book is not found
     */
    @Transactional
    public void deleteBook(@NonNull Long id) throws BookNotFoundException {
        if (!this.bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        this.bookRepository.deleteById(id);
    }

    /**
     * Exception thrown when a book is not found.
     */
    public static class BookNotFoundException extends RepositoryException.NotFound {
        /**
         * Creates a new BookNotFoundException with the given id.
         *
         * @param id - the id of the book
         */
        public BookNotFoundException(@NotNull Long id) {
            super("Book not found with id: " + id);
        }
    }

    /**
     * Exception thrown when a book already exists.
     */
    public static class BookAlreadyExistsException extends RepositoryException.Conflict {
        /**
         * Creates a new BookAlreadyExistsException with the given ISBN.
         *
         * @param isbn - the ISBN of the book
         */
        public BookAlreadyExistsException(@NotNull String isbn) {
            super("Book already exists with ISBN: " + isbn);
        }
    }
}