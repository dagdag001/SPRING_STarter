package com.example.task.repository;

import com.example.task.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class TaskRepositoryTest {
    @Autowired
    private TaskRepository repo;

    @Test
    void shouldFindByTitle() {
        Task task = new Task();
        task.setTitle("Test Task");
        repo.save(task);
        
        List<Task> found = repo.findByTitleContaining("Test");
        assertFalse(found.isEmpty());
    }
}
