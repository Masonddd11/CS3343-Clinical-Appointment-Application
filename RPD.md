# Requirements and Project Documentation (RPD)

## Clinical Appointment Management System

---

## 1. Project Overview

### 1.1 Purpose

The Clinical Appointment Management System is a web-based application designed to streamline the process of scheduling, managing, and tracking medical appointments between patients and healthcare providers. The system aims to reduce administrative overhead, minimize scheduling conflicts, and improve patient experience.

### 1.2 Scope

This application will serve three primary user types:

- **Patients**: Can book, view, reschedule, and cancel appointments based on departments and receive intelligent hospital recommendations using pathfinding algorithms
- **Healthcare Providers (Doctors)**: Can manage their schedules, view appointments, and update availability
- **Administrators**: Can manage users, appointments, system settings, hospitals, and departments

The system supports multiple hospitals across Hong Kong with intelligent routing recommendations based on geographic location, hospital capacity/intensity, and operational status (including closures due to epidemics/流行病).

### 1.3 Technology Stack

- **Backend**: Spring Boot 3.5.7, Java 21
- **Framework**: Spring Web, Spring Data JPA, Spring Security
- **Database**: (To be specified - PostgreSQL/MySQL recommended with PostGIS extension for geographic data)
- **Build Tool**: Maven
- **Additional Tools**: Lombok
- **Pathfinding Algorithm**: Dijkstra's or A\* algorithm for optimal hospital routing
- **Geographic Services**: Integration with Hong Kong geographic coordinate system (WGS84/HK1980)

---

## 2. Functional Requirements

### 2.1 User Management

#### 2.1.1 User Registration & Authentication

- **REQ-1.1**: System shall allow patients to register with personal information (name, email, phone, date of birth)
- **REQ-1.2**: System shall allow healthcare providers and administrators to be registered (by administrators only)
- **REQ-1.3**: System shall implement secure authentication using Spring Security
- **REQ-1.4**: System shall support password encryption/hashing
- **REQ-1.5**: System shall maintain different user roles: PATIENT, DOCTOR, ADMIN

#### 2.1.2 User Profiles

- **REQ-1.6**: Patients shall be able to view and edit their profile information including address/location for pathfinding
- **REQ-1.7**: Doctors shall be able to manage their profile including specialization, qualifications, and bio
- **REQ-1.8**: System shall display user profile information on appointment details

### 2.1.3 Hospital and Department Management

- **REQ-1.9**: System shall support multiple hospitals across Hong Kong with geographic coordinates (latitude, longitude)
- **REQ-1.10**: System shall maintain hospital information: name, address, coordinates, capacity/intensity level, operational status
- **REQ-1.11**: System shall track hospital closure status due to epidemics or other emergencies (流行病)
- **REQ-1.12**: System shall support department-based organization (e.g., Urology, Cardiology, Pediatrics)
- **REQ-1.13**: System shall map departments to hospitals (hospitals may have multiple departments)
- **REQ-1.14**: System shall maintain symptom-to-department mapping (e.g., kidney pain → Urology, chest pain → Cardiology)

### 2.2 Appointment Management

#### 2.2.1 Appointment Booking

- **REQ-2.1**: Patients shall be able to search for available doctors by specialization
- **REQ-2.1a**: Patients shall be able to select or describe symptoms, and system shall recommend appropriate department
- **REQ-2.1b**: System shall allow patients to directly select a department (e.g., Urology for kidney pain)
- **REQ-2.2**: Patients shall be able to view available time slots for a selected doctor
- **REQ-2.3**: Patients shall be able to book an appointment with a doctor for a specific date and time
- **REQ-2.3a**: System shall recommend optimal hospitals based on patient location using pathfinding algorithm
- **REQ-2.4**: System shall prevent double-booking (same doctor at same time)
- **REQ-2.5**: System shall enforce booking constraints (e.g., appointments can only be made during business hours)
- **REQ-2.6**: System shall require patients to provide reason for visit during booking

#### 2.2.2 Appointment Viewing

- **REQ-2.7**: Patients shall be able to view all their upcoming and past appointments
- **REQ-2.8**: Doctors shall be able to view their appointment schedule (daily, weekly views)
- **REQ-2.9**: System shall display appointment details: date, time, patient name, reason for visit, status

