# HMCTS Dev Test Backend
This is the backend test submission for the brand new HMCTS case management system that enables caseworkers to efficiently track and manage their daily tasks

## 🚀 Quick Start

### Prerequisites
- Java 21
- PostgreSQL database
- Gradle 8.x or higher

### Database Setup
Set up PostgreSQL locally or use Docker:

```bash
./gradlew build
```
This will compile the code, run tests, and generate the JAR file.

### Running the Application

#### Using Gradle
```bash
./gradlew bootRun
```
The application will start on `http://localhost:4000`

### Base URL
```
http://localhost:4000
```

## API Docs

All backend endpoints are documented in Swagger. Click below to explore them:

[Task Management API - Swagger UI](http://localhost:4000/swagger-ui/index.html)

## Project Structure

```
src/
├── main/
│   ├── java/uk/gov/hmcts/reform/dev/
│   │   ├── Application.java              # Main Spring Boot application
│   │   ├── controllers/
│   │   │   ├── CaseController.java       # Example Case Controller
│   │   │   ├── RootController.java       # Root/welcome endpoints
│   │   │   └── TaskController.java       # Task management endpoints
│   │   ├── models/
│   │   │   └── Task.java                 # Task entity model
│   │   ├── repositories/
│   │   │   └── TaskRepository.java       # Data access layer
│   │   ├── services/
│   │   │   └── TaskService.java          # Business logic
│   │   └── seeder/
│   │       └── DataSeeder.java           # Database seeding
│   └── resources/
│       └── application.yaml              # Application configuration
├── functionalTest/                       # Functional tests
├── integrationTest/                      # Integration tests
├── smokeTest/                           # Smoke tests
└── test/                                # Unit tests
│   ├── TaskControllerTest.java
│   └── TaskServiceTest.java
```
## Database Configuration

### Development
The application is configured to work with PostgreSQL. Update your `application.yaml` with your database credentials on the .env file. The configuration of the env structure is found below.


## Environment Variables

The backend requires some environment variables to run. Create a `.env` file in the project root with the following structure:

```env
# Database configuration
DB_HOST=your_database_host          # e.g., localhost
DB_NAME=your_database_name          # e.g., case_manager
DB_PORT=your_database_port          # e.g., 5432
DB_USER_NAME=your_database_user     # e.g., case_user
DB_PASSWORD=your_database_password  # e.g., supersecretpassword
SERVER_PORT=your_server_port        # e.g., 4000
```
# 🎯 Behavior Driven Development (BDD) Scenarios

## 🎯 BDD (Behavior Driven Development)

### Task Management Flow

| Behavior | Input | Output |
|----------|-------|--------|
| **Create a new task** | `POST /tasks/add-task` with task data | Task created successfully with auto-generated ID |
| **View all tasks** | `GET /tasks` | Returns an array of all tasks |
| **View paginated tasks** | `GET /tasks?page=0&size=10` | Returns paginated list of tasks (10 per page) |
| **Update task status** | `PATCH /tasks/{id}/status` with new status | Task status updated and returned |
| **Delete a task** | `DELETE /tasks/{id}` | Task removed from system (`204 No Content`) |
| **Get task by ID** | `GET /tasks/{id}` | Returns specific task details or `404 Not Found` |
| **Get summary of tasks** | `GET /summary` | Returns a summary count of total tasks and per status counts  |
| **View API documentation** | Navigate to `/swagger-ui.html` | Interactive API documentation displayed |

## 🐛 Known Issues
- - **Task Status Validation** – Backend does not enforce valid status transitions. Although the frontend `<select>` limits the choices, it is possible to re-select the current status.
- - **Database Pool Name** - An unresolved error solved by a string 



