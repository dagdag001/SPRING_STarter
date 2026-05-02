package com.example.task.repository;

import com.example.task.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @EntityGraph(value = "Task.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findByIdWithAssignee(@Param("id") Long id);

    List<Task> findByTitleContaining(String keyword);
}
