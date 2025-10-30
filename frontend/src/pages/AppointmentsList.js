import React, { useEffect, useState } from "react";
import { listAppointments, deleteAppointment } from "../api";
import { getHospitals } from "../api";

export default function AppointmentsList() {
  const [appointments, setAppointments] = useState([]);
  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState("");
  const [region, setRegion] = useState("");
  const [hospitalId, setHospitalId] = useState("");

  const load = () => {
    setLoading(true);
    setError(null);
    Promise.all([listAppointments(), getHospitals()])
      .then(([apps, hs]) => {
        setAppointments(apps);
        setHospitals(hs || []);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const onDelete = async (id) => {
    if (!window.confirm("Delete this appointment?")) return;
    try {
      await deleteAppointment(id);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const regionMap = {
    'Hong Kong Island': new Set(['Pok Fu Lam','Chai Wan','Wan Chai','Causeway Bay','Sheung Wan','Central','Wan Chai','Causeway Bay','Sheung Wan','Sheung Wan','Central','Tin Hau']),
    'New Territories': new Set(['Sha Tin','Tai Po','Tuen Mun','Tsuen Wan','Yuen Long','Kwai Chung','Sham Shui Po','Mong Kok','Kowloon','Kowloon City','Kowloon East']),
    'Lantau Island': new Set(['Lantau'])
  };

  const inRegion = (h) => {
    if (!region) return true;
    const s = regionMap[region];
    if (!s) return true;
    return h && s.has(h.district);
  };

  const hospitalsForRegion = hospitals.filter(h => inRegion(h));

  const visible = appointments.filter(a => {
    if (filter && !(a.patient && a.patient.name && a.patient.name.toLowerCase().includes(filter.toLowerCase()))) return false;
    if (region && !inRegion(a.hospital)) return false;
    if (hospitalId && (!a.hospital || String(a.hospital.nodeId) !== String(hospitalId))) return false;
    return true;
  });

  const fmtDate = (s) => {
    if (!s) return "";
    const d = new Date(s);
    return d.toLocaleString();
  };

  return (
    <div className="container">
      <div className="card">
        <h2 style={{ marginTop: 0 }}>Appointments</h2>

        <div style={{ display: 'flex', gap: 8, marginBottom: 12, alignItems: 'center' }}>
          <input placeholder="Filter by patient name" value={filter} onChange={e => setFilter(e.target.value)} />

          <label style={{ marginLeft: 8 }}>Region:</label>
          <select value={region} onChange={e => { setRegion(e.target.value); setHospitalId(""); }}>
            <option value="">All</option>
            <option value="Hong Kong Island">Hong Kong Island</option>
            <option value="New Territories">New Territories</option>
            <option value="Lantau Island">Lantau Island</option>
          </select>

          <label style={{ marginLeft: 8 }}>Hospital:</label>
          <select value={hospitalId} onChange={e => setHospitalId(e.target.value)}>
            <option value="">All hospitals</option>
            {hospitalsForRegion.map(h => (
              <option key={h.nodeId} value={h.nodeId}>{h.name} ({h.district})</option>
            ))}
          </select>

          <button onClick={() => load()}>Refresh</button>
        </div>

        {loading && <div>Loading appointments...</div>}
        {error && <div style={{ color: 'red' }}>{error}</div>}

        {!loading && !error && (
          visible.length ? (
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Patient ID</th>
                  <th>Name</th>
                  <th>Sex</th>
                  <th>Age</th>
                  <th>Hospital</th>
                  <th>Created</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {visible.map(a => (
                  <tr key={a.id}>
                    <td>{a.id}</td>
                    <td>{a.patient?.id}</td>
                    <td>{a.patient?.name}</td>
                    <td>{a.patient?.sex}</td>
                    <td>{a.patient?.age}</td>
                    <td>{a.hospital?.name}</td>
                    <td>{fmtDate(a.createdAt)}</td>
                    <td>{a.status}</td>
                    <td>
                      <button onClick={() => onDelete(a.id)} style={{ background: '#e74c3c', color: 'white', border: 'none' }}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div>No appointments found.</div>
          )
        )}
      </div>
    </div>
  );
}
