package com.example.task.service;

import com.example.task.dto.TaskRequest;
import com.example.task.dto.TaskResponse;
import com.example.task.entity.Task;
import com.example.task.repository.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<TaskResponse> getAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getById(Long id) {
        return repo.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Transactional
    public TaskResponse create(TaskRequest req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        Task saved = repo.save(task);
        return mapToResponse(saved);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest req) {
        Task task = repo.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        return mapToResponse(task);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription());
    }
}
