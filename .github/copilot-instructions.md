<!-- Copilot instructions for the World Learn repository -->
# World Learn — AI coding assistant guidance

This file contains repository-specific guidance for automated coding agents (Copilot-style assistants). Keep entries concise and concrete: reference files, commands, and exact examples found in the codebase.

1. Big picture
   - Monorepo Maven project (root `pom.xml`) with two modules: `frontend` (JavaFX desktop client) and `backend` (Javalin HTTP server).
   - Frontend: JavaFX app entrypoint `com.worldlearn.frontend.HelloApplication` (see `frontend/src/main/java/.../HelloApplication.java`). UI is FXML-driven under `frontend/src/main/resources/com/worldlearn/frontend/` (e.g. `Auth-view.fxml`, `dashboard-view.fxml`).
   - Backend: lightweight Javalin server entrypoint `com.worldlearn.backend.BackendApplication` (see `backend/src/main/java/.../BackendApplication.java`). Exposes REST endpoints on port 7000 (health `/health`, user APIs under `/api/users`, quizzes, lessons, questions, classes).

2. How to build & run (developer workflows)
   - Build both modules from repository root: `mvn -pl frontend,backend -am clean package` (or simply `mvn clean package` since root is a pom packaging). This compiles both modules and produces artifacts.
   - Run backend locally: from repository root `mvn -pl backend -am exec:java` (backend uses `exec-maven-plugin` mainClass `com.worldlearn.backend.BackendApplication`). The server listens on port 7000.
   - Run frontend (JavaFX): from repository root `mvn -pl frontend -am javafx:run` (frontend config uses `javafx-maven-plugin` with mainClass `com.worldlearn.frontend.HelloApplication`). Ensure JavaFX dependencies are available.
   - Tests: each module uses Maven surefire/junit. Run `mvn -pl frontend test` or `mvn -pl backend test` for module-specific tests.

3. Configuration & environment
   - Database: `backend/src/main/java/com/worldlearn/backend/config/DatabaseConfig.java` contains defaults:
     - URL `jdbc:postgresql://localhost:5432/worldlearn`
     - User `postgres`, password `postgrepw0` (overridable via `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` env vars).
     - The server exits if `Database.testConnection()` fails; ensure Postgres is running and credentials match.
   - Backend port constant referenced in configs is `SERVER_PORT = 7000` in `DatabaseConfig` (endpoints hardcode start port at 7000 in `BackendApplication`).

4. Project-specific patterns & conventions
   - Simple manual DI: Backend wiring happens in `BackendApplication.main` where DAOs -> Services -> Controllers are instantiated and bound to Javalin endpoints. When adding a new service/controller, mirror this pattern.
   - DTOs and models are shared between modules via Maven dependency: `frontend` depends on `backend` artifact to reuse DTOs and model classes (see `frontend/pom.xml` dependency on `com.worldlearn:backend`). Avoid creating duplicate model classes.
   - Frontend resources are loaded via `HelloApplication.class.getResource(...)` and FXML controllers call services in `frontend/src/main/java/com/worldlearn/frontend/services` (e.g., `AuthClientService`, `ApiService`). When adding FXML files, register controllers and call `controller.init(...)` if they require services.
   - Asynchronous API calls: frontend `ApiService` returns CompletableFutures; GUI controllers call `.thenApply` or `.join()` cautiously. Prefer returning CompletableFuture from services to avoid blocking the FX thread.

5. Integration points to watch
   - HTTP API routes: `BackendApplication` registers endpoints under `/api/*`. Frontend `ApiService` constructs requests against `http://localhost:7000` — changing server port requires updating both sides.
   - Database: `Database.getConnection()` uses `DriverManager` with credentials in `DatabaseConfig`. Tests or CI may expect a running Postgres or a mocked DAO.
   - Shared classes: `backend` artifact is a Maven dependency of `frontend` — changing package names or class signatures in backend models/DTOs breaks the frontend compile.

6. Files to consult when making changes (examples)
   - Backend entrypoint and routes: `backend/src/main/java/com/worldlearn/backend/BackendApplication.java`
   - Database configuration: `backend/src/main/java/com/worldlearn/backend/config/DatabaseConfig.java` and `backend/src/main/java/com/worldlearn/backend/database/Database.java`
   - Frontend app & FXML loaders: `frontend/src/main/java/com/worldlearn/frontend/HelloApplication.java` and `frontend/src/main/resources/com/worldlearn/frontend/*.fxml`
   - Frontend HTTP client and services: `frontend/src/main/java/com/worldlearn/frontend/services/ApiService.java` and `AuthClientService.java`
   - Maven module configuration: root `pom.xml`, `frontend/pom.xml`, `backend/pom.xml`

7. Code examples (copyable snippets found in repo)
   - Starting backend (main method): see `BackendApplication.main` — app starts Javalin on port 7000 and registers routes like `app.get("/api/users", userController::getAllUsers);`.
   - Loading FXML in frontend: `FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/worldlearn/frontend/Auth-view.fxml"));` then `AuthController controller = fxmlLoader.getController(); controller.init(auth, stage);`

8. Quick fixes and safe edits guidance
   - When updating model/DTO classes in `backend`, update the `frontend` dependency version (rebuild) and compile both modules. Prefer minimal, interface-preserving changes where possible.
   - Avoid changing public method signatures used by FXML controllers; FXML wiring depends on exact controller method names and fx:id attributes.
   - When modifying database columns/queries, also update corresponding DAO classes under `backend/src/main/java/com/worldlearn/backend/database`.

9. Tests & CI notes
   - The project uses Maven with JaCoCo configured in frontend. Workflows may run `mvn -DskipTests=false clean package` or module-specific test commands.
   - GitHub Actions: there is a workflow file at `.github/workflows/discord.yml` (not CI for build). No existing agent docs were found; add more only if verified.

If any of the above is unclear or you want more detail in a particular area (tests, specific endpoints, or how FXML controllers are initialized), tell me which part to expand and I will iterate.