#### 2.2.3 Appointment Modifications

- **REQ-2.10**: Patients shall be able to reschedule appointments (at least 24 hours before)
- **REQ-2.11**: Patients shall be able to cancel appointments (at least 24 hours before)
- **REQ-2.12**: Doctors shall be able to mark appointments as completed
- **REQ-2.13**: System shall automatically update appointment status (upcoming, completed, cancelled)
- **REQ-2.14**: System shall notify users via email when appointments are booked/rescheduled/cancelled (EXTRA)

#### 2.2.4 Appointment Status

- **REQ-2.15**: System shall track appointment status: PENDING, CONFIRMED, COMPLETED, CANCELLED
- **REQ-2.16**: System shall automatically confirm appointments upon booking

### 2.3 Schedule Management

#### 2.3.1 Doctor Availability

- **REQ-3.1**: Doctors shall be able to set their working hours (days and time slots)
- **REQ-3.2**: Doctors shall be able to mark specific dates as unavailable (holidays, leave)
- **REQ-3.3**: Doctors shall be able to block time slots for breaks or other activities
- **REQ-3.4**: System shall only show available time slots to patients during booking
- **REQ-3.5**: System shall prevent booking in unavailable time slots

### 2.4 Search and Filtering

#### 2.4.1 Doctor Search

- **REQ-4.1**: Patients shall be able to search doctors by name
- **REQ-4.2**: Patients shall be able to filter doctors by specialization
- **REQ-4.3**: System shall display doctor information: name, specialization, availability status

### 2.6 Hospital Pathfinding and Recommendation

#### 2.6.1 Geographic Pathfinding

