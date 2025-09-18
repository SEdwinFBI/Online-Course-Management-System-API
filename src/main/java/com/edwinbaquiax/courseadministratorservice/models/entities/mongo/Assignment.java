package com.edwinbaquiax.courseadministratorservice.models.entities.mongo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {
    @Id
    private String id;
    @Field("user_id")
    private Long userId;
    @Field("task_id")
    private String taskId;
    @Enumerated(EnumType.STRING)
    private String status;
    private Double score;
    @Field("submitted_at")
    private LocalDateTime submittedAt;

}