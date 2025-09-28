package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void smokeTest_getTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.totalItems").value(taskRepository.count()));
    }

    @Test
    void smokeTest_getTaskById() throws Exception {
        Long id = taskRepository.findAll().get(17).getId(); // ✅ seeded task
        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void smokeTest_createTask_andCleanup() throws Exception {
        String title = "SmokeTest Task";

        // ✅ ensure old test data is removed first
        taskRepository.findAll()
                .stream()
                .filter(t -> title.equals(t.getTitle()))
                .forEach(t -> taskRepository.deleteById(t.getId()));

        String newTaskJson = """
                {
                  "title": "SmokeTest Task",
                  "description": "Created from smoke test",
                  "status": "PENDING",
                  "dueDate": "12-12-2030"
                }
                """;

        mockMvc.perform(post("/tasks/add-task")
                .contentType("application/json")
                .content(newTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title));

        // ✅ cleanup after test
        taskRepository.findAll()
                .stream()
                .filter(t -> title.equals(t.getTitle()))
                .forEach(t -> taskRepository.deleteById(t.getId()));
    }

    @Test
    void smokeTest_updateTaskStatus() throws Exception {
        Long id = taskRepository.findAll().get(0).getId(); // ✅ seeded task
        mockMvc.perform(patch("/tasks/{id}/status?status=COMPLETED", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void smokeTest_deleteTask() throws Exception {
        // ✅ create a temporary task just for deletion
        Task task = new Task("TempDelete", "Task created for delete test",
                Task.Status.PENDING, java.time.LocalDate.now().plusDays(1));
        Task saved = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }
}