- **REQ-6.1**: System shall implement pathfinding algorithm (Dijkstra's or A\*) to find optimal hospitals based on patient location
- **REQ-6.2**: Pathfinding algorithm shall consider multiple weighted factors:
  - **REQ-6.2a**: Distance from patient location to hospital (geographic distance in kilometers)
  - **REQ-6.2b**: Hospital capacity/intensity level (lower intensity preferred for better service)
  - **REQ-6.2c**: Hospital operational status (closed hospitals due to epidemics/流行病 excluded or heavily penalized)
- **REQ-6.3**: System shall calculate weighted scores combining distance, intensity, and operational status
- **REQ-6.4**: System shall rank hospitals by optimal score and present top recommendations to patients
- **REQ-6.5**: System shall only recommend hospitals that:
  - Have the requested department available
  - Are currently operational (not closed due to epidemics)
  - Have available appointments in the requested timeframe

#### 2.6.2 Weight Calculation

- **REQ-6.6**: System shall use configurable weights for pathfinding factors (default: distance 40%, intensity 30%, operational status 30%)
- **REQ-6.7**: Closed hospitals (due to epidemics/流行病) shall receive maximum weight penalty or be excluded entirely
- **REQ-6.8**: System shall normalize distance (0-1 scale) based on maximum expected distance in Hong Kong
- **REQ-6.9**: System shall normalize intensity/capacity (0-1 scale) where 0 = low intensity (preferred), 1 = high intensity

### 2.5 Administrative Functions

#### 2.5.1 User Management

- **REQ-5.1**: Administrators shall be able to view all users (patients, doctors)
- **REQ-5.2**: Administrators shall be able to create, edit, and deactivate user accounts
- **REQ-5.3**: Administrators shall be able to assign doctor specializations

#### 2.5.2 Appointment Oversight

- **REQ-5.4**: Administrators shall be able to view all appointments across the system
- **REQ-5.5**: Administrators shall be able to cancel any appointment with reason
- **REQ-5.6**: Administrators shall be able to generate appointment reports (EXTRA)

#### 2.5.3 Hospital Management

- **REQ-5.7**: Administrators shall be able to add, edit, and manage hospital information (name, address, coordinates)
- **REQ-5.8**: Administrators shall be able to update hospital capacity/intensity levels
- **REQ-5.9**: Administrators shall be able to mark hospitals as closed due to epidemics (流行病) or other emergencies
- **REQ-5.10**: Administrators shall be able to set closure start and end dates for hospitals
- **REQ-5.11**: Administrators shall be able to assign departments to hospitals

#### 2.5.4 Department Management

- **REQ-5.12**: Administrators shall be able to create and manage departments
- **REQ-5.13**: Administrators shall be able to configure symptom-to-department mappings
- **REQ-5.14**: Administrators shall be able to assign doctors to departments

---

## 3. Non-Functional Requirements

### 3.1 Performance

- **REQ-NF-1**: System shall respond to user requests within 2 seconds for standard operations
- **REQ-NF-2**: System shall support at least 100 concurrent users
- **REQ-NF-15**: Pathfinding algorithm shall complete calculations within 1 second for up to 50 hospitals
- **REQ-NF-16**: Geographic distance calculations shall be optimized for real-time performance

### 3.2 Security

- **REQ-NF-3**: System shall implement role-based access control (RBAC)
- **REQ-NF-4**: System shall protect sensitive patient data (HIPAA considerations)
- **REQ-NF-5**: All API endpoints shall require authentication except registration/login
- **REQ-NF-6**: Users shall only access data they are authorized to view

### 3.3 Usability

- **REQ-NF-7**: System shall provide intuitive navigation and user interface
- **REQ-NF-8**: System shall display clear error messages and validation feedback
- **REQ-NF-9**: System shall be responsive and accessible on desktop devices

### 3.4 Reliability

- **REQ-NF-10**: System shall validate all user inputs before processing
- **REQ-NF-11**: System shall handle concurrent booking attempts gracefully
- **REQ-NF-12**: System shall maintain data integrity and consistency

### 3.5 Maintainability

- **REQ-NF-13**: Code shall follow Java naming conventions and best practices
- **REQ-NF-14**: System shall include proper error handling and logging

---

## 4. Extra Features (Extended Requirements)

### 4.1 Notifications

- **EXTRA-1**: Email notifications for appointment confirmations, reminders, and cancellations
- **EXTRA-2**: In-app notification system for users

### 4.2 Reporting & Analytics

- **EXTRA-3**: Appointment statistics dashboard for administrators
- **EXTRA-4**: Doctor availability and booking rate reports
- **EXTRA-5**: Patient appointment history export

### 4.3 Advanced Features

- **EXTRA-6**: Appointment reminder notifications (24 hours before)
- **EXTRA-7**: Waitlist functionality for fully booked time slots
- **EXTRA-8**: Rating and review system for doctors (after appointment completion)
- **EXTRA-9**: Recurring appointment booking option
- **EXTRA-10**: Appointment notes/medical history tracking

### 4.4 Data Management

- **EXTRA-11**: Soft delete functionality (preserve data for historical records)
- **EXTRA-12**: Audit logging for critical operations
- **EXTRA-13**: Data backup and recovery mechanisms

### 4.5 Advanced Pathfinding Features

- **EXTRA-14**: Real-time traffic integration for more accurate travel time estimation
- **EXTRA-15**: Public transportation route recommendations (MTR, bus routes) to hospitals
- **EXTRA-16**: Historical hospital performance metrics in pathfinding algorithm
- **EXTRA-17**: Multi-criteria optimization (allow patients to prioritize distance vs. intensity)
- **EXTRA-18**: Predictive closure alerts (notify patients if hospital might close before appointment date)

---

## 5. System Architecture

### 5.1 High-Level Architecture

```text
┌─────────────┐
│   Browser   │
│  (Frontend) │
└──────┬──────┘
       │
       │ HTTP/REST API
       │
┌──────▼─────────────────────────────────┐
│      Spring Boot Application           │
│  ┌──────────────────────────────────┐  │
│  │   Controller Layer (REST APIs)   │  │
│  ├──────────────────────────────────┤  │
│  │   Service Layer (Business Logic) │  │
│  ├──────────────────────────────────┤  │
│  │   Repository Layer (Data Access) │  │
│  ├──────────────────────────────────┤  │
│  │   Security Layer (Auth/Authorization)│
│  └──────────────────────────────────┘  │
└──────┬─────────────────────────────────┘
       │
       │ JPA/Hibernate
       │
┌──────▼──────┐
│  Database   │
│ (PostgreSQL)│
└─────────────┘
```

### 5.2 Package Structure

```text
com.hospital.management.demo
├── controller          (REST Controllers)
│   ├── PatientController
│   ├── DoctorController
│   ├── AppointmentController
│   └── AdminController
├── service             (Business Logic)
│   ├── PatientService
│   ├── DoctorService
│   ├── AppointmentService
│   └── UserService
├── repository          (Data Access)
│   ├── UserRepository
│   ├── PatientRepository
│   ├── DoctorRepository
│   └── AppointmentRepository
├── model/entity        (Domain Models)
│   ├── User
│   ├── Patient
│   ├── Doctor
│   ├── Appointment
│   ├── Hospital
│   ├── Department
│   ├── Symptom
│   ├── HospitalDepartment
│   └── TimeSlot
├── algorithm           (Pathfinding Algorithms)
│   ├── HospitalPathfinder
│   ├── PathfindingAlgorithm
│   └── WeightCalculator
├── dto                 (Data Transfer Objects)
├── security            (Security Configuration)
│   ├── SecurityConfig
│   └── JwtUtil (if using JWT)
└── exception           (Exception Handling)
    └── GlobalExceptionHandler
```

---

## 6. Data Model

### 6.1 Core Entities

#### User Entity

- `id` (Long, Primary Key)
- `email` (String, Unique)
- `password` (String, Encrypted)
- `role` (Enum: PATIENT, DOCTOR, ADMIN)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)
- `isActive` (Boolean)

