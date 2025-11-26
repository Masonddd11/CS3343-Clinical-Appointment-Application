import React, { useState, useEffect } from "react";
import {
  Card,
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  message,
  Tag,
  Space,
} from "antd";
import { PlusOutlined, EditOutlined } from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import { hospitalApi } from "../api/hospitalApi";
import type { HospitalResponse, HospitalRequest } from "../types/hospital";

const { Option } = Select;

const AdminHospitalsPage: React.FC = () => {
  const [hospitals, setHospitals] = useState<HospitalResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingHospital, setEditingHospital] = useState<HospitalResponse | null>(null);
  const [form] = Form.useForm();

  const fetchHospitals = async () => {
    try {
      setLoading(true);
      const data = await hospitalApi.getAllHospitals();
      setHospitals(data);
    } catch (error: any) {
      message.error("Failed to fetch hospitals");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHospitals();
  }, []);

  const handleSubmit = async (values: HospitalRequest) => {
    try {
      setLoading(true);
      if (editingHospital) {
        await hospitalApi.updateHospital(editingHospital.id, values);
        message.success("Hospital updated successfully");
      } else {
        await hospitalApi.createHospital(values);
        message.success("Hospital created successfully");
      }
      setModalVisible(false);
      form.resetFields();
      setEditingHospital(null);
      fetchHospitals();
    } catch (error: any) {
      message.error(error.response?.data?.message || "Failed to save hospital");
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (hospital: HospitalResponse) => {
    setEditingHospital(hospital);
    form.setFieldsValue(hospital);
    setModalVisible(true);
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      OPERATIONAL: "green",
      CLOSED: "red",
      LIMITED: "orange",
    };
    return colors[status] || "default";
  };

  const columns: ColumnsType<HospitalResponse> = [
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "District",
      dataIndex: "district",
      key: "district",
    },
    {
      title: "Address",
      dataIndex: "address",
      key: "address",
      ellipsis: true,
    },
    {
      title: "Capacity",
      dataIndex: "capacity",
      key: "capacity",
    },
    {
      title: "Intensity",
      dataIndex: "currentIntensity",
      key: "currentIntensity",
      render: (intensity: number) => `${(intensity * 100).toFixed(0)}%`,
    },
    {
      title: "Status",
      dataIndex: "operationalStatus",
      key: "operationalStatus",
      render: (status: string) => (
        <Tag color={getStatusColor(status)}>{status}</Tag>
      ),
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Button
          icon={<EditOutlined />}
          size="small"
          onClick={() => handleEdit(record)}
        >
          Edit
        </Button>
      ),
    },
  ];

  return (
    <div className="space-y-6" style={{ padding: '24px' }}>
      <div className="flex justify-between items-center" style={{
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        padding: '32px',
        borderRadius: '16px',
        boxShadow: '0 8px 24px rgba(102, 126, 234, 0.15)'
      }}>
        <div>
          <h1 className="text-3xl font-bold" style={{ color: '#ffffff', marginBottom: '8px' }}>Hospital Management</h1>
          <p style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '16px' }}>Manage hospitals and their information</p>
        </div>
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => {
            setEditingHospital(null);
            form.resetFields();
            setModalVisible(true);
          }}
          style={{
            background: '#ffffff',
            color: '#667eea',
            borderColor: 'transparent',
            fontWeight: 600,
            height: '44px',
            paddingLeft: '24px',
            paddingRight: '24px'
          }}
        >
          Add Hospital
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={hospitals}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingHospital ? "Edit Hospital" : "Add New Hospital"}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
          setEditingHospital(null);
        }}
        footer={null}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            name="name"
            label="Hospital Name"
            rules={[{ required: true, message: "Please enter hospital name" }]}
          >
            <Input placeholder="General Hospital" />
          </Form.Item>

          <Form.Item
            name="address"
            label="Address"
            rules={[{ required: true, message: "Please enter address" }]}
          >
            <Input placeholder="123 Main Street" />
          </Form.Item>

          <Form.Item
            name="district"
            label="District"
            rules={[{ required: true, message: "Please enter district" }]}
          >
            <Input placeholder="Central" />
          </Form.Item>

          <Space style={{ width: "100%" }} size="large">
            <Form.Item
              name="latitude"
              label="Latitude"
              rules={[{ required: true, message: "Required" }]}
            >
              <InputNumber placeholder="22.3193" step={0.0001} style={{ width: 150 }} />
            </Form.Item>

            <Form.Item
              name="longitude"
              label="Longitude"
              rules={[{ required: true, message: "Required" }]}
            >
              <InputNumber placeholder="114.1694" step={0.0001} style={{ width: 150 }} />
            </Form.Item>
          </Space>

          <Space style={{ width: "100%" }} size="large">
            <Form.Item
              name="capacity"
              label="Capacity"
              rules={[{ required: true, message: "Required" }]}
            >
              <InputNumber placeholder="100" min={1} style={{ width: 150 }} />
            </Form.Item>

            <Form.Item
              name="currentIntensity"
              label="Current Intensity"
              rules={[{ required: true, message: "Required" }]}
            >
              <InputNumber
                placeholder="0.5"
                min={0}
                max={1}
                step={0.1}
                style={{ width: 150 }}
              />
            </Form.Item>
          </Space>

          <Form.Item
            name="operationalStatus"
            label="Operational Status"
            rules={[{ required: true, message: "Please select status" }]}
          >
            <Select placeholder="Select status">
              <Option value="OPERATIONAL">Operational</Option>
              <Option value="LIMITED">Limited Service</Option>
              <Option value="CLOSED">Closed</Option>
            </Select>
          </Form.Item>

          <Form.Item name="closureReason" label="Closure Reason (if applicable)">
            <Input.TextArea rows={2} placeholder="Reason for closure or limited service" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button onClick={() => {
                setModalVisible(false);
                form.resetFields();
                setEditingHospital(null);
              }}>
                Cancel
              </Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                {editingHospital ? "Update" : "Create"}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AdminHospitalsPage;
