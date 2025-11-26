import React, { useState, useEffect } from "react";
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
import { PlusOutlined, UserOutlined } from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import { adminApi, type UserInfo } from "../api/adminApi";

const { Option } = Select;

const AdminUsersPage: React.FC = () => {
  const [users, setUsers] = useState<UserInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await adminApi.getAllUsers();
      setUsers(data);
    } catch (error: any) {
      message.error("Failed to fetch users");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleCreateUser = async (values: any) => {
    try {
      setLoading(true);
      await adminApi.createUser(values);
      message.success("User created successfully");
      setModalVisible(false);
      form.resetFields();
      fetchUsers();
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to create user");
    } finally {
      setLoading(false);
    }
  };

  const getRoleColor = (role: string) => {
    const colors: Record<string, string> = {
      ADMIN: "red",
      DOCTOR: "blue",
      PATIENT: "green",
    };
    return colors[role] || "default";
  };

  const columns: ColumnsType<UserInfo> = [
    {
      title: "User ID",
      dataIndex: "userId",
      key: "userId",
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "Role",
      dataIndex: "role",
      key: "role",
      render: (role: string) => <Tag color={getRoleColor(role)}>{role}</Tag>,
      filters: [
        { text: "Admin", value: "ADMIN" },
        { text: "Doctor", value: "DOCTOR" },
        { text: "Patient", value: "PATIENT" },
      ],
      onFilter: (value, record) => record.role === value,
    },
  ];

  return (
    <div className="space-y-6" style={{ padding: '24px' }}>
      <div className="flex justify-between items-center" style={{
        background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        padding: '32px',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(79, 172, 254, 0.15)'
      }}>
        <div>
          <h1 className="text-3xl font-bold" style={{ color: '#ffffff', marginBottom: '8px' }}>User Management</h1>
          <p style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '16px' }}>Manage system users and their roles</p>
        </div>
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => setModalVisible(true)}
          style={{
            background: '#ffffff',
            color: '#4facfe',
            borderColor: 'transparent',
            fontWeight: 600,
            height: '44px',
            paddingLeft: '24px',
            paddingRight: '24px'
          }}
        >
          Create User
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={users}
          rowKey="userId"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="Create New User"
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
      >
        <Form form={form} layout="vertical" onFinish={handleCreateUser}>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: "Please enter email" },
              { type: "email", message: "Please enter valid email" },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="user@example.com" />
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

          <Form.Item
            name="role"
            label="Role"
            rules={[{ required: true, message: "Please select role" }]}
          >
            <Select placeholder="Select role">
              <Option value="PATIENT">Patient</Option>
              <Option value="DOCTOR">Doctor</Option>
              <Option value="ADMIN">Admin</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="firstName"
            label="First Name"
            rules={[{ required: true, message: "Please enter first name" }]}
          >
            <Input placeholder="John" />
          </Form.Item>

          <Form.Item
            name="lastName"
            label="Last Name"
            rules={[{ required: true, message: "Please enter last name" }]}
          >
            <Input placeholder="Doe" />
          </Form.Item>

          <Form.Item name="phoneNumber" label="Phone Number">
            <Input placeholder="+852 1234 5678" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                Create User
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AdminUsersPage;
