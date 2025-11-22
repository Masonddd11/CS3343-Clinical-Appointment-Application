# Phase 1 Integration Guide: Smart Booking Flow

## Overview

Phase 1 integrates **symptom analysis** and **pathfinding** into the appointment booking flow. Instead of manually selecting department, hospital, and doctor, patients can now:

1. **Enter symptoms** → System suggests department
2. **Get hospital recommendations** → Based on location, intensity, and operational status
3. **Select doctor** → From recommended hospitals
4. **Book appointment** → With pathfinding score stored

## Architecture

### New Components

1. **`SmartAppointmentRequest`** - Enhanced booking request with symptoms
2. **`SmartAppointmentResponse`** - Response with recommendations + appointment
3. **`BookingRecommendationService`** - Orchestrates symptom analysis + pathfinding
4. **`/api/appointments/smart`** - New smart booking endpoint

### Flow Diagram

```
Patient enters symptoms
    ↓
SymptomService analyzes → Department recommendation
    ↓
If department found → PathfindingService recommends hospitals
    ↓
Patient selects hospital → System shows available doctors
    ↓
Patient selects doctor → Appointment booked with pathfinding score
```

## API Endpoints

### 1. Smart Booking Endpoint

**POST** `/api/appointments/smart`

**Authentication:** Required (PATIENT role)

**Request Body:**
```json
{
  "symptoms": "kidney pain in my flank",
  "departmentId": null,           // Optional - auto-filled from symptoms
  "hospitalId": null,             // Optional - will get recommendations if not provided
  "doctorId": null,               // Optional - required only when hospitalId is set
  "appointmentDate": "2024-12-28",
  "appointmentTime": "14:00",
  "reasonForVisit": "Kidney pain consultation",
  "maxHospitalRecommendations": 5  // Optional - default 5
}
```

**Response Scenarios:**

#### Scenario 1: Only Symptoms Provided
```json
{
  "appointment": null,
  "departmentRecommendation": {
    "departmentId": 1,
    "departmentName": "Urology",
    "departmentCode": "URO",
    "confidenceScore": 0.85,
    "matchedKeywords": ["kidney pain", "flank pain"],
    "message": "Recommended department: Urology (confidence: 85%)"
  },
  "hospitalRecommendations": [
    {
      "hospitalId": 1,
      "hospitalName": "Queen Elizabeth Hospital",
      "distance": 2.5,
      "score": 0.75,
      "operationalStatus": "OPERATIONAL"
    }
  ],
  "message": "Department auto-selected based on symptoms: Urology. Hospital recommendations provided. Please select a hospital and doctor to complete booking."
}
```

#### Scenario 2: Symptoms + Department Provided
```json
{
  "appointment": null,
  "departmentRecommendation": {
    "departmentId": 1,
    "departmentName": "Urology",
    "confidenceScore": 0.85
  },
  "hospitalRecommendations": [
    // ... hospital recommendations
  ],
  "message": "Hospital recommendations provided. Please select a hospital and doctor to complete booking."
}
```

#### Scenario 3: Symptoms + Department + Hospital Provided
```json
{
  "appointment": null,
  "departmentRecommendation": { ... },
  "hospitalRecommendations": [],
  "message": "Please select a doctor to complete booking. Available doctors: 2"
}
```

#### Scenario 4: All Fields Provided (Complete Booking)
```json
{
  "appointment": {
    "id": 1,
    "patientId": 1,
    "doctorId": 1,
    "hospitalId": 1,
    "departmentId": 1,
    "appointmentDate": "2024-12-28",
    "appointmentTime": "14:00",
    "pathfindingScore": 0.75,
    "status": "CONFIRMED"
  },
  "departmentRecommendation": { ... },
  "hospitalRecommendations": [],
  "message": "Appointment booked successfully!"
}
```

## How It Works

### Step 1: Symptom Analysis
```java
SymptomAnalysisResponse deptRec = bookingRecommendationService
    .analyzeSymptomsForDepartment("kidney pain");
```
- Normalizes and tokenizes input
- Matches against symptom keywords
- Returns department with confidence score

### Step 2: Hospital Pathfinding
```java
List<HospitalRecommendationResponse> hospitals = bookingRecommendationService
    .recommendHospitalsForPatient(patientId, departmentId, maxResults);
```
- Gets patient's location (latitude/longitude)
- Finds hospitals with the department
- Calculates weighted scores (distance + intensity + operational status)
- Returns sorted recommendations

### Step 3: Doctor Selection
```java
List<Doctor> doctors = doctorRepository
    .findByHospitalIdAndDepartmentId(hospitalId, departmentId);
```
- Finds available doctors at selected hospital/department
- Returns list for patient to choose

