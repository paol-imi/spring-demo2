package org.example.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * A location where books are stored.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class LocationDTO {
    /**
     * The name of the location.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    /**
     * The address of the location.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Address must be between 1 and 255 characters")
    private String address;

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    public static class LocationEDTO extends LocationDTO {
        /**
         * The unique identifier of the location.
         */
        @NotNull
        private Long id;
    }
}