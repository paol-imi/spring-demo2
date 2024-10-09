package org.example.library.security;

import lombok.AllArgsConstructor;
import org.example.library.entity.Location;
import org.example.library.repository.LocationRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Authorization class for the manager of a location.
 */
@Component
@AllArgsConstructor
public class ManagerAuthorization {
    /**
     * The LocationRepository to use for the authorization.
     */
    private final @NonNull LocationRepository locationRepository;

    /**
     * Checks if the authenticated user is the manager of the location.
     *
     * @param authentication The Authentication object of the authenticated user
     * @param locationId     The ID of the location to check
     * @return True if the authenticated user is the manager of the location, false otherwise
     */
    public boolean isUserLocationManager(@NonNull Authentication authentication, @NonNull Long locationId) {
        UserDetailsImpl authenticatedUser = (UserDetailsImpl) authentication.getPrincipal();

        Location location = locationRepository.findById(locationId).orElse(null);

        if (location == null) {
            return false;
        }

        return location.getManager().getId().equals(authenticatedUser.getId());
    }
}
