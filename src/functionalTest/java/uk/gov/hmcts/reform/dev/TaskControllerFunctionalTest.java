package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TaskControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullTaskLifecycle() throws Exception {
        // 1. Create a task
        String newTaskJson = """
                {
                  "title": "FunctionalTest Task",
                  "description": "Testing full lifecycle",
                  "status": "PENDING",
                  "dueDate": "20-12-2028"
                }
                """;

        String location = mockMvc.perform(post("/tasks/add-task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("FunctionalTest Task"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from JSON
        Long taskId = com.jayway.jsonpath.JsonPath.parse(location).read("$.id", Long.class);

        // 2. Fetch by ID
        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("FunctionalTest Task"));

        // 3. Update status
        mockMvc.perform(patch("/tasks/{id}/status?status=COMPLETED", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 4. Delete task
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        // 5. Verify deletion
        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

}
