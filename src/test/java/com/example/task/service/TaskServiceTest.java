package com.example.task.service;

import com.example.task.dto.TaskRequest;
import com.example.task.dto.TaskResponse;
import com.example.task.entity.Task;
import com.example.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {
        TaskRequest req = new TaskRequest("Learn Spring", "Read docs");
        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Learn Spring");
        saved.setDescription("Read docs");
        
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponse response = taskService.create(req);
        
        assertEquals("Learn Spring", response.getTitle());
        verify(taskRepository).save(any(Task.class));
    }
}