#### Patient Entity

- `id` (Long, Primary Key)
- `user` (User, One-to-One)
- `firstName` (String)
- `lastName` (String)
- `phoneNumber` (String)
- `dateOfBirth` (LocalDate)
- `address` (String, Optional)
- `latitude` (Double, Optional - for pathfinding)
- `longitude` (Double, Optional - for pathfinding)
- `district` (String, Optional - Hong Kong district)

#### Doctor Entity

- `id` (Long, Primary Key)
- `user` (User, One-to-One)
- `hospital` (Hospital, Many-to-One)
- `department` (Department, Many-to-One)
- `firstName` (String)
- `lastName` (String)
- `phoneNumber` (String)
- `specialization` (String)
- `qualifications` (String)
- `bio` (String, Optional)
- `isAvailable` (Boolean)

#### Appointment Entity

- `id` (Long, Primary Key)
- `patient` (Patient, Many-to-One)
- `doctor` (Doctor, Many-to-One)
- `hospital` (Hospital, Many-to-One)
- `department` (Department, Many-to-One)
- `appointmentDate` (LocalDate)
- `appointmentTime` (LocalTime)
- `reasonForVisit` (String)
- `symptoms` (String, Optional - for department matching)
- `status` (Enum: PENDING, CONFIRMED, COMPLETED, CANCELLED)
- `pathfindingScore` (Double, Optional - score used for hospital recommendation)
- `notes` (String, Optional)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

#### Hospital Entity

- `id` (Long, Primary Key)
- `name` (String)
- `address` (String)
- `latitude` (Double, for geographic coordinates)
- `longitude` (Double, for geographic coordinates)
- `district` (String, Hong Kong district)
- `capacity` (Integer, maximum patient capacity)
- `currentIntensity` (Double, 0.0-1.0 scale, current utilization/congestion level)
- `operationalStatus` (Enum: OPERATIONAL, CLOSED_EPIDEMIC, CLOSED_OTHER, PARTIAL_SERVICE)
- `closureReason` (String, Optional - e.g., "流行病", "Maintenance")
- `closureStartDate` (LocalDateTime, Optional)
- `closureEndDate` (LocalDateTime, Optional)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

#### Department Entity

- `id` (Long, Primary Key)
- `name` (String, Unique - e.g., "Urology", "Cardiology")
- `code` (String, Unique department code)
- `description` (String, Optional)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

#### HospitalDepartment Entity (Junction table)

- `id` (Long, Primary Key)
- `hospital` (Hospital, Many-to-One)
- `department` (Department, Many-to-One)
- `isActive` (Boolean)
- `createdAt` (LocalDateTime)

#### Symptom Entity (EXTRA - for department recommendation)

- `id` (Long, Primary Key)
- `symptom` (String, e.g., "kidney pain", "chest pain")
- `recommendedDepartment` (Department, Many-to-One)
- `priority` (Integer, for ranking when multiple matches exist)
- `createdAt` (LocalDateTime)

