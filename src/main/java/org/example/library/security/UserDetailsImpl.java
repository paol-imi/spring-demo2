package org.example.library.security;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.library.entity.User;
import org.springframework.lang.NonNull;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementation of the UserDetails interface for the User entity.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails, CredentialsContainer {
    /**
     * The User entity to use for the UserDetails.
     */
    @NonNull
    private final User user;

    /**
     * Gets the User entity of the UserDetails.
     *
     * @return The User entity of the UserDetails
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toSet());
    }

    /**
     * Gets the ID of the UserDetails.
     *
     * @return The ID of the UserDetails
     */
    public Long getId() {
        return this.user.getId();
    }

    /**
     * Gets the password of the UserDetails.
     *
     * @return The password of the UserDetails
     */
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    /**
     * Gets the username of the UserDetails.
     *
     * @return The username of the UserDetails
     */
    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    /**
     * Gets the email of the UserDetails.
     *
     * @return The email of the UserDetails
     */
    public String getEmail() {
        return this.user.getEmail();
    }

    /**
     * Checks if the account is enabled.
     *
     * @return True if the account is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        // TODO: Implement this method.
        return true;
    }

    /**
     * Checks if the credentials are not expired.
     *
     * @return True if the credentials are not expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: Implement this method.
        return true;
    }

    /**
     * Erases the credentials of the UserDetails.
     */
    @Override
    public void eraseCredentials() {
        // TODO: Implement this method.
        throw new UnsupportedOperationException("eraseCredentials() has not been implemented yet.");
    }
}
