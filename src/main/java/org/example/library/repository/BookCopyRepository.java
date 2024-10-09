package org.example.library.repository;

import org.example.library.entity.Book;
import org.example.library.entity.BookCopy;
import org.example.library.lib.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for the BookCopy entity.
 */
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, BookCopy.BookCopyId>, JpaSpecificationExecutor<BookCopy> {
    /**
     * Find the quantity of a book at a location.
     *
     * @param locationId the id of the location
     * @param bookId     the id of the book
     * @return the quantity of the book at the location
     */
    BookCopy getByLocationIdAndBookId(@Param("locationId") Long locationId, @Param("bookId") Long bookId);

    /**
     * Find the books with quantities at a location.
     *
     * @param locationId the id of the location
     * @param pageable   the pageable object
     * @return the books with quantities at the location
     */
    @Query("SELECT new org.example.library.lib.Tuple(b, bc.quantity)" +
            "FROM BookCopy bc JOIN bc.book b WHERE bc.id.locationId = :locationId")
    Page<Tuple<Book, Integer>> findBooksWithQuantitiesByLocationId(@Param("locationId") Long locationId, Pageable pageable);
}