#### TimeSlot Entity (EXTRA - for schedule management)

- `id` (Long, Primary Key)
- `doctor` (Doctor, Many-to-One)
- `dayOfWeek` (DayOfWeek)
- `startTime` (LocalTime)
- `endTime` (LocalTime)
- `isAvailable` (Boolean)

---

## 7. API Endpoints

### 7.1 Authentication

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### 7.2 Patient Endpoints

- `GET /api/patients/profile` - Get patient profile
- `PUT /api/patients/profile` - Update patient profile (including location)
- `GET /api/patients/appointments` - Get patient's appointments
- `POST /api/patients/appointments` - Book new appointment
- `PUT /api/patients/appointments/{id}` - Reschedule appointment
- `DELETE /api/patients/appointments/{id}` - Cancel appointment
- `GET /api/patients/departments` - Get list of available departments
- `POST /api/patients/symptoms/analyze` - Analyze symptoms and recommend department
- `GET /api/patients/hospitals/recommend` - Get recommended hospitals based on location, department, and pathfinding algorithm

### 7.3 Doctor Endpoints

- `GET /api/doctors` - Search/filter doctors
- `GET /api/doctors/{id}` - Get doctor details
- `GET /api/doctors/profile` - Get doctor's own profile
- `PUT /api/doctors/profile` - Update doctor profile
- `GET /api/doctors/appointments` - Get doctor's appointments
- `PUT /api/doctors/appointments/{id}/complete` - Mark appointment as completed
- `GET /api/doctors/schedule` - Get doctor's schedule
- `POST /api/doctors/schedule` - Set availability
- `PUT /api/doctors/schedule/{id}` - Update availability

### 7.4 Admin Endpoints

- `GET /api/admin/users` - Get all users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Deactivate user
- `GET /api/admin/appointments` - Get all appointments
- `DELETE /api/admin/appointments/{id}` - Cancel any appointment
- `GET /api/admin/reports` - Get appointment reports (EXTRA)

### 7.5 Hospital Management Endpoints

- `GET /api/hospitals` - Get all hospitals
- `GET /api/hospitals/{id}` - Get hospital details
- `POST /api/admin/hospitals` - Create new hospital (Admin only)
- `PUT /api/admin/hospitals/{id}` - Update hospital information
- `DELETE /api/admin/hospitals/{id}` - Deactivate hospital
- `PUT /api/admin/hospitals/{id}/status` - Update hospital operational status
- `PUT /api/admin/hospitals/{id}/intensity` - Update hospital capacity/intensity
- `GET /api/hospitals/{id}/departments` - Get departments available at hospital

### 7.6 Department Management Endpoints

- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get department details
- `POST /api/admin/departments` - Create new department (Admin only)
- `PUT /api/admin/departments/{id}` - Update department
- `DELETE /api/admin/departments/{id}` - Delete department
- `POST /api/admin/departments/assign-hospital` - Assign department to hospital
- `GET /api/departments/hospitals/{hospitalId}` - Get departments for specific hospital

### 7.7 Pathfinding Endpoints

- `POST /api/pathfinding/recommend` - Get recommended hospitals using pathfinding algorithm
  - Request: patient location, department, optional preferences
  - Response: ranked list of hospitals with scores and reasons
- `GET /api/pathfinding/weights` - Get current pathfinding weight configuration
- `PUT /api/admin/pathfinding/weights` - Update pathfinding weights (Admin only)

---

## 8. Use Cases

### UC-1: Patient Books Appointment with Department Selection

1. Patient logs into the system
2. Patient enters symptoms (e.g., "kidney pain") OR directly selects department (e.g., Urology)
3. System analyzes symptoms and recommends appropriate department (if symptoms provided)
4. Patient confirms or selects department
5. Patient enters/updates their location (address or coordinates)
6. System uses pathfinding algorithm to find optimal hospitals:
   - Calculates distance from patient to all hospitals with requested department
   - Considers hospital intensity/capacity levels
   - Excludes or heavily penalizes hospitals closed due to epidemics (流行病)
   - Computes weighted scores for each hospital
7. System displays ranked list of recommended hospitals with:
   - Hospital name and address
   - Distance from patient location
   - Current intensity level
   - Operational status
   - Available appointment slots
