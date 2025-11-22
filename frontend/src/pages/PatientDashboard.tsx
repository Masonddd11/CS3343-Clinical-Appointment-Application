import React from "react";
import { Card, Row, Col, Statistic, Button } from "antd";
import {
  CalendarOutlined,
  PlusOutlined,
  HospitalOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import AppointmentList from "../components/AppointmentList";

const PatientDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">
            Welcome back, {user?.email?.split("@")[0]}!
          </h1>
          <p className="text-gray-600 mt-1">
            Manage your appointments and health records
          </p>
        </div>
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => navigate("/book-appointment")}
        >
          Book Appointment
        </Button>
      </div>

      <Row gutter={16}>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Upcoming Appointments"
              value={0}
              prefix={<CalendarOutlined />}
              valueStyle={{ color: "#3f8600" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Total Appointments"
              value={0}
              prefix={<CalendarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Hospitals Nearby"
              value={0}
              prefix={<HospitalOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card title="My Appointments" className="mt-6">
        <AppointmentList showActions={true} />
      </Card>
    </div>
  );
};

export default PatientDashboard;

