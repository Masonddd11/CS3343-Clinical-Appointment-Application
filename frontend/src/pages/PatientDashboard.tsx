import React, { useState, useEffect } from "react";
import {
  Card,
  Row,
  Col,
  Statistic,
  Button,
  Table,
  Tag,
  Space,
  message,
  Modal,
  Descriptions,
} from "antd";
import {
  CalendarOutlined,
  PlusOutlined,
  MedicineBoxOutlined,
  EyeOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { appointmentApi } from "../api/appointmentApi";
import type { AppointmentResponse } from "../types/appointment";
import dayjs from "dayjs";

const PatientDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<AppointmentResponse | null>(null);
  const [detailsModalVisible, setDetailsModalVisible] = useState(false);

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const data = await appointmentApi.getPatientAppointments();
      setAppointments(data);
    } catch (error: any) {
      message.error("Failed to fetch appointments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleCancelAppointment = async (id: number) => {
    try {
      await appointmentApi.cancelAppointment(id);
      message.success("Appointment cancelled successfully");
      fetchAppointments();
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to cancel appointment");
    }
  };

  const showDetails = (appointment: AppointmentResponse) => {
    setSelectedAppointment(appointment);
    setDetailsModalVisible(true);
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      PENDING: "orange",
      CONFIRMED: "blue",
      COMPLETED: "green",
      CANCELLED: "red",
    };
    return colors[status] || "default";
  };

  const columns: ColumnsType<AppointmentResponse> = [
    {
      title: "Date",
      dataIndex: "appointmentDate",
      key: "appointmentDate",
      render: (date: string) => dayjs(date).format("MMM DD, YYYY"),
      sorter: (a, b) => dayjs(a.appointmentDate).unix() - dayjs(b.appointmentDate).unix(),
    },
    {
      title: "Time",
      dataIndex: "appointmentTime",
      key: "appointmentTime",
      render: (time: string) => dayjs(time, "HH:mm:ss").format("hh:mm A"),
    },
    {
      title: "Doctor",
      dataIndex: "doctorName",
      key: "doctorName",
    },
    {
      title: "Hospital",
      dataIndex: "hospitalName",
      key: "hospitalName",
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status: string) => <Tag color={getStatusColor(status)}>{status}</Tag>,
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<EyeOutlined />} onClick={() => showDetails(record)}>
            View
          </Button>
          {(record.status === "CONFIRMED" || record.status === "PENDING") && (
            <Button
              size="small"
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleCancelAppointment(record.id)}
            >
              Cancel
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const upcomingAppointments = appointments.filter(
    (apt) => apt.status === "CONFIRMED" || apt.status === "PENDING"
  );

  return (
    <div className="space-y-6" style={{ padding: '24px' }}>
      <div className="flex justify-between items-center" style={{
        background: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
        padding: '32px',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(168, 237, 234, 0.15)'
      }}>
        <div>
          <h1 className="text-3xl font-bold" style={{ color: '#2d3748', marginBottom: '8px' }}>
            Welcome back, {user?.email?.split("@")[0]}!
          </h1>
          <p style={{ color: '#4a5568', fontSize: '16px' }}>
            Manage your appointments and health records
          </p>
        </div>
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => navigate("/book-appointment")}
          style={{
            background: 'linear-gradient(90deg, #667eea 0%, #764ba2 100%)',
            borderColor: 'transparent',
            fontWeight: 600,
            height: '44px',
            paddingLeft: '24px',
            paddingRight: '24px',
            boxShadow: '0 4px 12px rgba(102, 126, 234, 0.3)'
          }}
        >
          Book Appointment
        </Button>
      </div>

      <Row gutter={16}>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Upcoming Appointments"
              value={upcomingAppointments.length}
              prefix={<CalendarOutlined />}
              valueStyle={{ color: "#3f8600" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Total Appointments"
              value={appointments.length}
              prefix={<CalendarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
+
            <Button type="link" onClick={() => navigate("/book-appointment")}
              style={{ padding: 0, height: "auto" }}>
              <Statistic
                title="Quick Access"
                value="Book Now"
                prefix={<MedicineBoxOutlined />}
                valueStyle={{ color: "#1890ff" }}
              />
            </Button>
          </Card>
        </Col>
      </Row>

      <Card title="My Appointments">
        <Table
          columns={columns}
          dataSource={appointments}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="Appointment Details"
        open={detailsModalVisible}
        onCancel={() => setDetailsModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailsModalVisible(false)}>
            Close
          </Button>,
        ]}
      >
        {selectedAppointment && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="Doctor">
              {selectedAppointment.doctorName}
            </Descriptions.Item>
            <Descriptions.Item label="Date">
              {dayjs(selectedAppointment.appointmentDate).format("MMMM DD, YYYY")}
            </Descriptions.Item>
            <Descriptions.Item label="Time">
              {dayjs(selectedAppointment.appointmentTime, "HH:mm:ss").format("hh:mm A")}
            </Descriptions.Item>
            <Descriptions.Item label="Hospital">
              {selectedAppointment.hospitalName}
            </Descriptions.Item>
            <Descriptions.Item label="Department">
              {selectedAppointment.departmentName}
            </Descriptions.Item>
            <Descriptions.Item label="Reason">
              {selectedAppointment.reasonForVisit || "N/A"}
            </Descriptions.Item>
            <Descriptions.Item label="Symptoms">
              {selectedAppointment.symptoms || "N/A"}
            </Descriptions.Item>
            <Descriptions.Item label="Status">
              <Tag color={getStatusColor(selectedAppointment.status)}>
                {selectedAppointment.status}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default PatientDashboard;