8. Patient selects preferred hospital from recommendations
9. System displays available doctors in selected department at chosen hospital
10. Patient selects a doctor (or system auto-assigns based on availability)
11. System displays available time slots for selected doctor
12. Patient selects date and time
13. Patient enters reason for visit
14. Patient confirms booking
15. System validates availability
16. System creates appointment with pathfinding score recorded
17. System updates doctor's schedule
18. System sends confirmation notification (EXTRA)

### UC-2: Doctor Views Schedule

1. Doctor logs into the system
2. Doctor navigates to appointments section
3. System displays appointments for selected date/week
4. Doctor can filter by status (upcoming, completed, cancelled)

### UC-3: Patient Reschedules Appointment

1. Patient logs into the system
2. Patient views their appointments
3. Patient selects appointment to reschedule
4. Patient clicks reschedule
5. System validates appointment is at least 24 hours away
6. System displays available alternative time slots
7. Patient selects new date/time
8. System updates appointment
9. System sends notification to doctor (EXTRA)

### UC-4: Administrator Manages Users

1. Administrator logs into the system
2. Administrator navigates to user management
3. System displays list of all users
4. Administrator can create new user (doctor/patient)
5. Administrator can edit user details
6. Administrator can deactivate users

### UC-5: Administrator Manages Hospital Closure Due to Epidemic

1. Administrator logs into the system
2. Administrator receives notification of epidemic situation (流行病)
3. Administrator navigates to hospital management
4. Administrator selects hospital to mark as closed
5. Administrator sets operational status to "CLOSED_EPIDEMIC"
6. Administrator enters closure reason: "流行病" or specific epidemic name
7. Administrator sets closure start and end dates (or leaves open-ended)
8. System automatically excludes closed hospital from pathfinding recommendations
9. System notifies patients with upcoming appointments at closed hospital (EXTRA)
10. System suggests alternative hospitals for affected appointments

### UC-6: System Pathfinding Algorithm Execution

1. Patient requests hospital recommendations with department and location
2. System queries all hospitals with requested department
3. System filters out hospitals with status "CLOSED_EPIDEMIC" or "CLOSED_OTHER"
4. For each valid hospital:
   a. Calculate geographic distance from patient location (using Haversine formula)
   b. Normalize distance (0-1 scale)
   c. Get current intensity level (normalized 0-1 scale)
   d. Check operational status weight (0 if operational, 1 if partially closed)
   e. Compute weighted score: (distance_weight × normalized_distance) + (intensity_weight × normalized_intensity) + (status_weight × status_penalty)
5. System sorts hospitals by score (lower is better)
6. System returns top N recommendations (default: top 5) with scores and reasoning

---

## 9. Security Considerations

### 9.1 Authentication

- Passwords must be encrypted using BCrypt or similar
- Session management through Spring Security
- Optional: JWT tokens for stateless authentication (EXTRA)

### 9.2 Authorization

- Role-based access control (RBAC)
- Patients can only access their own appointments
- Doctors can only access their own schedule and appointments
- Administrators have full system access

### 9.3 Data Protection

- Input validation on all endpoints
- SQL injection prevention through JPA
- XSS protection in responses
- Sensitive data encryption in database (EXTRA)

---

## 10. Testing Requirements

### 10.1 Unit Testing

- Service layer business logic testing
- Repository layer data access testing
- Controller endpoint testing with MockMvc

### 10.2 Integration Testing

- API endpoint integration tests
- Database integration tests
- Security integration tests

### 10.3 Test Coverage

- Minimum 70% code coverage for critical components

---

## 11. Deployment Considerations

### 11.1 Environment Setup

- Java 21 runtime environment
- Database server (PostgreSQL with PostGIS extension recommended for geographic queries, or MySQL)
- Application server (embedded Tomcat via Spring Boot)
- Geographic coordinate data for Hong Kong hospitals

### 11.2 Configuration

- Environment-specific properties (dev, prod)
- Database connection configuration
- Email service configuration (for notifications)

### 11.3 Database Migration

- Use Flyway or Liquibase for database versioning (EXTRA)
- Initial schema creation scripts

---

## 12. Project Timeline & Milestones

### Phase 1: Foundation (Week 1-2)

- Project setup and configuration
- Database design and entity creation
- User authentication and authorization
- Basic CRUD operations for users

