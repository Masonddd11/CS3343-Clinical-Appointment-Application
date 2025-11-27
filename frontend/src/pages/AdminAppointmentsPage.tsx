import React, { useEffect, useMemo, useState } from "react";
import {
  Card,
  Table,
  Tag,
  Space,
  Button,
  message,
  Select,
  Modal,
  Descriptions,
} from "antd";
import type { ColumnsType } from "antd/es/table";
import { appointmentApi } from "../api/appointmentApi";
import type { AppointmentResponse } from "../types/appointment";
import dayjs from "dayjs";

const statusColors: Record<string, string> = {
  PENDING: "orange",
  CONFIRMED: "blue",
  COMPLETED: "green",
  CANCELLED: "red",
};

const AdminAppointmentsPage: React.FC = () => {
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<string | undefined>();
  const [selected, setSelected] = useState<AppointmentResponse | null>(null);
  const [detailsVisible, setDetailsVisible] = useState(false);

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const data = await appointmentApi.getAllAppointments();
      setAppointments(data);
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to load appointments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleStatusChange = async (id: number, status: "CONFIRMED" | "CANCELLED" | "COMPLETED") => {
    try {
      await appointmentApi.updateAppointmentStatus(id, status);
      message.success("Appointment updated");
      fetchAppointments();
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to update appointment");
    }
  };

  const filteredAppointments = useMemo(() => {
    if (!selectedStatus) return appointments;
    return appointments.filter((appt) => appt.status === selectedStatus);
  }, [appointments, selectedStatus]);

  const columns: ColumnsType<AppointmentResponse> = [
    {
      title: "Date",
      dataIndex: "appointmentDate",
      key: "date",
      render: (date) => dayjs(date).format("YYYY-MM-DD"),
    },
    {
      title: "Time",
      dataIndex: "appointmentTime",
      key: "time",
      render: (time) => dayjs(time, "HH:mm:ss").format("HH:mm"),
    },
    {
      title: "Patient",
      dataIndex: "patientName",
      key: "patient",
    },
    {
      title: "Doctor",
      dataIndex: "doctorName",
      key: "doctor",
    },
    {
      title: "Hospital",
      dataIndex: "hospitalName",
      key: "hospital",
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status: string) => <Tag color={statusColors[status] || "default"}>{status}</Tag>,
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => { setSelected(record); setDetailsVisible(true); }}>
            View
          </Button>
          <Select
            size="small"
            value={record.status}
            onChange={(value) => handleStatusChange(record.id, value as "CONFIRMED" | "CANCELLED" | "COMPLETED")}
            options={["CONFIRMED", "CANCELLED", "COMPLETED"].map((status) => ({
              label: status,
              value: status,
              disabled: record.status === status,
            }))}
          />
        </Space>
      ),
    },
  ];

  return (
    <div className="space-y-6" style={{ padding: "24px" }}>
      <div className="flex justify-between items-center" style={{
        background: "linear-gradient(135deg, #43cea2 0%, #185a9d 100%)",
        padding: "32px",
        borderRadius: "16px",
        boxShadow: "0 8px 24px rgba(24, 90, 157, 0.15)",
      }}>
        <div>
          <h1 className="text-3xl font-bold" style={{ color: "#fff", marginBottom: "8px" }}>Appointment Management</h1>
          <p style={{ color: "rgba(255, 255, 255, 0.9)", fontSize: "16px" }}>
            Monitor and manage all patient bookings
          </p>
        </div>
        <Select
          allowClear
          placeholder="Filter by status"
          style={{ width: 200 }}
          value={selectedStatus}
          onChange={(value) => setSelectedStatus(value)}
          options={["PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"].map((status) => ({
            label: status,
            value: status,
          }))}
        />
      </div>

      <Card>
        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={filteredAppointments}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="Appointment Details"
        open={detailsVisible}
        onCancel={() => setDetailsVisible(false)}
        footer={null}
        width={600}
      >
        {selected && (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="Patient">{selected.patientName}</Descriptions.Item>
            <Descriptions.Item label="Doctor">{selected.doctorName}</Descriptions.Item>
            <Descriptions.Item label="Hospital">{selected.hospitalName}</Descriptions.Item>
            <Descriptions.Item label="Department">{selected.departmentName}</Descriptions.Item>
            <Descriptions.Item label="Date">{dayjs(selected.appointmentDate).format("YYYY-MM-DD")}</Descriptions.Item>
            <Descriptions.Item label="Time">{dayjs(selected.appointmentTime, "HH:mm:ss").format("HH:mm")}</Descriptions.Item>
            <Descriptions.Item label="Status">{selected.status}</Descriptions.Item>
            {selected.reasonForVisit && (
              <Descriptions.Item label="Reason">{selected.reasonForVisit}</Descriptions.Item>
            )}
            {selected.symptoms && (
              <Descriptions.Item label="Symptoms">{selected.symptoms}</Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default AdminAppointmentsPage;
