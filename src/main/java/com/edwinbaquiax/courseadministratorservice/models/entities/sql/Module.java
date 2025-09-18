package com.edwinbaquiax.courseadministratorservice.models.entities.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "module_name")
    @Size(min = 3, max = 30)
    private String moduleName;
    @NotBlank
    private String description;
    private boolean active;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties(
            {"modules",
                    "handler",
                    "hibernateLazyInitializer"
            }
    )
    @ManyToOne
    private Course course;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.active = true; //todos los  cursos son activo por defecto
        this.createdAt = LocalDateTime.now();
    }



}
