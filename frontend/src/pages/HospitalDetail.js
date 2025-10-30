import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getHospital } from "../api";

export default function HospitalDetail() {
  const { id } = useParams();
  const [hospital, setHospital] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getHospital(id)
      .then((h) => {
        if (h === null) setError("Hospital not found");
        else setHospital(h);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div>Loading hospital...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;
  return (
    <div>
      <h2>{hospital.name}</h2>
      <p>ID: {hospital.nodeId}</p>
      <p>Location: ({hospital.x}, {hospital.y})</p>
      <Link to="/hospitals">Back to hospitals</Link>
    </div>
  );
}
