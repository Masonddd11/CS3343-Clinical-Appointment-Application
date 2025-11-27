# Hospital Management System - Backend

## Project Setup

This is a Spring Boot application for managing clinical appointments with intelligent hospital pathfinding.

## Prerequisites

- Java 21
- Maven 3.6+

## Running the Application

1. **Build the project:**

   ```bash
   mvn clean install
   ```

2. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

   Or run the `DemoApplication` class directly from your IDE.

3. **Access H2 Console:**
   - URL: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:file:./data/hospitaldb`
   - Username: `sa`
   - Password: (leave empty)

## Project Structure

```
com.hospital.management.demo
├── model/
│   ├── entity/          # JPA Entities (User, Patient, Doctor, Hospital, etc.)
│   └── enums/           # Enumerations (UserRole, AppointmentStatus, etc.)
├── repository/          # JPA Repositories
├── service/             # Business Logic Services
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── security/            # Security Configuration
├── algorithm/           # Pathfinding Algorithms
└── exception/           # Exception Handling
```

## Database

- **Development:** H2 In-Memory Database
- **Production:** PostgreSQL (configured in application.properties)

## API Endpoints

The application will have REST endpoints following the structure defined in the RPD:

- `/api/auth/**` - Authentication endpoints
- `/api/patients/**` - Patient endpoints
- `/api/doctors/**` - Doctor endpoints
- `/api/hospitals/**` - Hospital endpoints
- `/api/appointments/**` - Appointment endpoints
- `/api/admin/**` - Admin endpoints
