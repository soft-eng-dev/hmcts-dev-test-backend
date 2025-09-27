package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class TaskFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
    }

    @Test
    void fullTaskLifecycle_withDuplicateCheck() throws Exception {
        // 1. CREATE a task
        Task newTask = new Task();
        newTask.setTitle("My Integration Task");
        newTask.setDescription("Testing full lifecycle");
        newTask.setStatus(Task.Status.PENDING);

        String createResponse = mockMvc.perform(post("/tasks/add-task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Integration Task"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Task createdTask = objectMapper.readValue(createResponse, Task.class);
        Long taskId = createdTask.getId();


        // 3. GET paginated tasks (should include only one)
        mockMvc.perform(
                get("/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].title")
                        .value("My Integration Task"))
                .andExpect(jsonPath("$.totalItems").value(1));

        // 4. GET task by ID
        mockMvc.perform(
                get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("My Integration Task"));

        // 5. UPDATE task status
        mockMvc.perform(
                patch("/tasks/" + taskId + "/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 6. DELETE the task
        mockMvc.perform(
                delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // 7. VERIFY it’s gone
        mockMvc.perform(
                get("/tasks/" + taskId))
                .andExpect(status().isNotFound());

    }
}