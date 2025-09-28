package uk.gov.hmcts.reform.dev.services;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> getPaginatedTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Task createTask(Task task) {
        if (taskRepository.existsByTitle(task.getTitle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A task with this title already exists");
        }
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTaskStatus(Long id, Task.Status status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found "));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}