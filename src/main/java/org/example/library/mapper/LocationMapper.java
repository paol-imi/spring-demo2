package org.example.library.mapper;

import org.example.library.dto.LocationDTO;
import org.example.library.dto.LocationDTO.LocationEDTO;
import org.example.library.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for the Location entity.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDTO toDto(Location entity);

    LocationEDTO toEDto(Location entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Location toEntity(LocationDTO dto);

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Location toEntity(LocationEDTO edto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Location updateLocation(LocationDTO dto, @MappingTarget Location entity);
}