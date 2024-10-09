package org.example.library.service;

import lombok.AllArgsConstructor;
import org.example.library.entity.User;
import org.example.library.repository.UserRepository;
import org.example.library.security.UserDetailsImpl;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for the CustomUserDetailsService.
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * The UserRepository to use for the service.
     */
    private final @NonNull UserRepository userRepository;

    /**
     * Loads a user by the username.
     *
     * @param username The username to load the user by
     * @return The UserDetails of the user
     * @throws UsernameNotFoundException If the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserDetailsImpl(user);
    }
}