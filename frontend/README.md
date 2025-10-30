This folder contains a small React frontend for the Clinical Appointment backend.

Quick start (from this folder):

1. Install dependencies:

```bash
npm install
```

2. Start dev server:

```bash
npm start
```

The frontend defaults to the backend API at http://localhost:8081/api. You can override this with the REACT_APP_API_BASE environment variable.

On Windows cmd.exe, set and start like this (example if your backend runs on port 8081):

```cmd
set REACT_APP_API_BASE=http://localhost:8081/api
npm start
```

If your backend is running on the default Spring Boot port 8080, use:

```cmd
set REACT_APP_API_BASE=http://localhost:8080/api
npm start
```

Troubleshooting "Failed to fetch":
- Make sure the Spring Boot backend is running (run `mvn spring-boot:run` in the project root or start from your IDE).
- Open browser devtools (F12) -> Network tab -> reload; inspect the request to `/api/hospitals` for status, response body, and CORS errors.
- If you see a network error, confirm the backend URL and port and set REACT_APP_API_BASE accordingly before starting the frontend.
