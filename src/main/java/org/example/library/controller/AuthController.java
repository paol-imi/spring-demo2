package org.example.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.library.dto.LoginRequestDTO;
import org.example.library.dto.LoginResponseDTO;
import org.example.library.dto.SignupRequestDTO;
import org.example.library.lib.ErrorResponseImpl;
import org.example.library.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication.
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    /**
     * The authentication service.
     */
    private final @NonNull AuthService authService;

    /**
     * Signup a new user.
     *
     * @param signupRequestDTO the signup request
     * @return the response entity
     */
    @PostMapping("/signup")
    @Operation(summary = "Signup", description = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseImpl.class))),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseImpl.class)))
    })
    public ResponseEntity<?> signup(
            @Parameter(description = "Signup request", required = true) @NonNull
            @Valid @RequestBody SignupRequestDTO signupRequestDTO
    ) {
        try {
            // Register the new user
            this.authService.signup(signupRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AuthService.UserAlreadyExistsException e) {
            // Return a 409 Conflict response if the username or email already exists
            return e.toResponseEntity();
        }
    }

    /**
     * Login the user by returning a JWT token.
     *
     * @param loginRequestDTO the login request
     * @return the response entity with the token
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate a user and return a token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> login(
            @Parameter(description = "Login credentials", required = true) @NonNull
            @Valid @RequestBody
            LoginRequestDTO loginRequestDTO
    ) {
        try {
            // Authenticate the user and return a token.
            return ResponseEntity.ok(this.authService.login(loginRequestDTO));
        } catch (AuthService.UnauthorizedException e) {
            // Return a 401 Unauthorized response if the credentials are invalid.
            return e.toResponseEntity();
        }
    }
}