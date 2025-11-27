import React, { useState, useEffect, useMemo } from "react";
import {
  Card,
  Table,
  Button,
  Modal,
  Form,
  Input,
  Select,
  message,
  Tag,
  Space,
} from "antd";
import { PlusOutlined } from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import { doctorApi } from "../api/doctorApi";
import { hospitalApi } from "../api/hospitalApi";
import { departmentApi } from "../api/departmentApi";
import type { DoctorResponse, DoctorRequest } from "../types/doctor";
import type { HospitalResponse } from "../types/hospital";
import type { DepartmentResponse } from "../types/department";

const { Option } = Select;

const AdminDoctorsPage: React.FC = () => {
  const [doctors, setDoctors] = useState<DoctorResponse[]>([]);
  const [hospitals, setHospitals] = useState<HospitalResponse[]>([]);
  const [departments, setDepartments] = useState<DepartmentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [hospitalFilter, setHospitalFilter] = useState<number | undefined>();

  const fetchData = async () => {
    try {
      setLoading(true);
      const [doctorData, hospitalData, departmentData] = await Promise.all([
        doctorApi.getAllDoctors(),
        hospitalApi.getAllHospitals(),
        departmentApi.getAllDepartments(),
      ]);
      setDoctors(doctorData);
      setHospitals(hospitalData);
      setDepartments(departmentData);
    } catch (error: any) {
      message.error("Failed to fetch data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateDoctor = async (values: DoctorRequest) => {
    try {
      setLoading(true);
      await doctorApi.createDoctor(values);
      message.success("Doctor created successfully");
      setModalVisible(false);
      form.resetFields();
      fetchData();
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to create doctor");
    } finally {
      setLoading(false);
    }
  };

  const filteredDoctors = useMemo(() => {
    if (!hospitalFilter) {
      return doctors;
    }
    return doctors.filter((doctor) => doctor.hospitalId === hospitalFilter);
  }, [doctors, hospitalFilter]);

  const columns: ColumnsType<DoctorResponse> = [
    {
      title: "Name",
      key: "name",
      render: (_, record) => `Dr. ${record.firstName} ${record.lastName}`,
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "Specialization",
      dataIndex: "specialization",
      key: "specialization",
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
      title: "Phone",
      dataIndex: "phoneNumber",
      key: "phoneNumber",
    },
    {
      title: "Status",
      dataIndex: "isAvailable",
      key: "isAvailable",
      render: (isAvailable: boolean) => (
        <Tag color={isAvailable ? "green" : "red"}>
          {isAvailable ? "Available" : "Unavailable"}
        </Tag>
      ),
    },
  ];

  return (
    <div className="space-y-6" style={{ padding: '24px' }}>
      <div className="flex justify-between items-center" style={{
        background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        padding: '32px',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(240, 147, 251, 0.15)'
      }}>
        <div>
          <h1 className="text-3xl font-bold" style={{ color: '#ffffff', marginBottom: '8px' }}>Doctor Management</h1>
          <p style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '16px' }}>Manage doctors and their assignments</p>
        </div>
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => setModalVisible(true)}
          style={{
            background: '#ffffff',
            color: '#f5576c',
            borderColor: 'transparent',
            fontWeight: 600,
            height: '44px',
            paddingLeft: '24px',
            paddingRight: '24px'
          }}
        >
          Add Doctor
        </Button>
      </div>

      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          allowClear
          placeholder="Filter by hospital"
          style={{ width: 240 }}
          value={hospitalFilter}
          onChange={(value) => setHospitalFilter(value)}
          options={hospitals.map((hospital) => ({
            label: hospital.name,
            value: hospital.id,
          }))}
        />
      </Space>

      <Card>
        <Table
          columns={columns}
          dataSource={filteredDoctors}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="Add New Doctor"
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleCreateDoctor}>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: "Please enter email" },
              { type: "email", message: "Please enter valid email" },
            ]}
          >
            <Input placeholder="doctor@hospital.com" />
          </Form.Item>

          <Form.Item
            name="password"
            label="Password"
            rules={[
              { required: true, message: "Please enter password" },
              { min: 6, message: "Password must be at least 6 characters" },
            ]}
          >
            <Input.Password placeholder="Enter password" />
          </Form.Item>

          <Space style={{ width: "100%" }} size="large">
            <Form.Item
              name="firstName"
              label="First Name"
              rules={[{ required: true, message: "Required" }]}
            >
              <Input placeholder="John" style={{ width: 200 }} />
            </Form.Item>

            <Form.Item
              name="lastName"
              label="Last Name"
              rules={[{ required: true, message: "Required" }]}
            >
              <Input placeholder="Smith" style={{ width: 200 }} />
            </Form.Item>
          </Space>

          <Form.Item
            name="specialization"
            label="Specialization"
            rules={[{ required: true, message: "Please enter specialization" }]}
          >
            <Input placeholder="Cardiology" />
          </Form.Item>

          <Form.Item name="qualifications" label="Qualifications">
            <Input placeholder="MD, MBBS" />
          </Form.Item>

          <Form.Item name="phoneNumber" label="Phone Number">
            <Input placeholder="+852 1234 5678" />
          </Form.Item>

          <Form.Item
            name="hospitalId"
            label="Hospital"
            rules={[{ required: true, message: "Please select hospital" }]}
          >
            <Select placeholder="Select hospital" showSearch>
              {hospitals.map((hospital) => (
                <Option key={hospital.id} value={hospital.id}>
                  {hospital.name}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="departmentId"
            label="Department"
            rules={[{ required: true, message: "Please select department" }]}
          >
            <Select placeholder="Select department" showSearch>
              {departments.map((dept) => (
                <Option key={dept.id} value={dept.id}>
                  {dept.name} ({dept.code})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="bio" label="Bio">
            <Input.TextArea rows={3} placeholder="Brief biography or description" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                Create Doctor
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AdminDoctorsPage;
