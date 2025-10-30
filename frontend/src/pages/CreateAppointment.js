import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createAppointment, geocodeAddress } from "../api";

export default function CreateAppointment() {
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [hkid, setHkid] = useState("");
  const [age, setAge] = useState("");
  const [sex, setSex] = useState("");
  const [dob, setDob] = useState("");
  const [illnessRecord, setIllnessRecord] = useState("");
  const [healthRecord, setHealthRecord] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [loadingGeo, setLoadingGeo] = useState(false);
  const [creating, setCreating] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);

    if (!name.trim()) { setError("Name is required"); return; }
    if (!address.trim()) { setError("Address or district is required"); return; }

    setLoadingGeo(true);
    try {
      const geo = await geocodeAddress(address);
      const patient = {
        name: name.trim(),
        x: geo.lat,
        y: geo.lon,
        hkid: hkid.trim(),
        age: age ? Number(age) : null,
        sex: sex,
        dob: dob,
        illnessRecord: illnessRecord.trim(),
        healthRecord: healthRecord.trim(),
        email: email.trim(),
        phone: phone.trim(),
      };
      setLoadingGeo(false);
      setCreating(true);
      const appt = await createAppointment(patient);
      setResult({ appt, geo });
      setCreating(false);
    } catch (err) {
      setLoadingGeo(false);
      setCreating(false);
      setError(err.message);
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h2>Create Appointment</h2>
        <form onSubmit={onSubmit} style={{ maxWidth: 720 }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div>
              <label>Name<br />
                <input value={name} onChange={(e) => setName(e.target.value)} required />
              </label>
            </div>
            <div>
              <label>HKID<br />
                <input value={hkid} onChange={(e) => setHkid(e.target.value)} placeholder="A123456(7)" />
              </label>
            </div>

            <div>
              <label>Age<br />
                <input type="number" min="0" value={age} onChange={(e) => setAge(e.target.value)} />
              </label>
            </div>
            <div>
              <label>Sex<br />
                <select value={sex} onChange={(e) => setSex(e.target.value)}>
                  <option value="">Select</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </label>
            </div>

            <div>
              <label>Date of Birth<br />
                <input type="date" value={dob} onChange={(e) => setDob(e.target.value)} />
              </label>
            </div>
            <div>
              <label>Phone<br />
                <input value={phone} onChange={(e) => setPhone(e.target.value)} />
              </label>
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label>Address or district<br />
                <input value={address} onChange={(e) => setAddress(e.target.value)} placeholder="e.g. Central, Pok Fu Lam, Sha Tin" required />
              </label>
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label>Illness record<br />
                <textarea value={illnessRecord} onChange={(e) => setIllnessRecord(e.target.value)} rows={3} />
              </label>
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label>Health record<br />
                <textarea value={healthRecord} onChange={(e) => setHealthRecord(e.target.value)} rows={3} />
              </label>
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label>Email address<br />
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
              </label>
            </div>

          </div>

          <div style={{ marginTop: 12 }}>
            <button type="submit" disabled={loadingGeo || creating}>{loadingGeo ? 'Locating...' : creating ? 'Creating...' : 'Find Nearest Hospital & Book'}</button>
            <button type="button" onClick={() => navigate("/hospitals")} style={{ marginLeft: 8 }}>Cancel</button>
          </div>
        </form>

        {error && <div style={{ color: "red", marginTop: 12 }}>{error}</div>}

        {result && (
          <div style={{ marginTop: 12 }}>
            <h3>Appointment Created</h3>
            <p>Patient ID: {result.appt.patient?.id}</p>
            <p>Patient: {result.appt.patient?.name}</p>
            <p>Sex: {result.appt.patient?.sex} — Age: {result.appt.patient?.age}</p>
            <p>Assigned Hospital: {result.appt.hospital?.name} (ID: {result.appt.hospital?.nodeId || result.appt.hospital?.id})</p>
            <p>Booking created: {result.appt.createdAt}</p>
            <p>Status: {result.appt.status}</p>
            <p>Resolved address: {result.geo.display_name} (lat: {result.geo.lat}, lon: {result.geo.lon})</p>
          </div>
        )}
      </div>
    </div>
  );
}
