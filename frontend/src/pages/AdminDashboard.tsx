import React, { useEffect, useState } from "react";
import { Card, Row, Col, Statistic, Spin, Alert } from "antd";
import {
  UserOutlined,
  MedicineBoxOutlined,
  CalendarOutlined,
  TeamOutlined,
} from "@ant-design/icons";
import { useAuth } from "../hooks/useAuth";
import { adminApi, type DashboardSummaryResponse } from "../api/adminApi";

const AdminDashboard: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardSummaryResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        setError(null);
        const summary = await adminApi.getDashboardSummary();
        setStats(summary);
      } catch (err: any) {
        setError(err.response?.data?.message || "Failed to load dashboard");
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  return (
    <div className="space-y-6" style={{ padding: '24px' }}>
      <div style={{
        background: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
        padding: '32px',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(250, 112, 154, 0.15)'
      }}>
        <h1 className="text-3xl font-bold" style={{ color: '#ffffff', marginBottom: '8px' }}>Admin Dashboard</h1>
        <p style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '16px' }}>
          Manage users, hospitals, and system settings
        </p>
      </div>

      {error && <Alert type="error" message={error} showIcon />}
      <Spin spinning={loading}>
        <Row gutter={16}>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="Total Users"
                value={stats?.totalUsers ?? 0}
                prefix={<UserOutlined />}
                valueStyle={{ color: "#1890ff" }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="Hospitals"
                value={stats?.totalHospitals ?? 0}
                prefix={<MedicineBoxOutlined />}
                valueStyle={{ color: "#52c41a" }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="Departments"
                value={stats?.totalDepartments ?? 0}
                prefix={<TeamOutlined />}
                valueStyle={{ color: "#722ed1" }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="Appointments"
                value={stats?.totalAppointments ?? 0}
                prefix={<CalendarOutlined />}
                valueStyle={{ color: "#fa8c16" }}
              />
            </Card>
          </Col>
        </Row>
        <Row gutter={16} style={{ marginTop: 16 }}>
          <Col xs={24} sm={12} md={8}>
            <Card>
              <Statistic
                title="Pending"
                value={stats?.pendingAppointments ?? 0}
                valueStyle={{ color: "#faad14" }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={8}>
            <Card>
              <Statistic
                title="Confirmed"
                value={stats?.confirmedAppointments ?? 0}
                valueStyle={{ color: "#13c2c2" }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={8}>
            <Card>
              <Statistic
                title="Completed"
                value={stats?.completedAppointments ?? 0}
                valueStyle={{ color: "#52c41a" }}
              />
            </Card>
          </Col>
        </Row>
      </Spin>

      <Row gutter={16} className="mt-4">
        <Col xs={24} md={12}>
          <Card title="Quick Actions" className="h-full">
            <div className="space-y-3">
              <p className="text-gray-600">
                Use the navigation menu to manage:
              </p>
              <ul className="list-disc list-inside space-y-2 text-gray-700">
                <li>Users and their roles</li>
                <li>Hospitals and their information</li>
                <li>Departments and specializations</li>
                <li>System-wide appointments</li>
              </ul>
            </div>
          </Card>
        </Col>
        <Col xs={24} md={12}>
          <Card title="System Information" className="h-full">
            <div className="space-y-2 text-gray-700">
              <p>
                <span className="font-medium">Logged in as:</span> {user?.email}
              </p>
              <p>
                <span className="font-medium">Role:</span> {user?.role}
              </p>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default AdminDashboard;