### Phase 2: Core Features (Week 3-4)

- Appointment booking functionality
- Doctor availability management
- Appointment viewing and management
- Basic search and filtering

### Phase 3: Advanced Features (Week 5-6)

- Notification system (EXTRA)
- Reporting and analytics (EXTRA)
- Advanced appointment features
- Testing and bug fixes

### Phase 4: Polish & Deployment (Week 7-8)

- UI/UX improvements
- Performance optimization
- Documentation completion
- Deployment preparation

---

## 13. Success Criteria

### 13.1 Functional Criteria

- All core requirements (REQ-1.x through REQ-5.x) implemented and working
- At least 3 extra features from Section 4 implemented
- System handles concurrent appointment bookings correctly
- All user roles can perform their designated functions

### 13.2 Quality Criteria

- Code follows best practices and is well-documented
- Test coverage meets minimum requirements
- System is secure and handles errors gracefully
- Application is deployable and runs without critical errors

---

## 14. Assumptions and Constraints

### 14.1 Assumptions

- Users have valid email addresses
- System will be used within Hong Kong geographic boundaries
- Hong Kong geographic coordinate system (WGS84 or HK1980) will be used
- Business hours are standard 9 AM - 5 PM (configurable per hospital)
- Multiple hospitals across Hong Kong will be supported
- Patient locations will be within Hong Kong territory
- Hospital closure due to epidemics (流行病) will be temporary and tracked with dates
- Distance calculations use straight-line (Haversine) distance, not actual travel routes
- Hospital intensity/capacity data will be updated regularly by administrators

### 14.2 Constraints

- Appointments must be booked at least 24 hours in advance
- Cancellations must be made at least 24 hours in advance
- No same-day appointment bookings (can be relaxed later)
- Maximum booking window: 3 months in advance
- Pathfinding algorithm considers only hospitals within Hong Kong territory
- Hospital closure status must be manually updated by administrators (no automatic detection)
- Distance calculations assume straight-line distance (not actual road distance or travel time)
- Pathfinding weights must sum to 100% and be configured by administrators
- Maximum pathfinding recommendations: 10 hospitals per request
- Patient location (latitude/longitude) is optional but required for pathfinding recommendations

---

## 15. Future Enhancements (Out of Scope)

- Mobile application (iOS/Android)
- Video consultation integration
- Payment processing
- Internationalization (i18n) - currently focused on Hong Kong
- Real-time chat support
- Integration with electronic health records (EHR)
- Real-time traffic data integration for actual travel time (currently uses straight-line distance)
- Integration with Hong Kong Hospital Authority APIs for real-time capacity data
- Automatic epidemic detection and hospital closure alerts
- Public transportation route integration (MTR, bus) for patient journey planning

---

## 16. Glossary

- **Appointment**: A scheduled meeting between a patient and doctor
- **Time Slot**: A specific time period available for booking
- **Specialization**: Medical field of expertise (e.g., Cardiology, Pediatrics)
- **Department**: A medical department within a hospital (e.g., Urology, Cardiology, Emergency Medicine)
- **Hospital Intensity**: A measure of hospital utilization/congestion (0.0 = low, 1.0 = high/congested)
- **Pathfinding Algorithm**: Algorithm (Dijkstra's or A\*) used to find optimal hospital routes based on weighted factors
- **Weighted Score**: Combined score calculated from distance, intensity, and operational status for hospital ranking
- **流行病**: Epidemic (Chinese term) - refers to hospital closures due to disease outbreaks
- **Haversine Formula**: Formula used to calculate great-circle distance between two points on a sphere given their latitudes and longitudes
- **RBAC**: Role-Based Access Control
- **JWT**: JSON Web Token (authentication mechanism)
- **HIPAA**: Health Insurance Portability and Accountability Act (data privacy regulations)

---

## Document Version History

| Version | Date           | Author       | Changes                                                                                                                                                                |
| ------- | -------------- | ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.0     | [Current Date] | Project Team | Initial RPD document                                                                                                                                                   |
| 1.1     | [Current Date] | Project Team | Added department-based booking, hospital management, pathfinding algorithm with weighted factors (distance, intensity, epidemic closures), Hong Kong geography support |

---

## End of Document
