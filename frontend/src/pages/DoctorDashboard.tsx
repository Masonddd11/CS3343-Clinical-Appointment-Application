import React, { useState, useEffect } from "react";
import {
  Card,
  Table,
  Button,
  Tag,
  message,
  Modal,
  Descriptions,
  Statistic,
  Row,
  Col,
  Space,
} from "antd";
import {
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
} from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import { appointmentApi } from "../api/appointmentApi";
import type { AppointmentResponse } from "../types/appointment";
import dayjs from "dayjs";

const DoctorDashboard: React.FC = () => {
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedAppointment, setSelectedAppointment] =
    useState<AppointmentResponse | null>(null);
  const [detailsModalVisible, setDetailsModalVisible] = useState(false);

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const data = await appointmentApi.getDoctorAppointments();
      setAppointments(data);
    } catch (error) {
      message.error("Failed to fetch appointments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleMarkCompleted = async (id: number) => {
    try {
      await appointmentApi.markAppointmentCompleted(id);
      message.success("Appointment marked as completed");
      fetchAppointments();
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to update appointment");
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
      sorter: (a, b) =>
        dayjs(a.appointmentDate).unix() - dayjs(b.appointmentDate).unix(),
    },
    {
      title: "Time",
      dataIndex: "appointmentTime",
      key: "appointmentTime",
      render: (time: string) => dayjs(time, "HH:mm:ss").format("hh:mm A"),
    },
    {
      title: "Patient",
      dataIndex: "patientName",
      key: "patientName",
    },
    {
      title: "Hospital",
      dataIndex: "hospitalName",
      key: "hospitalName",
    },
    {
      title: "Department",
      dataIndex: "departmentName",
      key: "departmentName",
    },
    {
      title: "Reason",
      dataIndex: "reasonForVisit",
      key: "reasonForVisit",
      ellipsis: true,
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status: string) => (
        <Tag color={getStatusColor(status)}>{status}</Tag>
      ),
      filters: [
        { text: "Pending", value: "PENDING" },
        { text: "Confirmed", value: "CONFIRMED" },
        { text: "Completed", value: "COMPLETED" },
        { text: "Cancelled", value: "CANCELLED" },
      ],
      onFilter: (value, record) => record.status === value,
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => showDetails(record)}>
            Details
          </Button>
          {record.status === "CONFIRMED" && (
            <Button
              size="small"
              type="primary"
              icon={<CheckCircleOutlined />}
              onClick={() => handleMarkCompleted(record.id)}
            >
              Complete
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const upcomingAppointments = appointments.filter(
    (apt) => apt.status === "CONFIRMED" || apt.status === "PENDING"
  );
  const completedToday = appointments.filter(
    (apt) =>
      apt.status === "COMPLETED" &&
      dayjs(apt.appointmentDate).isSame(dayjs(), "day")
  );

  return (
    <div className="space-y-6" style={{ padding: '24px' }}>
      <div style={{
        background: 'linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%)',
        padding: '32px',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(137, 247, 254, 0.15)'
      }}>
        <h1 className="text-3xl font-bold" style={{ color: '#ffffff', marginBottom: '8px' }}>Doctor Dashboard</h1>
        <p style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '16px' }}>Manage your appointments and schedule</p>
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
              title="Completed Today"
              value={completedToday.length}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: "#1890ff" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Total Appointments"
              value={appointments.length}
              prefix={<ClockCircleOutlined />}
            />
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
          selectedAppointment?.status === "CONFIRMED" && (
            <Button
              key="complete"
              type="primary"
              icon={<CheckCircleOutlined />}
              onClick={() => {
                if (selectedAppointment) {
                  handleMarkCompleted(selectedAppointment.id);
                  setDetailsModalVisible(false);
                }
              }}
            >
              Mark Complete
            </Button>
          ),
        ]}
      >
        {selectedAppointment && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="Patient">
              {selectedAppointment.patientName}
            </Descriptions.Item>
            <Descriptions.Item label="Date">
              {dayjs(selectedAppointment.appointmentDate).format("MMMM DD, YYYY")}
            </Descriptions.Item>
            <Descriptions.Item label="Time">
              {dayjs(selectedAppointment.appointmentTime, "HH:mm:ss").format(
                "hh:mm A"
              )}
            </Descriptions.Item>
            <Descriptions.Item label="Hospital">
              {selectedAppointment.hospitalName}
            </Descriptions.Item>
            <Descriptions.Item label="Department">
              {selectedAppointment.departmentName}
            </Descriptions.Item>
            <Descriptions.Item label="Reason for Visit">
              {selectedAppointment.reasonForVisit}
            </Descriptions.Item>
            <Descriptions.Item label="Symptoms">
              {selectedAppointment.symptoms || "N/A"}
            </Descriptions.Item>
            <Descriptions.Item label="Status">
              <Tag color={getStatusColor(selectedAppointment.status)}>
                {selectedAppointment.status}
              </Tag>
            </Descriptions.Item>
            {selectedAppointment.notes && (
              <Descriptions.Item label="Notes">
                {selectedAppointment.notes}
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default DoctorDashboard;
