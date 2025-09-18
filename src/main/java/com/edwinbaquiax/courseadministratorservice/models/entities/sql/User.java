package com.edwinbaquiax.courseadministratorservice.models.entities.sql;

import com.edwinbaquiax.courseadministratorservice.validations.ExistsByUsername;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


/**
 * Tabla que representa Usuarios, en vez de una tabla sola para estudiantes segun el requerimiento.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ExistsByUsername TODO: por corregir
    @NotBlank
    @Size(min = 3, max = 20)
    @Column(unique = true, nullable = false)
    private String username;
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 5, max = 100)
    private String password;

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    private String lastname;
    private boolean enabled;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private Set<Role> roles;
    @OneToMany(mappedBy = "teacher")
    private Set<Course> taughtCourses;
    @OneToMany(mappedBy = "user")
    private Set<Enrollment> enrollments;

    @PrePersist
    public void prePersist() {
        enabled = true;
        createdAt = LocalDateTime.now();
    }

}
