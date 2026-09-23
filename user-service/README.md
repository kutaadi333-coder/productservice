# User Management API

## 1. Project Purpose
This project is part of the Epic Objective to build a strong practical foundation for developing Java Spring Boot microservices. By completing this microservice, the trainee demonstrates the ability to:
* Create a production‑structured Spring Boot service.
* Connect to a relational database (PostgreSQL/MySQL).
* Implement CRUD operations with proper layering.
* Expose REST endpoints following industry best practices.

---

## 2. Technologies Used
* **Java:** Java 17 / 21
* **Framework:** Spring Boot (Web, Data JPA, Validation, Actuator)
* **ORM:** Hibernate / JPA for ORM
* **Database:** PostgreSQL / MySQL (configurable via profiles)
* **Utilities:** Lombok for boilerplate reduction
* **Build Tool:** Apache Maven for build and dependency management
* **Version Control:** Git for version control
* **API Testing:** Postman

---

## 3. Folder Structure
```text
userservice/
├── src/
│   ├── main/
│   │   ├── java/com/company/userservice/
│   │   │   ├── UserserviceApplication.java       # Application entry point
│   │   │   ├── controller/
│   │   │   │   └── ApiController.java             # REST Controllers / Endpoints
│   │   │   ├── service/
│   │   │   │   └── UserService.java               # Business logic layer
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java            # JPA repository interface
│   │   │   ├── entity/
│   │   │   │   └── UserDetails.java               # JPA Entity / Data model
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java    # Centralized error handler
│   │   │       └── DuplicateEmailException.java   # Custom business exceptions
│   │   └── resources/
│   │       └── application.properties             # Database & server configuration
│   └── test/
└── pom.xml                                        # Maven project dependencies


## 4. API List

**Base URL:** `http://127.0.0.1:8080/api/v1/users`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/users` | Create a new user | `201 Created`, `400 Bad Request`, `409 Conflict` |
| **GET** | `/api/v1/users` | List all registered users | `200 OK` |
| **GET** | `/api/v1/users/{id}` | Retrieve user by ID | `200 OK`, `404 Not Found`, `400 Bad Request` |
| **PUT** | `/api/v1/users/{id}` | Update existing user details | `200 OK`, `404 Not Found` |
| **DELETE** | `/api/v1/users/{id}` | Delete a user by ID | `200 OK`, `404 Not Found` |

### Sample JSON Request Body (POST / PUT)
```json
{
  "name": "Alex Mercer",
  "email": "alex.mercer@example.com"
}

## 5. How to Run

### Database Configuration
Ensure `src/main/resources/application.properties` has your local PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/usersdb
spring.datasource.username=postgres
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update