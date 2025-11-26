import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Input, Button, Card, message, Typography, Select, DatePicker } from 'antd';
import type { Dayjs } from 'dayjs';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined, HomeOutlined } from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';
import type { RegisterRequest } from '../types/auth';
import { UserRole } from '../types/auth';

const { Title } = Typography;
const { Option } = Select;

interface RegisterFormValues {
  email: string;
  password: string;
  role: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  dateOfBirth?: Dayjs;
  address?: string;
  latitude?: string;
  longitude?: string;
  district?: string;
}

const RegisterPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  const onFinish = async (values: RegisterFormValues) => {
    setLoading(true);
    try {
      const registerData: RegisterRequest = {
        email: values.email,
        password: values.password,
        role: values.role as RegisterRequest['role'],
        firstName: values.firstName,
        lastName: values.lastName,
        phoneNumber: values.phoneNumber,
        dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : undefined,
        address: values.address,
        latitude: values.latitude ? parseFloat(values.latitude) : undefined,
        longitude: values.longitude ? parseFloat(values.longitude) : undefined,
        district: values.district,
      };

      await register(registerData);
      message.success('Registration successful!');
      navigate('/dashboard');
    } catch (error: unknown) {
      const errorMessage = error && typeof error === 'object' && 'response' in error
        ? (error as { response?: { data?: { message?: string } } }).response?.data?.message
        : undefined;
      message.error(errorMessage || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gradient-to-br from-indigo-500 via-purple-500 to-purple-600 py-5">
      <Card className="w-full max-w-lg shadow-lg rounded-lg">
        <div className="text-center mb-6">
          <Title level={2} className="mb-2">Hospital Management</Title>
          <Title level={4} type="secondary">Create Account</Title>
        </div>

        <Form
          form={form}
          name="register"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
          layout="vertical"
        >
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Please input your email!' },
              { type: 'email', message: 'Please enter a valid email!' }
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="Email"
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="Password"
            rules={[
              { required: true, message: 'Please input your password!' },
              { min: 6, message: 'Password must be at least 6 characters!' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Password"
            />
          </Form.Item>

          <Form.Item
            name="role"
            label="Role"
            rules={[{ required: true, message: 'Please select a role!' }]}
          >
            <Select placeholder="Select role">
              <Option value={UserRole.PATIENT}>Patient</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="firstName"
            label="First Name"
            rules={[{ required: true, message: 'Please input your first name!' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="First Name"
            />
          </Form.Item>

          <Form.Item
            name="lastName"
            label="Last Name"
            rules={[{ required: true, message: 'Please input your last name!' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="Last Name"
            />
          </Form.Item>

          <Form.Item
            name="phoneNumber"
            label="Phone Number"
          >
            <Input
              prefix={<PhoneOutlined />}
              placeholder="Phone Number"
            />
          </Form.Item>

          <Form.Item
            name="dateOfBirth"
            label="Date of Birth"
          >
            <DatePicker
              style={{ width: '100%' }}
              format="YYYY-MM-DD"
              placeholder="Select date of birth"
            />
          </Form.Item>

          <Form.Item
            name="address"
            label="Address"
          >
            <Input
              prefix={<HomeOutlined />}
              placeholder="Address"
            />
          </Form.Item>

          <Form.Item
            name="district"
            label="District"
          >
            <Input
              placeholder="District"
            />
          </Form.Item>

          <Form.Item
            name="latitude"
            label="Latitude (optional)"
          >
            <Input
              type="number"
              step="any"
              placeholder="Latitude"
            />
          </Form.Item>

          <Form.Item
            name="longitude"
            label="Longitude (optional)"
          >
            <Input
              type="number"
              step="any"
              placeholder="Longitude"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={loading}
              style={{ height: 40 }}
            >
              Register
            </Button>
          </Form.Item>
        </Form>

        <div className="text-center mt-4">
          <span className="text-gray-600">Already have an account? </span>
          <Link to="/login" className="text-indigo-600 hover:text-indigo-800 font-medium">
            Login now
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default RegisterPage;

