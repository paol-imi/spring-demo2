package org.example.library.service;

import lombok.AllArgsConstructor;
import org.example.library.dto.LoginRequestDTO;
import org.example.library.dto.LoginResponseDTO;
import org.example.library.dto.SignupRequestDTO;
import org.example.library.entity.User;
import org.example.library.lib.RepositoryException;
import org.example.library.repository.UserRepository;
import org.example.library.security.JwtTokenProvider;
import org.example.library.security.UserDetailsImpl;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {
    /**
     * The authentication manager.
     */
    private final @NonNull AuthenticationManager authenticationManager;

    /**
     * The JWT token provider.
     */
    private final @NonNull JwtTokenProvider jwtTokenProvider;

    /**
     * The user details service.
     */
    private final @NonNull UserRepository userRepository;

    /**
     * The user repository.
     */
    private final @NonNull PasswordEncoder passwordEncoder;

    /**
     * Register a new user.
     *
     * @param signupRequestDTO the signup request
     * @throws UserAlreadyExistsException if the user already exists
     */
    @Transactional
    public void signup(SignupRequestDTO signupRequestDTO) throws UserAlreadyExistsException {
        // Check if the username or email already exists.
        if (userRepository.findByUsername(signupRequestDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        if (userRepository.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered.");
        }

        // Create and save the user.
        User user = new User(
                null,
                signupRequestDTO.getUsername(),
                passwordEncoder.encode(signupRequestDTO.getPassword()),
                signupRequestDTO.getEmail(),
                null);

        userRepository.save(user);
    }

    /**
     * Login the user by returning a JWT token.
     *
     * @param loginRequest the login request
     * @return the login response
     * @throws AuthenticationException if the authentication fails
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws UnauthorizedException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);
            return new LoginResponseDTO(userDetails.getUsername(), userDetails.getEmail(), token);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException();
        }
    }

    /**
     * Exception thrown when the user already exists.
     */
    public static class UserAlreadyExistsException extends RepositoryException.Conflict {
        /**
         * Creates a new UserAlreadyExistsException with the given violation.
         *
         * @param violation the violation
         */
        public UserAlreadyExistsException(String violation) {
            super("Unable to create user: " + violation);
        }
    }

    /**
     * Exception thrown when the user is unauthorized.
     */
    public static class UnauthorizedException extends RepositoryException.Unauthorized {
        /**
         * Creates a new UnauthorizedException.
         */
        public UnauthorizedException() {
            super("Bad credentials");
        }
    }
}
