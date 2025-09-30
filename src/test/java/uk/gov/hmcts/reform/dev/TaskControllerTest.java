package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.hmcts.reform.dev.controllers.TaskController;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;


class TaskControllerTest {

    private MockMvc mockMvc;
    private TaskService taskService;
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class); // Mockito mock manually
        taskController = new TaskController(taskService);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDueDate(LocalDate.of(2030, 12, 12));

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.dueDate").value("2030-12-12")); // ✅ correct string
    }

    @Test
    void getTaskSummary_shouldReturnSummary() throws Exception {
        when(taskService.getTotalTasks()).thenReturn(5L);
        when(taskService.getTaskCountsByStatus()).thenReturn(Arrays.asList(
                new Object[]{Task.Status.PENDING, 3L},
                new Object[]{Task.Status.COMPLETED, 2L}
        ));

        mockMvc.perform(get("/tasks/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(5))
                .andExpect(jsonPath("$.statusCounts.PENDING").value(3))
                .andExpect(jsonPath("$.statusCounts.COMPLETED").value(2));
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("New Task");
        task.setDueDate(LocalDate.of(2030, 12, 12));

        when(taskService.createTask(any(Task.class))).thenReturn(task);

        String json = """
                {
                    "title": "New Task",
                    "description": "Test create",
                    "status": "PENDING",
                    "dueDate": "2030-12-12"
                }
                """;

        mockMvc.perform(post("/tasks/add-task")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.dueDate").value("2030-12-12")); // ✅ assert as string
    }

    @Test
    void updateTaskStatus_shouldReturnUpdatedTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task");
        task.setStatus(Task.Status.COMPLETED);
        task.setDueDate(LocalDate.of(2030, 12, 12));

        when(taskService.updateTaskStatus(1L, Task.Status.COMPLETED)).thenReturn(task);

        mockMvc.perform(patch("/tasks/1/status?status=COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}
