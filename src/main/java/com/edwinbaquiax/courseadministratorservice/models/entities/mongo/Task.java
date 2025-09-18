package com.edwinbaquiax.courseadministratorservice.models.entities.mongo;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    private String id;
    private String title;
    private Double value;
    private String description;
    private String instructions;
    private boolean active;
    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private String typeTask;
    @Field("module_id")
    private Long moduleId;



}
