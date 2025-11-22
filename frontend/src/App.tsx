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

  if (user?.role === "PATIENT") {
    return <PatientDashboard />;
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-4">Doctor Dashboard</h1>
      <p className="text-gray-600">Doctor dashboard coming soon...</p>
    </div>
  );
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
        path="/appointments"
        element={
          <PrivateRoute>
            <Layout>
              <div className="p-6">
                <h1 className="text-2xl font-bold mb-4">My Appointments</h1>
                <div>Appointments list will be shown here</div>
              </div>
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
