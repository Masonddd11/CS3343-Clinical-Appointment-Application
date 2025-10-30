// Default to port 8081 per user's environment; allow override with REACT_APP_API_BASE
const BASE = process.env.REACT_APP_API_BASE || "http://localhost:8081/api";

async function extractError(res) {
  let text = "";
  try {
    // try JSON first
    const j = await res.json();
    text = j.message || JSON.stringify(j);
  } catch (e) {
    try {
      text = await res.text();
    } catch (e2) {
      text = "(no response body)";
    }
  }
  return `${res.status} ${res.statusText}: ${text}`;
}

async function fetchWithNetworkHint(url, opts) {
  try {
    const res = await fetch(url, opts);
    return res;
  } catch (e) {
    // network-level failure (DNS, connection refused, CORS preflight that failed, etc.)
    throw new Error(
      `Network error when fetching ${url}. Make sure the backend server is running on ${
        process.env.REACT_APP_API_BASE || "http://localhost:8081/api"
      } and accepts requests from this origin. Original error: ${e.message}`
    );
  }
}

export async function getHospitals() {
  const res = await fetchWithNetworkHint(`${BASE}/hospitals`);
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

export async function getHospital(id) {
  const res = await fetchWithNetworkHint(`${BASE}/hospitals/${id}`);
  if (res.status === 404) return null;
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

export async function createAppointment(patient) {
  const res = await fetchWithNetworkHint(`${BASE}/appointments`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(patient),
  });
  if (res.status === 503) throw new Error("No hospital available");
  if (!res.ok) {
    if (res.status === 400) throw new Error("Invalid patient data");
    throw new Error(await extractError(res));
  }
  return res.json();
}

// New admin and appointment endpoints
export async function listAppointments() {
  const res = await fetchWithNetworkHint(`${BASE}/appointments`);
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

export async function deleteAppointment(id) {
  const res = await fetchWithNetworkHint(`${BASE}/appointments/${id}`, { method: "DELETE" });
  if (res.status === 404) throw new Error("Appointment not found");
  if (!res.ok) throw new Error(await extractError(res));
  return true;
}

export async function listPatients() {
  const res = await fetchWithNetworkHint(`${BASE}/patients`);
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

export async function deletePatientAppointments(name) {
  const res = await fetchWithNetworkHint(`${BASE}/patients/${encodeURIComponent(name)}`, { method: "DELETE" });
  if (res.status === 404) throw new Error("No appointments for that patient");
  if (!res.ok) throw new Error(await extractError(res));
  return true;
}

export async function addHospital(hospital) {
  const res = await fetchWithNetworkHint(`${BASE}/hospitals`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(hospital),
  });
  if (!res.ok) throw new Error(await extractError(res));
  return res.json();
}

export async function deleteHospital(id) {
  const res = await fetchWithNetworkHint(`${BASE}/hospitals/${id}`, { method: "DELETE" });
  if (res.status === 404) throw new Error("Hospital not found");
  if (!res.ok) throw new Error(await extractError(res));
  return true;
}

// Geocoding via Nominatim (OpenStreetMap) - no API key required but rate-limited
export async function geocodeAddress(address) {
  const q = encodeURIComponent(address + ", Hong Kong");
  const url = `https://nominatim.openstreetmap.org/search?q=${q}&format=json&limit=1&addressdetails=1`;
  const res = await fetch(url, { headers: { 'User-Agent': 'ClinicalAppointmentApp/1.0 (your-email@example.com)' } });
  if (!res.ok) throw new Error(`Geocoding failed: ${res.status} ${res.statusText}`);
  const j = await res.json();
  if (!j || !j.length) throw new Error('Address not found');
  return { lat: parseFloat(j[0].lat), lon: parseFloat(j[0].lon), display_name: j[0].display_name };
}

export async function reverseGeocode(lat, lon) {
  const url = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json&addressdetails=1`;
  const res = await fetch(url, { headers: { 'User-Agent': 'ClinicalAppointmentApp/1.0 (your-email@example.com)' } });
  if (!res.ok) throw new Error(`Reverse geocoding failed: ${res.status} ${res.statusText}`);
  const j = await res.json();
  return j;
}
