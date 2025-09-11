package uk.gov.hmcts.reform.dev.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final Random random = new Random();

    public DataSeeder(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        // Only seed if DB is greater than 4 due to postman testing 
        
        if (taskRepository.count() < 4) {
            for (int i = 1; i <= 20; i++) {
                Task.Status status;
                int r = random.nextInt(4);
                switch (r) {
                    case 0 -> status = Task.Status.PENDING;
                    case 1 -> status = Task.Status.IN_PROGRESS;
                    case 2 -> status = Task.Status.COMPLETED;
                    default -> status = Task.Status.CANCELLED;
                }

                Task task = new Task(
                        "TASK-" + i,
                        "This is the description for task #" + i,
                        status,
                        LocalDateTime.now().plusDays(random.nextInt(30))
                );

                taskRepository.save(task);
            }
            System.out.println("Seeded 20 tasks into the database!");
        } else {
            System.out.println("Database already has tasks. Skipping seeding.");
        }
    }
}