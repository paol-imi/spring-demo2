package org.example.library.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to ignore audit fields when mapping entities.
 */
@Retention(RetentionPolicy.CLASS)
@Mapping(target = "createdBy", ignore = true)
@Mapping(target = "createdDate", ignore = true)
@Mapping(target = "lastModifiedBy", ignore = true)
@Mapping(target = "lastModifiedDate", ignore = true)
public @interface IgnoreAuditFields {
}