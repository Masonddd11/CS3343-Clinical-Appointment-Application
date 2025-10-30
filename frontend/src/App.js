import React from "react";
import { Routes, Route, Link } from "react-router-dom";
import HospitalsList from "./pages/HospitalsList";
import HospitalDetail from "./pages/HospitalDetail";
import CreateAppointment from "./pages/CreateAppointment";
import AppointmentsList from "./pages/AppointmentsList";
import AdminDashboard from "./pages/AdminDashboard";

export default function App() {
  return (
    <div style={{ fontFamily: "Arial, sans-serif", padding: 20 }}>
      <header style={{ marginBottom: 20 }}>
        <h1>Clinical Appointment</h1>
        <nav>
          <Link to="/hospitals" style={{ marginRight: 10 }}>Hospitals</Link>
          <Link to="/appointments/new" style={{ marginRight: 10 }}>Create Appointment</Link>
          <Link to="/appointments" style={{ marginRight: 10 }}>Appointments</Link>
          <Link to="/admin">Admin</Link>
        </nav>
      </header>

      <main>
        <Routes>
          <Route path="/" element={<HospitalsList />} />
          <Route path="/hospitals" element={<HospitalsList />} />
          <Route path="/hospitals/:id" element={<HospitalDetail />} />
          <Route path="/appointments/new" element={<CreateAppointment />} />
          <Route path="/appointments" element={<AppointmentsList />} />
          <Route path="/admin" element={<AdminDashboard />} />
        </Routes>
      </main>
    </div>
  );
}
