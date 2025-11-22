import React, { useState, useEffect } from "react";
import { Card, List, Tag, Button, Empty, Spin, message } from "antd";
import {
  CalendarOutlined,
  ClockCircleOutlined,
  HospitalOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { appointmentApi } from "../api/appointmentApi";
import type { AppointmentResponse } from "../types/appointment";
import { useAuth } from "../hooks/useAuth";

interface AppointmentListProps {
  showActions?: boolean;
}

const AppointmentList: React.FC<AppointmentListProps> = ({
  showActions = true,
}) => {
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    try {
      setLoading(true);
      const data =
        user?.role === "DOCTOR"
          ? await appointmentApi.getDoctorAppointments()
          : await appointmentApi.getPatientAppointments();
      setAppointments(data);
    } catch (error) {
      message.error("Failed to load appointments");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id: number) => {
    try {
      await appointmentApi.cancelAppointment(id);
      message.success("Appointment cancelled successfully");
      loadAppointments();
    } catch (error) {
      message.error("Failed to cancel appointment");
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "CONFIRMED":
        return "blue";
      case "COMPLETED":
        return "green";
      case "CANCELLED":
        return "red";
      case "PENDING":
        return "orange";
      default:
        return "default";
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const formatTime = (timeString: string) => {
    return timeString;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Spin size="large" />
      </div>
    );
  }

  if (appointments.length === 0) {
    return (
      <Card>
        <Empty
          description="No appointments found"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      </Card>
    );
  }

  return (
    <List
      grid={{ gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 3 }}
      dataSource={appointments}
      renderItem={(appointment) => (
        <List.Item>
          <Card
            className="h-full"
            actions={
              showActions && appointment.status !== "CANCELLED"
                ? [
                  <Button
                    key="cancel"
                    danger
                    onClick={() => handleCancel(appointment.id)}
                    disabled={appointment.status === "COMPLETED"}
                  >
                    Cancel
                  </Button>,
                ]
                : undefined
            }
          >
            <div className="space-y-3">
              <div className="flex justify-between items-start">
                <Tag color={getStatusColor(appointment.status)}>
                  {appointment.status}
                </Tag>
                {appointment.pathfindingScore && (
                  <span className="text-xs text-gray-500">
                    Score: {appointment.pathfindingScore.toFixed(2)}
                  </span>
                )}
              </div>

              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <CalendarOutlined className="text-indigo-600" />
                  <span className="font-medium">
                    {formatDate(appointment.appointmentDate)}
                  </span>
                </div>

                <div className="flex items-center gap-2">
                  <ClockCircleOutlined className="text-indigo-600" />
                  <span>{formatTime(appointment.appointmentTime)}</span>
                </div>

                <div className="flex items-center gap-2">
                  <HospitalOutlined className="text-indigo-600" />
                  <span className="font-medium">{appointment.hospitalName}</span>
                </div>

                <div className="flex items-center gap-2">
                  <UserOutlined className="text-indigo-600" />
                  <span>
                    {user?.role === "PATIENT"
                      ? `Dr. ${appointment.doctorName}`
                      : appointment.patientName}
                  </span>
                </div>

                <div>
                  <span className="text-gray-600">Department: </span>
                  <span className="font-medium">
                    {appointment.departmentName}
                  </span>
                </div>

                {appointment.reasonForVisit && (
                  <div>
                    <span className="text-gray-600">Reason: </span>
                    <span>{appointment.reasonForVisit}</span>
                  </div>
                )}

                {appointment.symptoms && (
                  <div>
                    <span className="text-gray-600">Symptoms: </span>
                    <span>{appointment.symptoms}</span>
                  </div>
                )}
              </div>
            </div>
          </Card>
        </List.Item>
      )}
    />
  );
};

export default AppointmentList;

