package org.example.library.mapper;

import org.example.library.dto.BookDTO;
import org.example.library.dto.BookDTO.BookEDTO;
import org.example.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for the Book entity.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO toDto(Book entity);

    BookEDTO toEDto(Book entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Book toEntity(BookDTO dto);

    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Book toEntity(BookEDTO edto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Book updateBook(BookDTO dto, @MappingTarget Book entity);
}
