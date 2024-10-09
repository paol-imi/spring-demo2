package org.example.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.example.library.entity.Genre;
import org.example.library.validator.ISBN;

import java.time.LocalDate;

/**
 * A DTO representing a book.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class BookDTO {
    /**
     * The title of the book.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    public String title;

    /**
     * The author of the book.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    public String author;

    /**
     * The genre of the book.
     */
    @NotNull
    public Genre genre;

    /**
     * The ISBN of the book.
     */
    @NotNull
    @ISBN(message = "Invalid ISBN format")
    public String isbn;

    /**
     * The publication date of the book.
     */
    @NotNull
    @Past(message = "Publication date must be in the past")
    public LocalDate publicationDate;

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    public static class BookEDTO extends BookDTO {
        /**
         * The unique identifier of the book.
         */
        @NotNull
        public Long id;
    }
}
