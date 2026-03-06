# AGENTS.md - Boopoom Coding Guide for Agents

This file defines coding and workflow conventions for agentic tools working in this repo.
It is based on current source code, Gradle config, and repository layout.

## Rule Sources (Cursor/Copilot)
- `.cursor/rules/`: not present
- `.cursorrules`: not present
- `.github/copilot-instructions.md`: not present
- This `AGENTS.md` is the primary instruction file for coding agents.

## Project Snapshot
- Stack: Java 21, Spring Boot 4.0.3, Gradle Wrapper, Thymeleaf, Spring MVC, Spring Data JPA, Spring Security.
- Persistence: MySQL via `com.mysql:mysql-connector-j`.
- Package root: `com.example.boopoom`.
- Architecture: controller (`web`) -> service (`service`) -> repository (`repository`) -> domain (`domain`).
- Domain includes JPA inheritance for products (`Gpu`, `Ram`, `Ssd`).
- Tests currently use JUnit 5 + `@SpringBootTest`.

## Repository Layout
- `src/main/java/com/example/boopoom/domain`: entities, enums, search DTOs.
- `src/main/java/com/example/boopoom/domain/product`: abstract `Product` and concrete subtypes.
- `src/main/java/com/example/boopoom/repository`: `EntityManager`-based repositories.
- `src/main/java/com/example/boopoom/service`: transactional business logic.
- `src/main/java/com/example/boopoom/web`: MVC controllers and form DTOs.
- `src/main/resources/templates`: Thymeleaf templates.
- `src/test/java`: integration tests.

## Build, Lint, and Test Commands
Run all commands from repository root (`/Users/jyyang/Desktop/project/boopoom`).

### Build and Run
```bash
./gradlew clean
./gradlew build
./gradlew bootRun
./gradlew bootJar
./gradlew compileJava
```

### Verification / Lint
```bash
./gradlew check
./gradlew test
```
- There is no dedicated lint plugin configured (no Checkstyle/Spotless/PMD task in `build.gradle`).
- Treat `./gradlew check` and `./gradlew compileJava` as the baseline quality gates.

### Single Test Execution (Important)
```bash
# Single test class (preferred format)
./gradlew test --tests "com.example.boopoom.BoopoomApplicationTests"

# Single test method
./gradlew test --tests "com.example.boopoom.BoopoomApplicationTests.contextLoads"

# Pattern match
./gradlew test --tests "*ApplicationTests"
```
- Add `--info` when debugging failing tests.

### Useful Diagnostics
```bash
./gradlew tasks --all
./gradlew dependencies
./gradlew --status
```

## Java and Spring Style Conventions

### Formatting
- Use 4 spaces for indentation.
- Use same-line opening braces.
- Keep lines near 120 chars max.
- Use one blank line between logical sections; avoid excessive vertical whitespace.
- Avoid trailing whitespace.

### Imports
- Prefer explicit imports over wildcard imports in new/edited code.
- Suggested import order:
  1) `java.*`
  2) `jakarta.*`
  3) `org.springframework.*`
  4) `lombok.*`
  5) `com.example.boopoom.*`
- Keep static imports grouped separately at the end.
- When touching legacy files, avoid unrelated import churn.

### Naming
- Classes/interfaces/enums: `PascalCase`.
- Methods/fields/params/local vars: `camelCase`.
- Constants: `UPPER_SNAKE_CASE`.
- Packages: lowercase.
- Test classes: `ClassNameTests`.
- Test methods: `methodName_shouldExpectedBehavior` when practical.

### Types and Data Modeling
- Use `Long` for entity identifiers.
- Use `List<T>` for collections.
- Use `LocalDateTime` for timestamps (existing pattern in `User`, `Trade`, `TradeSearch`).
- Use enums for finite state (`TradeStatus`, `DamageStatus`, `Platform`).
- Prefer primitives (`int`) only when null is not a valid state.
- Use DTO/form types under `web/forms` for controller binding.

### Lombok
- Existing code uses `@Getter`, `@Setter`, and `@RequiredArgsConstructor` heavily.
- Keep Lombok usage consistent with the surrounding file.
- Do not introduce Lombok features that obscure behavior (e.g., broad `@Data` on entities).

## Layer-Specific Conventions

### Domain / Entity Layer
- Use `@Entity` with JPA annotations (`@Id`, `@GeneratedValue`, `@Column`, `@Enumerated`).
- Keep association mappings explicit (`@ManyToOne`, `@OneToMany`, `mappedBy`, lazy loading).
- Keep domain behavior in entities where appropriate (`Trade.cancel`, `Trade.complete`).
- Prefer static factory methods (`createUser`, `createTrade`, `createGpu/createRam/createSsd`).
- Maintain both sides of bidirectional associations via convenience methods.

### Repository Layer
- Repositories are `EntityManager`-based classes, not Spring Data interfaces.
- Use JPQL with named parameters.
- Return domain objects / `List<T>` directly.
- Keep query construction readable; avoid hard-coded magic limits unless documented.

### Service Layer
- Annotate services with `@Service`.
- Default to `@Transactional(readOnly = true)` at class level.
- Add method-level `@Transactional` for write operations.
- Keep business validation in services before persistence.
- Inject dependencies via constructor (`@RequiredArgsConstructor` preferred).

### Web Layer (Thymeleaf MVC)
- Use `@Controller` and view-name returns for server-rendered pages.
- Bind inputs via form DTOs and `@ModelAttribute` / `@RequestParam`.
- Use `@Valid` + `BindingResult` when validation rules are present.
- Keep controllers thin: delegate core logic to services.

## Error Handling and Validation
- Use runtime exceptions for business rule violations (`IllegalStateException`, custom exceptions).
- Custom business exceptions belong in `src/main/java/com/example/boopoom/exception`.
- User-facing business messages are often Korean; keep new messages consistent with feature context.
- Add bean validation annotations (`@NotBlank`, `@Email`, etc.) on form fields when introducing new forms.

## Security and Sensitive Data
- Password hashing uses `BCryptPasswordEncoder` in domain factory logic.
- Never log secrets, credentials, or password hashes.
- `application.properties` currently contains local DB credentials; do not duplicate secrets in code/tests/docs.
- Prefer environment-specific overrides for local secrets.

## Testing Guidelines
- Use JUnit 5 (`org.junit.jupiter.api.Test`).
- Use `@SpringBootTest` for integration-style coverage.
- Add focused tests for service business rules and repository query behavior.
- Keep tests deterministic; avoid reliance on mutable shared state.

## Agent Workflow Expectations
- Make minimal, targeted changes aligned with existing architecture.
- Preserve package boundaries and naming conventions.
- Run relevant commands before finalizing:
  - minimum: `./gradlew test --tests "<target>"` for touched logic
  - preferred before handoff: `./gradlew test` or `./gradlew build`
- Do not add new dependencies/plugins without clear need.
- Document any intentional deviations in PR/commit notes.
