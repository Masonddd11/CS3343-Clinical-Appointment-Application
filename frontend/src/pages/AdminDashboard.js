import React, { useEffect, useState } from "react";
import { getHospitals, addHospital, deleteHospital, listPatients, deletePatientAppointments } from "../api";

export default function AdminDashboard() {
  const [hospitals, setHospitals] = useState([]);
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const hs = await getHospitals();
      setHospitals(hs);
      const ps = await listPatients();
      setPatients(ps);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const onAddHospital = async () => {
    const name = window.prompt("Hospital name:");
    if (!name) return;
    const x = Number(window.prompt("X coordinate (number):", "0"));
    const y = Number(window.prompt("Y coordinate (number):", "0"));
    try {
      await addHospital({ name, x, y });
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  const onDeleteHospital = async (id) => {
    if (!window.confirm("Delete this hospital?")) return;
    try {
      await deleteHospital(id);
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  const onDeletePatient = async (name) => {
    if (!window.confirm(`Delete appointments for ${name}?`)) return;
    try {
      await deletePatientAppointments(name);
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  if (loading) return <div>Loading admin data...</div>;
  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  return (
    <div className="container">
      <div className="card">
        <h2 style={{ marginTop: 0 }}>Admin Dashboard</h2>

        <section style={{ marginBottom: 20 }}>
          <h3>Hospitals <button onClick={onAddHospital} style={{ marginLeft: 12 }}>Add Hospital</button></h3>
          <ul>
            {hospitals.map(h => (
              <li key={h.nodeId} style={{ marginBottom: 6 }}>
                {h.name} (id: {h.nodeId}) — location: ({h.x}, {h.y})
                <button onClick={() => onDeleteHospital(h.nodeId)} style={{ marginLeft: 8, background: '#e74c3c', color: 'white', border: 'none', padding: '4px 8px' }}>Delete</button>
              </li>
            ))}
          </ul>
        </section>

        <section>
          <h3>Patients</h3>
          <ul>
            {patients.map((p, idx) => (
              <li key={idx} style={{ marginBottom: 6 }}>
                {p.name} — location ({p.x}, {p.y})
                <button onClick={() => onDeletePatient(p.name)} style={{ marginLeft: 8, background: '#e74c3c', color: 'white', border: 'none', padding: '4px 8px' }}>Remove</button>
              </li>
            ))}
            {!patients.length && <li>No patients found.</li>}
          </ul>
        </section>
      </div>
    </div>
  );
}
