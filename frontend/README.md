This folder contains a small React frontend for the Clinical Appointment backend.

Quick start (from this folder):

1. Install dependencies:

```bash
npm install
```

2. Start dev server (the frontend talks to the backend API):

- By default the frontend will call the backend at http://localhost:8082/api (the project uses port 8082 in `application.properties`).
- You can override this with the REACT_APP_API_BASE environment variable.

On Windows cmd.exe (example if your backend runs on port 8082):

```cmd
set REACT_APP_API_BASE=http://localhost:8082/api
npm start
```

On PowerShell:

```powershell
$env:REACT_APP_API_BASE = "http://localhost:8082/api"
npm start
```

If you prefer the usual default 3000 dev server without setting env, you can also edit `frontend/src/api.js` to point the `BASE` constant to your backend URL (not recommended for production).

Build for production:

```bash
npm run build
```

Backend (how to run the Spring Boot app)

- From the project root (one level up from this folder) you can run the backend with Maven:

```bash
mvn -DskipTests spring-boot:run
```

- Or run the packaged jar (after `mvn package`):

```bash
java -jar target/clinical-appointment-app-0.0.1-SNAPSHOT.jar
```

Notes about ports and the API base

- The project uses `src/main/resources/application.properties` to set the Spring Boot port. By default this project is configured to run on port 8082, so the frontend defaults to `http://localhost:8082/api`.
- If you change the backend port, set the environment variable before starting the frontend:

```cmd
set REACT_APP_API_BASE=http://localhost:8081/api
npm start
```

A&E realtime waiting times (new feature)

- The backend now exposes two endpoints that proxy and cache the Hospital Authority A&E feed (updates ~every 15 minutes):
  - GET /api/ae-wait-times — returns the raw HA JSON feed
  - GET /api/ae-wait-times/map — returns a JSON object keyed by hospital name for easy lookup

- The frontend fetches `/api/ae-wait-times/map` and shows the Triage waiting times inside the Hospitals list and Hospital detail pages where a matching hospital name is found.
- The backend caches the remote feed for ~10 minutes and will fall back to a bundled `data/a&e_waiting_time.json` file if the remote feed cannot be reached.

Quick checks / smoke tests

- Confirm the backend is running and the A&E endpoints return data (Windows cmd.exe example):

```cmd
curl http://localhost:8082/api/ae-wait-times
curl http://localhost:8082/api/ae-wait-times/map
```

- If you see JSON returned, the frontend should be able to display the A&E wait times for hospitals that match the HA feed hospital names.

Troubleshooting "Failed to fetch" / No A&E values shown

- Make sure the backend is running (see `mvn spring-boot:run` output) and that it is listening on the port you expect (8082 by default).
- Open browser DevTools (F12) -> Network tab and reload the page. Inspect requests to `/api/hospitals` and `/api/ae-wait-times/map` for status and response. Common problems:
  - Network error / connection refused — backend not running or wrong port
  - CORS/preflight issues — if serving frontend from a different origin, you may need to enable CORS on the backend
  - 404 from `/api/ae-wait-times/map` — backend not compiled/started with the new controller (restart it)
- If the HA feed is temporarily down, the backend will return cached or bundled data; check backend logs for fetch failures.

If hospital waiting times are still missing in the UI

- Hospital names in your local DB must match the HA feed names. If a hospital's name differs (e.g. abbreviations or alternate naming), the frontend matching may not find it. If you see mismatches I can add a small normalization table to map DB names to HA feed names.

Where to look for logs

- Backend: console output from `mvn spring-boot:run` or the packaged jar
- Frontend: browser console (F12) and the terminal where you ran `npm start`


