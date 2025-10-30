import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getHospitals } from "../api";
import { MapContainer, TileLayer, Marker, Popup, Polyline, CircleMarker } from 'react-leaflet';
import L from 'leaflet';

// fix default icon issue in some bundlers
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

// create a red pin SVG icon as a data URL for the user's location
const redPinSvg = `data:image/svg+xml;utf8,${encodeURIComponent(`
<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 36' width='24' height='36'>
  <path fill='#e74c3c' stroke='#b92b2b' stroke-width='1' d='M12 0C7.03 0 3 4.03 3 9c0 7.5 9 20 9 20s9-12.5 9-20c0-4.97-4.03-9-9-9z'/>
  <circle cx='12' cy='9' r='3.5' fill='white'/>
</svg>
`)}`;

const redPinIcon = L.icon({
  iconUrl: redPinSvg,
  iconSize: [24, 36],
  iconAnchor: [12, 36],
  popupAnchor: [0, -36],
});

function haversine(lat1, lon1, lat2, lon2) {
  const toRad = (v) => (v * Math.PI) / 180;
  const R = 6371; // km
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c; // distance in km
}

export default function HospitalsList() {
  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [district, setDistrict] = useState("");
  const [lat, setLat] = useState("");
  const [lon, setLon] = useState("");
  const [nearest, setNearest] = useState(null);
  const [transport, setTransport] = useState("driving");
  const [region, setRegion] = useState("");

  const regionMap = {
    'Hong Kong Island': new Set(['Pok Fu Lam','Chai Wan','Wan Chai','Causeway Bay','Sheung Wan','Central','Wan Chai']),
    'New Territories': new Set(['Sha Tin','Tai Po','Tuen Mun','Tsuen Wan','Yuen Long','Kwai Chung','Sham Shui Po','Mong Kok','Kowloon','Kowloon City','Kowloon East']),
    'Lantau Island': new Set(['Lantau'])
  };

  const inRegion = (h) => {
    if (!region) return true;
    const s = regionMap[region];
    if (!s) return true;
    return s.has(h.district);
  };

  useEffect(() => {
    getHospitals()
      .then((data) => setHospitals(data))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    // when district changes, clear nearest
    setNearest(null);
  }, [district]);

  if (loading) return <div>Loading hospitals...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (!hospitals.length) return <div>No hospitals found.</div>;

  const districts = Array.from(new Set(hospitals.map(h => h.district).filter(Boolean))).sort();

  const list = hospitals.filter(h => (district ? h.district === district : true) && inRegion(h));

  const onFindNearest = () => {
    const lx = parseFloat(lat);
    const ly = parseFloat(lon);
    if (Number.isNaN(lx) || Number.isNaN(ly)) {
      setError("Please enter valid latitude and longitude numbers.");
      return;
    }
    let best = null;
    let bestD = Infinity;
    for (const h of hospitals) {
      const d = haversine(lx, ly, h.x, h.y);
      if (d < bestD) { bestD = d; best = h; }
    }
    if (best) {
      setNearest({ hospital: best, distanceKm: bestD });
    }
  };

  const estimateTime = (distanceKm, mode) => {
    // simple speed assumptions
    const speeds = { walking: 5, driving: 40, transit: 25 }; // km/h
    const speed = speeds[mode] || 40;
    const hours = distanceKm / speed;
    const mins = Math.round(hours * 60);
    return mins;
  };

  const googleMapsLink = (fromLat, fromLon, toLat, toLon, mode) => {
    // mode=driving|walking|transit
    const travel = mode === 'walking' ? 'walking' : mode === 'transit' ? 'transit' : 'driving';
    return `https://www.google.com/maps/dir/?api=1&origin=${fromLat},${fromLon}&destination=${toLat},${toLon}&travelmode=${travel}`;
  };

  const hkCenter = [22.32, 114.17];
  const hkZoom = 11;

  const userPos = (lat && lon) ? [parseFloat(lat), parseFloat(lon)] : null;

  return (
    <div>
      <h2>Hospitals</h2>

      <div style={{ marginBottom: 12 }}>
        <label style={{ marginRight: 8 }}>Region:</label>
        <select value={region} onChange={e => setRegion(e.target.value)} style={{ marginRight: 12 }}>
          <option value="">All</option>
          <option value="Hong Kong Island">Hong Kong Island</option>
          <option value="New Territories">New Territories</option>
          <option value="Lantau Island">Lantau Island</option>
        </select>

        <label style={{ marginRight: 8 }}>Filter by district:</label>
        <select value={district} onChange={e => setDistrict(e.target.value)}>
          <option value="">All</option>
          {districts.map(d => <option key={d} value={d}>{d}</option>)}
        </select>
      </div>

      <div style={{ marginBottom: 12, padding: 12, background: 'white', borderRadius: 6 }}>
        <h3>Your location</h3>
        <div style={{ display: 'flex', gap: 8 }}>
          <input placeholder="Latitude (e.g. 22.3)" value={lat} onChange={e => setLat(e.target.value)} />
          <input placeholder="Longitude (e.g. 114.1)" value={lon} onChange={e => setLon(e.target.value)} />
          <button onClick={onFindNearest}>Find nearest</button>
        </div>
        <div style={{ marginTop: 8 }}>
          Or use your browser location: <button onClick={() => {
            if (!navigator.geolocation) { setError('Geolocation not available in your browser'); return; }
            navigator.geolocation.getCurrentPosition(pos => {
              setLat(pos.coords.latitude.toFixed(6));
              setLon(pos.coords.longitude.toFixed(6));
              setError(null);
            }, err => setError('Could not get location: ' + err.message));
          }}>Use my location</button>
        </div>

        <div style={{ marginTop: 8 }}>
          Travel mode:
          <select value={transport} onChange={e => setTransport(e.target.value)} style={{ marginLeft: 8 }}>
            <option value="driving">Driving</option>
            <option value="transit">Public Transit</option>
            <option value="walking">Walking</option>
          </select>
        </div>

        {nearest && (
          <div style={{ marginTop: 12 }}>
            <h4>Nearest hospital: {nearest.hospital.name}</h4>
            <div>Distance: {nearest.distanceKm.toFixed(2)} km</div>
            <div>Estimated arrival time ({transport}): {estimateTime(nearest.distanceKm, transport)} minutes</div>
            <div style={{ marginTop: 6 }}>
              <a href={googleMapsLink(lat, lon, nearest.hospital.x, nearest.hospital.y, transport)} target="_blank" rel="noreferrer">Open route in Google Maps</a>
            </div>
          </div>
        )}
      </div>

      <div style={{ height: 420, marginBottom: 12 }}>
        <MapContainer center={userPos || hkCenter} zoom={hkZoom} style={{ height: '100%', width: '100%' }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

          {list.map(h => (
            <Marker key={h.nodeId} position={[h.x, h.y]}>
              <Popup>
                <div><strong>{h.name}</strong></div>
                <div>{h.district}</div>
              </Popup>
            </Marker>
          ))}

          {userPos && (
            <Marker position={userPos} icon={redPinIcon}>
              <Popup>Your location</Popup>
            </Marker>
          )}

          {nearest && userPos && (
            <Polyline positions={[userPos, [nearest.hospital.x, nearest.hospital.y]]} color="blue" />
          )}

        </MapContainer>
      </div>

      <ul style={{ listStyle: 'none', padding: 0 }}>
        {list.map((h) => (
          <li key={h.nodeId} style={{ marginBottom: 12, padding: 12, background: 'white', borderRadius: 6, boxShadow: '0 1px 3px rgba(0,0,0,0.06)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <strong style={{ fontSize: 16 }}>{h.name}</strong>
                <div style={{ fontSize: 13, color: '#666' }}>district: {h.district} — location: ({h.x}, {h.y})</div>
              </div>
              <div>
                <Link to={`/hospitals/${h.nodeId}`} style={{ marginLeft: 8 }}>Details</Link>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
