package com.edwinbaquiax.courseadministratorservice.models.entities.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    @NotBlank
    private String description;

    @PrePersist
    public void prePersist(){
        if(!getName().toLowerCase().startsWith("role_")){
            setName("ROLE_"+getName().toUpperCase());
        }
    }
}
