# AGENTS.md - Boopoom Project Guidelines

## Project Overview

Boopoom is a Spring Boot 4.0.3 application with Java 21 using Gradle. It's a product trading platform with JPA entities, Spring Security, Thymeleaf templates, and MySQL database.

## Build Commands

### Gradle Wrapper
```bash
./gradlew build          # Build the project (compiles, tests)
./gradlew bootRun       # Run the application
./gradlew clean         # Clean build artifacts
./gradlew test          # Run all tests
```

### Running Tests
```bash
./gradlew test --tests "BoopoomApplicationTests"         # Run specific test class
./gradlew test --tests "BoopoomApplicationTests.test*"  # Run tests matching pattern
./gradlew test --tests "*ServiceTest"                    # Run all service tests
./gradlew test --info                                     # Run tests with verbose output
```

### Other Useful Commands
```bash
./gradlew bootJar           # Build executable JAR
./gradlew dependencies      # List all dependencies
./gradlew --status         # Check Gradle daemon status
./gradlew compileJava      # Compile Java sources only
```

## Project Structure
```
src/main/java/com/example/boopoom/
├── BoopoomApplication.java          # Main entry point
├── domain/                          # JPA entities (User, Trade, etc.)
│   ├── product/                     # Product hierarchy (inheritance)
│   ├── Platform.java                # Enum: STEAM, EPIC_GAMES, etc.
│   ├── DamageStatus.java            # Enum: NEW, DAMAGED, etc.
│   └── TradeStatus.java             # Enum: PENDING, COMPLETED, etc.
├── repository/                      # Data access layer
├── service/                         # Business logic
├── web/                             # Controllers
│   └── forms/                       # Form DTOs for Thymeleaf
└── exception/                       # Custom exceptions

src/main/resources/
├── application.properties           # Configuration
├── templates/                       # Thymeleaf views
└── static/                          # CSS, JS assets
```

## Code Style

### Naming Conventions
- **Classes/Interfaces**: PascalCase (`UserService`, `TradeController`, `Product`)
- **Methods**: camelCase (`findUsers()`, `createTrade()`, `findByEmail()`)
- **Variables**: camelCase (`userId`, `nickName`, `passwordHash`)
- **Constants**: UPPER_SNAKE_CASE (`POINT_AMOUNT`, `INITIAL_POINT`)
- **Packages**: lowercase (`com.example.boopoom.domain`)
- **Test Classes**: `ClassNameTests` (e.g., `UserServiceTests`)

### Formatting
- **Indentation**: 4 spaces (no tabs)
- **Braces**: Same-line opening braces
- **Line length**: Under 120 characters
- **No spaces** after method names: `findUsers()` not `findUsers ()`

### Import Order
1. `java`/`javax` packages
2. `org.springframework` packages
3. Other external libraries
4. Internal project packages

## Entity Design

### Basic Entity Pattern
```java
@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String nickName;
    private String email;
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<Trade> trades = new ArrayList<>();

    public static User createUser(String nickName, String email, String password) {
        User user = new User();
        user.setNickName(nickName);
        user.setEmail(email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPasswordHash(encoder.encode(password));
        return user;
    }
}
```

### JPA Conventions
- Use `@Entity`, `@Table`, `@Column` annotations
- Use `@Id`, `@GeneratedValue` for primary keys
- Use `@Enumerated(EnumType.STRING)` for enums
- Use lazy fetching (`FetchType.LAZY`) for relationships
- Use `@OneToMany`, `@ManyToOne`, `@ManyToMany` with `mappedBy`
- Entities require no-arg constructor (Lombok handles this)
- Use factory methods (`createUser()`) for object creation, not constructors

## Service Layer

### Service Pattern
```java
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Long join(User user) {
        validateDuplicate(user);
        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicate(User user) {
        if (!userRepository.findByEmail(user.getEmail()).isEmpty()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }
}
```

### Service Conventions
- Use `@Service` annotation
- Use `@Transactional(readOnly = true)` at class level
- Use `@Transactional` at method level for write operations
- Use constructor injection (field injection with `@Autowired` is acceptable)
- Validate business rules with descriptive error messages (Korean)

## Controller Layer

### Controller Pattern
```java
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/createUserForm";
    }

    @PostMapping("/users/new")
    public String create(@Valid UserForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "users/createUserForm";
        }
        User user = User.createUser(form.getNickName(), form.getEmail(), form.getPassword());
        userService.join(user);
        return "redirect:/";
    }
}
```

### Controller Conventions
- Use `@Controller` for Thymeleaf, `@RestController` for APIs
- Use `@RequiredArgsConstructor` for constructor injection
- Use `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Use `@Valid` for form validation
- Return view names (String) or `@ResponseBody` for JSON

## Repository Layer

### Repository Pattern
```java
@Repository
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findOne(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findByEmail(String email) {
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
    }
}
```

### Repository Conventions
- Use `@Repository` annotation (optional with Spring Data JPA)
- Use JPQL queries with named parameters (` :paramName`)
- Return `List<T>` for collections, not arrays
- Use `findOne()` for single results (may return null)

## Validation

- Use Bean Validation annotations: `@NotNull`, `@NotBlank`, `@Size`, `@Email`
- Apply `@Valid` on request bodies in controllers
- Handle `BindingResult` for form validation errors

## Error Handling

- Throw `IllegalStateException` for business rule violations
- Use descriptive Korean error messages
- Create custom exceptions in `exception/` package when needed
- Consider `@ControllerAdvice` for global exception handling

## Testing

```java
@SpringBootTest
class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    void join_shouldCreateUser() {
        User user = User.createUser("nick", "test@test.com", "password123");
        Long id = userService.join(user);
        assertThat(id).isNotNull();
    }
}
```

### Test Conventions
- Use JUnit 5 (`org.junit.jupiter.api.Test`)
- Use `@SpringBootTest` for integration tests
- Name test methods: `methodName_shouldExpectedBehavior()`
- Test classes: `ClassNameTests.java`

## Security

- Passwords hashed with `BCryptPasswordEncoder`
- Use Spring Security for authentication/authorization
- Never log sensitive data (passwords, tokens)

## Configuration

- Database: MySQL (configured in `application.properties`)
- JPA schema: auto-generated (`spring.jpa.hibernate.ddl-auto=update`)
- Enable SQL logging: `spring.jpa.show-sql=true`

## Logging

- Use SLF4J (`Logger`, `LoggerFactory`)
- Log levels: `INFO` for operations, `ERROR` for failures
- Avoid logging sensitive data

## Thymeleaf Templates

- Templates in `src/main/resources/templates/`
- Use `th:text`, `th:each`, `th:if`, `th:href`, `th:field`
- Access Spring beans via `@beanName` or `T(com.example.ClassName)`

## Development Workflow

1. Run `./gradlew build` before committing
2. Run tests with `./gradlew test`
3. Use `./gradlew bootRun` for local development
4. Verify database schema auto-generation

## Notes

- Application uses Korean in business logic messages
- Enum values: `STEAM`, `EPIC_GAMES`, `NEW`, `DAMAGED`, `PENDING`, `COMPLETED`
- Uses JPA inheritance: `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`