### Step 4: Booking with Pathfinding Score
```java
appointmentService.bookAppointmentWithPathfindingScore(
    patientId, request, pathfindingScore
);
```
- Books appointment normally
- Stores pathfinding score for analytics

## Testing Guide

### Prerequisites
1. Start the application: `mvn spring-boot:run`
2. Register/Login as patient to get JWT token

### Test Case 1: Full Smart Booking Flow

**Step 1: Login as Patient**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "patient@test.com",
  "password": "patient123"
}
```

Save the `token` from response.

**Step 2: Smart Booking - Symptoms Only**
```bash
POST http://localhost:8080/api/appointments/smart
Authorization: Bearer <YOUR_TOKEN>
Content-Type: application/json

{
  "symptoms": "kidney pain in my flank area",
  "appointmentDate": "2024-12-28",
  "appointmentTime": "14:00",
  "reasonForVisit": "Kidney pain consultation"
}
```

**Expected Response:**
- `departmentRecommendation` with Urology department
- `hospitalRecommendations` with sorted hospitals
- `appointment` is null (not yet booked)
- `message` explains next steps

**Step 3: Select Hospital and Doctor**
```bash
POST http://localhost:8080/api/appointments/smart
Authorization: Bearer <YOUR_TOKEN>
Content-Type: application/json

{
  "symptoms": "kidney pain",
  "departmentId": 1,
  "hospitalId": 1,
  "doctorId": 1,
  "appointmentDate": "2024-12-28",
  "appointmentTime": "14:00",
  "reasonForVisit": "Kidney pain consultation"
}
```

**Expected Response:**
- `appointment` with full booking details
- `pathfindingScore` stored in appointment
- `message`: "Appointment booked successfully!"

### Test Case 2: Progressive Booking

**Step 1: Get Department Recommendation**
```bash
POST http://localhost:8080/api/appointments/smart
{
  "symptoms": "chest pain and tightness",
  "appointmentDate": "2024-12-29",
  "appointmentTime": "10:00",
  "reasonForVisit": "Heart checkup"
}
```

**Step 2: Review Hospital Recommendations**
- Check `hospitalRecommendations` array
- Each hospital has: distance, intensity, operational status, score

**Step 3: Get Available Doctors**
```bash
GET http://localhost:8080/api/doctors/hospital/1/department/2
Authorization: Bearer <YOUR_TOKEN>
```

**Step 4: Complete Booking**
```bash
POST http://localhost:8080/api/appointments/smart
{
  "symptoms": "chest pain",
  "departmentId": 2,
  "hospitalId": 1,
  "doctorId": 2,
  "appointmentDate": "2024-12-29",
  "appointmentTime": "10:00",
  "reasonForVisit": "Heart checkup"
}
```

### Test Case 3: Verify Pathfinding Score

After booking, check the appointment:

```bash
GET http://localhost:8080/api/appointments/1
Authorization: Bearer <YOUR_TOKEN>
```

The response should include `pathfindingScore` field.

## Integration Points

### 1. Symptom Analysis Integration
- **Service:** `SymptomService.analyzeSymptom()`
- **Used in:** `BookingRecommendationService.analyzeSymptomsForDepartment()`
- **Output:** Department recommendation with confidence

### 2. Pathfinding Integration
- **Service:** `HospitalPathfinderService.recommendHospitals()`
- **Used in:** `BookingRecommendationService.recommendHospitalsForPatient()`
- **Output:** Sorted hospital recommendations with scores

### 3. Appointment Booking Integration
- **Service:** `AppointmentService.bookAppointmentWithPathfindingScore()`
- **Used in:** Smart booking endpoint
- **Enhancement:** Stores pathfinding score for analytics

## Benefits

1. **User Experience:** Patients don't need to know which department to choose
2. **Intelligent Routing:** System suggests best hospitals based on multiple factors
3. **Data-Driven:** Pathfinding scores stored for future analysis
4. **Flexible:** Can still use manual booking endpoint if needed
5. **Progressive:** Supports step-by-step booking flow

## Error Handling

### No Department Found
```json
{
  "message": "No matching department found for the given symptoms. Please select a department manually."
}
```

### No Hospitals Found
```json
{
  "error": "No hospitals found for the selected department"
}
```

### Patient Location Not Set
```json
{
  "error": "Patient location not set. Cannot provide hospital recommendations."
}
```

### No Doctors Available
```json
{
  "error": "No doctors available at the selected hospital and department"
}
```

## Next Steps (Future Enhancements)

1. **Auto-select best hospital** if only one recommendation
2. **Auto-select doctor** if only one available
3. **Time slot suggestions** based on doctor availability
4. **Confirmation step** before final booking
5. **Booking history** with pathfinding scores for analytics


