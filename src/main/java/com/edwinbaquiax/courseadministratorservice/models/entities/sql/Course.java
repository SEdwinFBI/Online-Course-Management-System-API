package com.edwinbaquiax.courseadministratorservice.models.entities.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "title", nullable = false,unique = true)
    @Size(min = 3, max = 30)
    private String title;
    @NotBlank
    private String description;
    private boolean active;
    @Column(name="created_at")
    private LocalDateTime createdAt;


    @JsonIgnoreProperties(
            {"courses",
                    "handler",
                    "hibernateLazyInitializer"
            }
    )

    @OneToMany(mappedBy = "course")
    private Set<Module> modules;

    @ManyToOne
    private User  teacher;

    @OneToMany(mappedBy = "course")
    private Set<Enrollment> enrollments;

    @PrePersist
    public void prePersist(){
        this.active = true; //todos los  cursos son activo por defecto
        this.createdAt = LocalDateTime.now();
    }

}
