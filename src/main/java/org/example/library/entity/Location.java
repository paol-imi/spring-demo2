package org.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A location where books are stored.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "locations", indexes = {
        // Index to allow searching for books by title.
        @Index(name = "idx_location_name", columnList = "name"),
})
public class Location extends Auditable {
    /**
     * The book copies stored at the location.
     */
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCopy> bookCopies = new HashSet<>();

    /**
     * The manager of the location.
     */
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    /**
     * The unique identifier of the location.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the location.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * The address of the location.
     */
    @Column(nullable = false)
    private String address;
}