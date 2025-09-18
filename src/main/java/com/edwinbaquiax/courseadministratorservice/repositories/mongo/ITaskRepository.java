package com.edwinbaquiax.courseadministratorservice.repositories.mongo;

import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaskRepository extends MongoRepository<Task,String > {
    Page<Task> findAllByModuleIdAndActiveTrue(Long moduleId, Pageable pageable);

}
