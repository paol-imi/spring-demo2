package org.example.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.example.library.dto.BookDTO;
import org.example.library.dto.BookDTO.BookEDTO;
import org.example.library.entity.Book;
import org.example.library.lib.SpecificationComposer;
import org.example.library.service.BookService;
import org.example.library.specification.BookSpecification;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the Book entity.
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Book", description = "The Book API")
@AllArgsConstructor
public class BookController {
    /**
     * The BookService instance.
     */
    private final BookService bookService;

    /**
     * Get a paginated list of all books in the library. Optional filtering by title and author.
     *
     * @param title    the title to filter by (case-insensitive, partial match, optional)
     * @param author   the author to filter by (case-insensitive, partial match, optional)
     * @param pageable the Pageable information for pagination (optional, default page: 0, size: 20, sort: title, direction: ASC)
     * @return a paginated list of books
     */
    @GetMapping
    @Operation(summary = "List all books", description = "Get a paginated list of all books in the library. Optional filtering by title and author.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),

    })
    public ResponseEntity<Page<BookEDTO>> getAllBooks(
            @Parameter(description = "Filter books by title (case-insensitive, partial match)")
            @RequestParam(required = false) @Nullable
            String title,
            @Parameter(description = "Filter books by author (case-insensitive, partial match)")
            @RequestParam(required = false) @Nullable
            String author,
            @Parameter(description = "Pageable information for pagination") @ParameterObject
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) @NotNull
            Pageable pageable
    ) {
        // Create a specification for the title and author.
        Specification<Book> bookSpecification = SpecificationComposer.and(
                BookSpecification.titleLike(title),
                BookSpecification.authorLike(author)
        );

        // Return a paginated list of books.
        return ResponseEntity.ok(this.bookService.getBooks(bookSpecification, pageable));
    }

    /**
     * Get a single book by its id.
     *
     * @param id the id of the book
     * @return the book if found, empty otherwise
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id", description = "Get a single book by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the book",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookEDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<BookEDTO> getBookById(
            @Parameter(description = "ID of the book to retrieve", required = true) @NonNull
            @PathVariable
            Long id
    ) {
        return this.bookService.getBookById(id)
                // Return the book if found.
                .map(ResponseEntity::ok)
                // Return a 404 Not Found response if the book is not found.
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new book in the library.
     *
     * @param bookDTO the book to create
     * @return the created book
     */
    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book in the library")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created the book",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookEDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Book already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createBook(
            @Parameter(description = "Book to add to the library", required = true) @NonNull
            @Valid @RequestBody
            BookDTO bookDTO
    ) {
        try {
            // Create the book and return it.
            return ResponseEntity.ok(this.bookService.createBook(bookDTO));
        } catch (BookService.BookAlreadyExistsException e) {
            // Return a 409 Conflict response if the book already exists.
            return e.toResponseEntity();
        }
    }

    /**
     * Update an existing book in the library.
     *
     * @param id      the id of the book to update
     * @param bookDTO the book data to update
     * @return the updated book
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Update an existing book in the library")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the book",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookEDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateBook(
            @Parameter(description = "ID of the book to update", required = true) @NonNull
            @PathVariable
            Long id,
            @Parameter(description = "Updated book information", required = true) @NonNull
            @Valid @RequestBody
            BookDTO bookDTO
    ) {
        try {
            // Update the book by its ID and return it.
            return ResponseEntity.ok(this.bookService.updateBook(id, bookDTO));
        } catch (BookService.BookNotFoundException e) {
            // Return a 404 Not Found response if the book is not found.
            return e.toResponseEntity();
        }
    }

    /**
     * Delete a book from the library.
     *
     * @param id the id of the book to delete
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Delete a book from the library")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted the book"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteBook(
            @Parameter(description = "ID of the book to delete", required = true) @NonNull
            @PathVariable Long id
    ) {
        try {
            // Delete the book by its ID.
            this.bookService.deleteBook(id);
            // Return a 204 No Content response.
            return ResponseEntity.noContent().build();
        } catch (BookService.BookNotFoundException e) {
            // Return a 404 Not Found response.
            return e.toResponseEntity();
        }
    }
}