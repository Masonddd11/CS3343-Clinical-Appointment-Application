import React from "react";
import { Card, Row, Col, Statistic } from "antd";
import {
  UserOutlined,
  HospitalOutlined,
  CalendarOutlined,
  TeamOutlined,
} from "@ant-design/icons";
import { useAuth } from "../hooks/useAuth";

const AdminDashboard: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-800">Admin Dashboard</h1>
        <p className="text-gray-600 mt-1">
          Manage users, hospitals, and system settings
        </p>
      </div>

      <Row gutter={16}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Total Users"
              value={0}
              prefix={<UserOutlined />}
              valueStyle={{ color: "#1890ff" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Hospitals"
              value={0}
              prefix={<HospitalOutlined />}
              valueStyle={{ color: "#52c41a" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Departments"
              value={0}
              prefix={<TeamOutlined />}
              valueStyle={{ color: "#722ed1" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Appointments"
              value={0}
              prefix={<CalendarOutlined />}
              valueStyle={{ color: "#fa8c16" }}
            />
          </Card>
        </Col>
      </Row>

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

