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
    'Hong Kong Island': new Set(['Pok Fu Lam','Chai Wan','Wan Chai','Causeway Bay','Sheung Wan','Central','Tin Hau','Admiralty','North Point']),
    'New Territories': new Set(['Sha Tin','Tai Po','Tuen Mun','Tsuen Wan','Yuen Long','Kwai Chung','Tseung Kwan O','Tin Shui Wai','Ma On Shan','Fanling','Sheung Shui']),
    'Lantau Island': new Set(['Lantau','Tung Chung'])
  };

  // Replace previous inRegion logic with a robust detector that:
  // - prefers an explicit `region` field on the hospital
  // - falls back to inspecting `district` (handles "Tuen Mun, NT" and common abbreviations)
  // - falls back to inspecting hospital.name when district is missing
  // - uses a small canonical list of district -> region matches, and also recognizes "NT"/"New Territories"
  const normalize = (s) => (s || '').toString().trim().toLowerCase();

  const inferRegionFromDistrict = (districtRaw) => {
    const d = normalize(districtRaw);
    if (!d) return '';
    // quick checks for explicit markers
    if (d.includes('new territories') || d === 'nt' || d.includes('n.t') || d.includes('n.t.')) return 'New Territories';
    if (d.includes('hong kong') || d.includes('hk') || d.includes('h.k')) return 'Hong Kong Island';
    if (d.includes('kowloon') || d.includes('kln')) return 'Kowloon';
    if (d.includes('lantau')) return 'Lantau Island';

    // split into tokens by comma/paren/slash/dash/space and check known district names
    const tokens = d.split(/[,:()\/\\\-]+|\s+/).map(t => t.trim()).filter(Boolean);
    const nt = new Set(['tuen','tuen mun','sha','sha tin','tai','tai po','yuen','yuen long','tsuen','tsuen wan','tseung','tseung kwan o','fanling','sheung shui','tung chung','ma on shan','tin shui wai','kwai chung']);
    const hk = new Set(['pok','pok fu lam','chai','chai wan','wan','wan chai','causeway','causeway bay','sheung','sheung wan','central','tin hau','admiralty','north point']);
    const kw = new Set(['kowloon','kowloon city','mong','mong kok','sham','sham shui po','kwun','kwun tong','kowloon east','kowloon west','lai chi kok','yau','yau ma tei','jordan']);
    for (const t of tokens) {
      const tt = t.replace(/\./g, '');
      if (nt.has(tt) || Array.from(nt).some(n => tt === n || tt.includes(n) || n.includes(tt))) return 'New Territories';
      if (hk.has(tt) || Array.from(hk).some(n => tt === n || tt.includes(n) || n.includes(tt))) return 'Hong Kong Island';
      if (kw.has(tt) || Array.from(kw).some(n => tt === n || tt.includes(n) || n.includes(tt))) return 'Kowloon';
    }
    return '';
  };

  const detectRegion = (h) => {
    if (!h) return '';
    // explicit region field wins
    if (h.region && typeof h.region === 'string' && h.region.trim()) {
      const r = normalize(h.region);
      if (r.includes('new territories') || r.includes('nt')) return 'New Territories';
      if (r.includes('hong kong') || r.includes('island') || r.includes('hk')) return 'Hong Kong Island';
      if (r.includes('kowloon') || r.includes('kln')) return 'Kowloon';
      if (r.includes('lantau')) return 'Lantau Island';
    }
    // check district
    const fromDistrict = inferRegionFromDistrict(h.district || '');
    if (fromDistrict) return fromDistrict;
    // fallback: check name for district tokens (e.g. "Tuen Mun Hospital")
    const fromName = inferRegionFromDistrict(h.name || '');
    if (fromName) return fromName;
    // final fallback: check combined district+name as a substring (handles 'Tuen Mun Hospital' or 'Tuen Mun, NT' cases)
    const combined = normalize((h.district || '') + ' ' + (h.name || ''));
    if (combined.includes('tuen mun') || combined.includes('sha tin') || combined.includes('tai po') || combined.includes('yuen long')) return 'New Territories';
    if (combined.includes('pok fu lam') || combined.includes('chai wan') || combined.includes('wan chai') || combined.includes('causeway bay')) return 'Hong Kong Island';
    if (combined.includes('mong kok') || combined.includes('sham shui po') || combined.includes('kowloon') || combined.includes('kwun tong')) return 'Kowloon';
    return '';
  };

  const inRegion = (h) => {
    if (!region) return true;
    if (!h) return false;
    // If hospital object has a 'region' field explicitly matching the selected region
    const detected = detectRegion(h);
    if (detected) return detected === region;

    // As last resort, try matching against the small regionMap membership (case-insensitive containment)
    const s = regionMap[region];
    if (!s) return true;
    const districtRaw = (h.district || '').toString().trim();
    let tokens = districtRaw ? districtRaw.split(/[,()\/\\\-]+|\s+/).map(t => t.trim().toLowerCase()).filter(Boolean) : [];
    if (tokens.length === 0 && h.name) tokens = h.name.toString().split(/[,()\/\\\-]+|\s+/).map(t => t.trim().toLowerCase()).filter(Boolean);
    if (tokens.length === 0) return false;
    for (const sEntry of Array.from(s)) {
      const sLower = sEntry.toLowerCase();
      if (tokens.includes(sLower)) return true;
      for (const t of tokens) {
        if (sLower.includes(t) || t.includes(sLower)) return true;
      }
    }
    // fallback: substring match on combined district+name
    const combined = ((h.district || '') + ' ' + (h.name || '')).toLowerCase();
    for (const sEntry of Array.from(s)) {
      if (combined.includes(sEntry.toLowerCase())) return true;
    }
    return false;
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
