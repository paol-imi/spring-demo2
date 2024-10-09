package org.example.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * A request to log in.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LoginRequestDTO {
    /**
     * The username.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private final String username;

    /**
     * The password.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private final String password;
}