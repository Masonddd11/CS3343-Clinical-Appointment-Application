-- data.sql disabled to avoid conflicts with JPA schema creation. Seed data is now handled programmatically in DataInitializer.
-- If you need SQL-based sample data, ensure table names and columns match the generated schema (plural table names like 'hospitals').

-- Sample Hospitals
-- INSERT INTO hospital (id, name, address, district, operational_status) VALUES
--   (1, 'Central Hospital', '123 Queen St', 'Central', 'OPERATIONAL'),
--   (2, 'Eastside Medical', '456 East Ave', 'Eastern', 'OPERATIONAL');

-- Sample Departments
-- INSERT INTO department (id, name, code) VALUES
--   (1, 'Cardiology', 'CARD'),
--   (2, 'Urology', 'URO');

-- Sample Doctors
-- INSERT INTO doctor (id, first_name, last_name, specialization, qualifications, hospital_id, department_id, is_available) VALUES
--   (1, 'Alice', 'Wong', 'Urology', 'MBBS, FRCS', 1, 2, true),
--   (2, 'Ben', 'Lee', 'Cardiology', 'MBBS, MRCP', 1, 1, true);

-- Sample Patients
-- INSERT INTO patient (id, first_name, last_name, email) VALUES
--   (1, 'Bob', 'Chan', 'patient@test.com');

-- Sample Appointments
-- INSERT INTO appointment (id, patient_id, doctor_id, hospital_id, department_id, appointment_date, appointment_time, reason_for_visit, symptoms, status) VALUES
--   (1, 1, 1, 1, 2, '2025-12-01', '09:00:00', 'Checkup', 'Frequent urination', 'CONFIRMED'),
--   (2, 1, 2, 1, 1, '2025-12-02', '10:30:00', 'Heart pain', 'Chest pain, shortness of breath', 'PENDING');
