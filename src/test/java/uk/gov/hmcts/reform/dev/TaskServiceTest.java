package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTask_shouldSaveTask_whenTitleDoesNotExist() {
        Task task = new Task();
        task.setTitle("New Task");

        when(taskRepository.existsByTitle("New Task")).thenReturn(false);
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createTask(task);
        assertEquals("New Task", result.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void createTask_shouldThrowException_whenTitleExists() {
        Task task = new Task();
        task.setTitle("Duplicate Task");

        when(taskRepository.existsByTitle("Duplicate Task")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).save(task);
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getAllStatuses_shouldReturnAllStatuses() {
        List<Task.Status> statuses = taskService.getAllStatuses();
        assertEquals(Arrays.asList(Task.Status.values()), statuses);
    }
}
