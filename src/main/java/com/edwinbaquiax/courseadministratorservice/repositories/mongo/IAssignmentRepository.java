package com.edwinbaquiax.courseadministratorservice.repositories.mongo;

import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAssignmentRepository extends MongoRepository<Assignment,String> {
    Page<Assignment> findAllByUserId(Long userId, Pageable pageable);

    Page<Assignment> findAllByTaskId(String taskId, Pageable pageable);

    boolean existsAssignmentByUserIdAndTaskId(Long userId, String taskId);


}
