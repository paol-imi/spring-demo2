package org.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * A user of the library.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends Auditable {
    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username of the user.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * The email of the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The roles of the user.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;
}