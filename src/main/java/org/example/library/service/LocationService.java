package org.example.library.service;

import org.example.library.dto.LocationDTO;
import org.example.library.dto.LocationDTO.LocationEDTO;
import org.example.library.entity.Location;
import org.example.library.lib.RepositoryException;
import org.example.library.mapper.LocationMapper;
import org.example.library.repository.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for the Location entity.
 */
@Service
public class LocationService {
    /**
     * The repository for the Location entity.
     */
    private final @NonNull LocationRepository locationRepository;

    /**
     * The ModelMapper instance.
     */
    private final @NonNull LocationMapper locationMapper;

    /**
     * Create a new LocationServiceImpl.
     *
     * @param locationRepository the repository for the Location entity
     * @param locationMapper     the ModelMapper instance
     */
    public LocationService(@NonNull LocationRepository locationRepository, @NonNull LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    /**
     * Get a single location by its id.
     *
     * @param id the id of the location
     * @return an optional with the location if found, empty otherwise
     */
    public @NonNull Optional<LocationEDTO> getLocationById(@NonNull Long id) {
        return this.locationRepository.findById(id).map(this.locationMapper::toEDto);
    }

    /**
     * Get a list of all locations in the library using a specification and pageable.
     *
     * @param locationSpecification the specification to filter the locations
     * @param pageable              the pagination information
     * @return a page of locations
     */
    public @NonNull Page<LocationEDTO> getLocations(@Nullable Specification<Location> locationSpecification, @NonNull Pageable pageable) {
        return this.locationRepository.findAll(locationSpecification, pageable).map(this.locationMapper::toEDto);
    }

    /**
     * Create a new location in the library.
     *
     * @param locationDTO the location to create
     * @return the created location
     */
    @Transactional
    public @NonNull LocationEDTO createLocation(@NonNull LocationDTO locationDTO) {
        Location location = this.locationMapper.toEntity(locationDTO);
        Location savedLocation = this.locationRepository.save(location);
        return this.locationMapper.toEDto(savedLocation);
    }

    /**
     * Update an existing location in the library.
     *
     * @param id          the id of the location to update
     * @param locationDTO the location data to update
     * @return the updated location
     * @throws LocationNotFoundException if the location is not found
     */
    @Transactional
    public @NonNull LocationEDTO updateLocation(@NonNull Long id, @NonNull LocationDTO locationDTO) throws LocationNotFoundException {
        Location location = this.locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id));
        this.locationMapper.updateLocation(locationDTO, location);
        return this.locationMapper.toEDto(location);
    }

    /**
     * Delete a location from the library.
     *
     * @param id the id of the location to delete
     * @throws LocationNotFoundException if the location is not found
     */
    @Transactional
    public void deleteLocation(@NonNull Long id) throws LocationNotFoundException {
        if (!this.locationRepository.existsById(id)) {
            throw new LocationNotFoundException(id);
        }

        this.locationRepository.deleteById(id);
    }

    /**
     * Exception thrown when a location is not found.
     */
    public static class LocationNotFoundException extends RepositoryException.NotFound {
        /**
         * Creates a new LocationNotFoundException with the given id.
         *
         * @param id - the id of the location
         */
        public LocationNotFoundException(@NonNull Long id) {
            super("Location not found with id: " + id);
        }
    }
}