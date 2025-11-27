import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { ConfigProvider } from "antd";
import { AuthProvider } from "./context/AuthContext";
import { useAuth } from "./hooks/useAuth";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import PatientDashboard from "./pages/PatientDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import DoctorDashboard from "./pages/DoctorDashboard";
import BookAppointmentPage from "./pages/BookAppointmentPage";
import AdminUsersPage from "./pages/AdminUsersPage";
import AdminHospitalsPage from "./pages/AdminHospitalsPage";
import AdminDoctorsPage from "./pages/AdminDoctorsPage";
import AdminAppointmentsPage from "./pages/AdminAppointmentsPage";
import "./App.css";

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-lg text-gray-600">Loading...</div>
      </div>
    );
  }

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

const RoleBasedDashboard: React.FC = () => {
  const { user } = useAuth();

  if (user?.role === "ADMIN") {
    return <AdminDashboard />;
  }

  if (user?.role === "DOCTOR") {
    return <DoctorDashboard />;
  }

  return <PatientDashboard />;
};

const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Layout>
              <RoleBasedDashboard />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/book-appointment"
        element={
          <PrivateRoute>
            <Layout>
              <BookAppointmentPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/users"
        element={
          <PrivateRoute>
            <Layout>
              <AdminUsersPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/hospitals"
        element={
          <PrivateRoute>
            <Layout>
              <AdminHospitalsPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/doctors"
        element={
          <PrivateRoute>
            <Layout>
              <AdminDoctorsPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/admin/appointments"
        element={
          <PrivateRoute>
            <Layout>
              <AdminAppointmentsPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
};

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: "#667eea",
        },
      }}
    >
      <AuthProvider>
        <Router>
          <AppRoutes />
        </Router>
      </AuthProvider>
    </ConfigProvider>
  );
}

export default App;
