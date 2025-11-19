# How to Access H2 Database Console

## Step 1: Start the Application

The application is starting in the background. Wait a few seconds for it to fully start up.

## Step 2: Access H2 Console

1. **Open your web browser**
2. **Navigate to:** http://localhost:8080/h2-console
3. **You should see the H2 Console login page**

## Step 3: Connect to Database

Fill in the connection details:

- **JDBC URL:** `jdbc:h2:mem:hospitaldb`
- **User Name:** `sa`
- **Password:** (leave empty - no password)

Then click **Connect**

## Step 4: View Tables

Once connected, you can:

1. **See all tables:** Type `SHOW TABLES;` and click **Run**
   - This will show all created tables (users, patients, doctors, hospitals, departments, appointments, etc.)

2. **Query any table:** For example:
   ```sql
   SELECT * FROM users;
   SELECT * FROM hospitals;
   SELECT * FROM departments;
   SELECT * FROM appointments;
   ```

3. **View table structure:** Type `DESC table_name;` (e.g., `DESC users;`)

## Quick Queries to Try

```sql
-- View all users
SELECT * FROM users;

-- View all hospitals
SELECT * FROM hospitals;

-- View all departments
SELECT * FROM departments;

-- View hospital-department mappings
SELECT h.name as hospital, d.name as department 
FROM hospital_departments hd
JOIN hospitals h ON hd.hospital_id = h.id
JOIN departments d ON hd.department_id = d.id;

-- View all appointments
SELECT * FROM appointments;

-- Check table count
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM hospitals;
```

## Notes

- The database is **in-memory** - data will be lost when the application stops
- Tables are automatically created when the application starts (via `spring.jpa.hibernate.ddl-auto=update`)
- You can run any SQL queries in the H2 console
- The console is only accessible locally (for security)

## Troubleshooting

If you can't connect:
1. Make sure the application is running (check the console output)
2. Wait a few seconds after startup for the database to initialize
3. Verify the JDBC URL matches exactly: `jdbc:h2:mem:hospitaldb`
4. Check that the server is running on port 8080

