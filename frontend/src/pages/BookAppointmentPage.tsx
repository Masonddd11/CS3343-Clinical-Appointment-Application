import React, { useState } from "react";
import {
  Card,
  Form,
  Input,
  Button,
  Select,
  DatePicker,
  TimePicker,
  Steps,
  message,
  Row,
  Col,
  List,
  Tag,
  Space,
  Alert,
} from "antd";
import {
  MedicineBoxOutlined,
  EnvironmentOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import { symptomApi, type SymptomAnalysisResponse } from "../api/symptomApi";
import { pathfindingApi, type HospitalRecommendationResponse } from "../api/pathfindingApi";
import { hospitalApi } from "../api/hospitalApi";
import { departmentApi } from "../api/departmentApi";
import { doctorApi } from "../api/doctorApi";
import { appointmentApi } from "../api/appointmentApi";
import type { HospitalResponse } from "../types/hospital";
import type { DepartmentResponse } from "../types/department";
import type { DoctorResponse } from "../types/doctor";
import dayjs, { type Dayjs } from "dayjs";

const { TextArea } = Input;

const BookAppointmentPage: React.FC = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(0);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  // Step 1: Symptom Analysis
  const [symptomResult, setSymptomResult] = useState<SymptomAnalysisResponse | null>(null);
  const [symptomError, setSymptomError] = useState<string | null>(null);

  // Step 2: Hospital Selection
  const [recommendedHospitals, setRecommendedHospitals] = useState<HospitalRecommendationResponse[]>([]);
  const [selectedHospital, setSelectedHospital] = useState<HospitalResponse | null>(null);
  const [departments, setDepartments] = useState<DepartmentResponse[]>([]);
  const [selectedDepartment, setSelectedDepartment] = useState<number | null>(null);

  // Step 3: Doctor Selection
  const [doctors, setDoctors] = useState<DoctorResponse[]>([]);
  const [selectedDoctor, setSelectedDoctor] = useState<DoctorResponse | null>(null);

  // Step 4: Date & Time
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(null);
  const [selectedTime, setSelectedTime] = useState<Dayjs | null>(null);

  // Location state
  const [useDeviceLocation, setUseDeviceLocation] = useState(false);
  const [manualLocation, setManualLocation] = useState<{ latitude?: number; longitude?: number }>({});
  const [locationError, setLocationError] = useState<string | null>(null);
  const isLocationReady =
    typeof manualLocation.latitude === "number" && !Number.isNaN(manualLocation.latitude) &&
    typeof manualLocation.longitude === "number" && !Number.isNaN(manualLocation.longitude);

  const getUserLocation = async (): Promise<{ latitude: number; longitude: number }> => {
    if (isLocationReady) {
      return { latitude: manualLocation.latitude!, longitude: manualLocation.longitude! };
    }
    throw new Error("Please provide your location before continuing.");
  };

  // New: try to fetch and set device location into manualLocation
  const handleUseMyLocation = () => {
    setLocationError(null);
    if (!("geolocation" in navigator)) {
      setLocationError("Geolocation is not supported by your browser");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        setManualLocation({ latitude: position.coords.latitude, longitude: position.coords.longitude });
        setUseDeviceLocation(true);
      },
      () => {
        setLocationError("Failed to retrieve your location. Please enter it manually.");
        setUseDeviceLocation(false);
      }
    );
  };

  const handleSymptomAnalysis = async (values: { symptoms: string }) => {
    if (!isLocationReady) {
      setLocationError("Please provide your location (device or manual) before analyzing symptoms.");
      message.error("Location is required to recommend hospitals.");
      return;
    }

    try {
      setLoading(true);
      const result = await symptomApi.analyzeSymptom({ symptom: values.symptoms });
      setSymptomResult(result);
      setSymptomError(null);

      if (result.departmentId) {
        // Get recommended hospitals
        const location = await getUserLocation();
        const hospitals = await pathfindingApi.recommendHospitals({
          latitude: location.latitude,
          longitude: location.longitude,
          departmentId: result.departmentId,
          maxResults: 5,
        });
        setRecommendedHospitals(hospitals);
        setSelectedDepartment(result.departmentId);
        setCurrentStep(1);
      } else {
        setSymptomError("Could not determine department automatically. Please pick one manually.");
        message.warning("Could not determine department. Please select manually.");
      }
    } catch (error: any) {
      const errMsg = error.response?.data?.message || "Failed to analyze symptoms";
      setSymptomError(errMsg);
      message.error(errMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleHospitalSelect = async (hospitalId: number) => {
    try {
      setLoading(true);
      const hospital = await hospitalApi.getHospitalById(hospitalId);
      setSelectedHospital(hospital);

      // Fetch departments for this hospital
      const depts = await departmentApi.getAllDepartments();
      setDepartments(depts);

      setCurrentStep(2);
    } catch (error: any) {
      message.error("Failed to fetch hospital details");
    } finally {
      setLoading(false);
    }
  };

  const handleDepartmentSelect = async (departmentId: number) => {
    try {
      setLoading(true);
      setSelectedDepartment(departmentId);

      if (selectedHospital) {
        const doctorList = await doctorApi.getAllDoctors();
        const filteredDoctors = doctorList.filter(
          (d) =>
            d.hospitalId === selectedHospital.id &&
            d.departmentId === departmentId &&
            d.isAvailable
        );
        setDoctors(filteredDoctors);
        setCurrentStep(3);
      }
    } catch (error: any) {
      message.error("Failed to fetch doctors");
    } finally {
      setLoading(false);
    }
  };

  const handleDoctorSelect = (doctor: DoctorResponse) => {
    setSelectedDoctor(doctor);
    setCurrentStep(4);
  };

  const handleBooking = async () => {
    if (!selectedDoctor || !selectedHospital || !selectedDepartment || !selectedDate || !selectedTime) {
      message.error("Please complete all steps");
      return;
    }

    // Validate location presence
    if (!manualLocation.latitude || !manualLocation.longitude) {
      message.error("Please provide your location (use device or enter manually)");
      return;
    }

    try {
      setLoading(true);
      const values = form.getFieldsValue();
      await appointmentApi.bookAppointment({
        doctorId: selectedDoctor.id,
        hospitalId: selectedHospital.id,
        departmentId: selectedDepartment,
        appointmentDate: selectedDate.format("YYYY-MM-DD"),
        appointmentTime: selectedTime.format("HH:mm"),
        reasonForVisit: values.reasonForVisit || "General consultation",
        symptoms: values.symptoms || "",
        patientLatitude: manualLocation.latitude,
        patientLongitude: manualLocation.longitude,
      });

      message.success("Appointment booked successfully!");
      navigate("/dashboard");
    } catch (error: any) {
      const backendMessage = error.response?.data?.message;
      if (backendMessage?.toLowerCase().includes("24 hours")) {
        message.error("Bookings must be at least 24h ahead. Please pick a later slot.");
      } else if (backendMessage?.toLowerCase().includes("3 months")) {
        message.error("Bookings are limited to within the next 3 months.");
      } else {
        message.error(backendMessage || "Failed to book appointment");
      }
    } finally {
      setLoading(false);
    }
  };

  const disabledDate = (current: Dayjs) => {
    const today = dayjs().startOf("day");
    const maxDate = dayjs().add(3, "month");
    return current && (current < today.add(1, "day") || current > maxDate);
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-800">Book Appointment</h1>
        <p className="text-gray-600 mt-1">Follow the steps to book your appointment</p>
      </div>

      <Card>
        <Steps
          current={currentStep}
          className="mb-8"
          items={[
            { title: "Symptoms", icon: <MedicineBoxOutlined /> },
            { title: "Hospital", icon: <EnvironmentOutlined /> },
            { title: "Department" },
            { title: "Doctor" },
            { title: "Schedule", icon: <CalendarOutlined /> },
          ]}
        />

        <Form form={form} layout="vertical">
          {/* Step 0: Symptom Analysis */}
          {currentStep === 0 && (
            <div className="space-y-4">
              <Alert
                message="Step 1: Location & Symptoms"
                description="Provide your current location so we can rank nearby hospitals before describing your symptoms."
                type="info"
                showIcon
              />

              <div className="space-y-3">
                <h4 className="font-semibold">Your Location</h4>
                <p className="text-sm text-gray-500">Use GPS or enter coordinates manually.</p>
                <Space direction="vertical" style={{ width: "100%" }}>
                  <Space>
                    <Button onClick={handleUseMyLocation} icon={<EnvironmentOutlined />}>Use my location</Button>
                    <Button onClick={() => { setUseDeviceLocation(false); setManualLocation({}); }}>Reset</Button>
                  </Space>
                  {locationError && <Alert type="error" message={locationError} showIcon />}
                  <Row gutter={12}>
                    <Col xs={24} md={12}>
                      <Form.Item label="Latitude" required>
                        <Input
                          placeholder="22.3193"
                          value={manualLocation.latitude ?? ""}
                          onChange={(e) => {
                            const value = e.target.value.trim();
                            setManualLocation((state) => ({
                              ...state,
                              latitude: value ? Number(value) : undefined,
                            }));
                          }}
                        />
                      </Form.Item>
                    </Col>
                    <Col xs={24} md={12}>
                      <Form.Item label="Longitude" required>
                        <Input
                          placeholder="114.1694"
                          value={manualLocation.longitude ?? ""}
                          onChange={(e) => {
                            const value = e.target.value.trim();
                            setManualLocation((state) => ({
                              ...state,
                              longitude: value ? Number(value) : undefined,
                            }));
                          }}
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                </Space>
              </div>

              <Form.Item
                name="symptoms"
                label="Symptoms"
                rules={[{ required: true, message: "Please describe your symptoms" }]}
              >
                <TextArea rows={4} placeholder="E.g., fever, cough, headache..." />
              </Form.Item>
              <Button
                type="primary"
                loading={loading}
                disabled={!isLocationReady}
                onClick={() => form.validateFields(["symptoms"]).then(handleSymptomAnalysis)}
              >
                Analyze Symptoms
              </Button>
            </div>
          )}

          {/* Step 1: Hospital Selection */}
          {currentStep === 1 && (
            <div className="space-y-4">
              {symptomResult && (
                <Alert
                  message={`Recommended Department: ${symptomResult.departmentName}`}
                  description={`Confidence: ${(symptomResult.confidenceScore * 100).toFixed(0)}% | Matched: ${symptomResult.matchedKeywords.join(", ")}`}
                  type="success"
                  showIcon
                />
              )}
              {symptomError && (
                <Alert message={symptomError} type="warning" showIcon />
              )}
              <h3 className="text-lg font-semibold">Recommended Hospitals</h3>
              <List
                dataSource={recommendedHospitals}
                renderItem={(hospital) => (
                  <List.Item
                    actions={[
                      <Button
                        type="primary"
                        onClick={() => handleHospitalSelect(hospital.hospitalId)}
                      >
                        Select
                      </Button>,
                    ]}
                  >
                    <List.Item.Meta
                      title={hospital.hospitalName}
                      description={
                        <Space direction="vertical" size="small">
                          <span>{hospital.address}, {hospital.district}</span>
                          <Space>
                            <Tag color="blue">{hospital.distance.toFixed(1)} km away</Tag>
                            <Tag color={hospital.operationalStatus === "OPERATIONAL" ? "green" : "orange"}>
                              {hospital.operationalStatus}
                            </Tag>
                          </Space>
                          <span className="text-gray-500">{hospital.recommendationReason}</span>
                        </Space>
                      }
                    />
                  </List.Item>
                )}
              />
            </div>
          )}

          {/* Step 2: Department Selection */}
          {currentStep === 2 && (
            <div className="space-y-4">
              <Alert
                message={`Selected Hospital: ${selectedHospital?.name}`}
                type="info"
                showIcon
              />
              <Form.Item label="Select Department" required>
                <Select
                  size="large"
                  placeholder="Choose department"
                  value={selectedDepartment}
                  onChange={handleDepartmentSelect}
                  options={departments.map((dept) => ({
                    label: `${dept.name} (${dept.code})`,
                    value: dept.id,
                  }))}
                />
              </Form.Item>
            </div>
          )}

          {/* Step 3: Doctor Selection */}
          {currentStep === 3 && (
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">Available Doctors</h3>
              <List
                dataSource={doctors}
                renderItem={(doctor) => (
                  <List.Item
                    actions={[
                      <Button type="primary" onClick={() => handleDoctorSelect(doctor)}>
                        Select
                      </Button>,
                    ]}
                  >
                    <List.Item.Meta
                      title={`Dr. ${doctor.firstName} ${doctor.lastName}`}
                      description={
                        <Space direction="vertical" size="small">
                          <span>Specialization: {doctor.specialization}</span>
                          {doctor.qualifications && <span>Qualifications: {doctor.qualifications}</span>}
                          <Tag color="green">Available</Tag>
                        </Space>
                      }
                    />
                  </List.Item>
                )}
              />
            </div>
          )}

          {/* Step 4: Date & Time Selection */}
          {currentStep === 4 && (
            <div className="space-y-4">
              <Alert
                message={`Selected Doctor: Dr. ${selectedDoctor?.firstName} ${selectedDoctor?.lastName}`}
                description={isLocationReady ? `Patient location: ${manualLocation.latitude?.toFixed(4)}, ${manualLocation.longitude?.toFixed(4)}` : undefined}
                type="info"
                showIcon
              />

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="Appointment Date" required>
                    <DatePicker
                      size="large"
                      className="w-full"
                      disabledDate={disabledDate}
                      value={selectedDate}
                      onChange={setSelectedDate}
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="Appointment Time" required>
                    <TimePicker
                      size="large"
                      className="w-full"
                      format="HH:mm"
                      minuteStep={30}
                      value={selectedTime}
                      onChange={setSelectedTime}
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item name="reasonForVisit" label="Reason for Visit">
                <Input placeholder="General consultation" />
              </Form.Item>

              <Alert
                message="Need to adjust your location?"
                description="Go back to Step 1 if you need to update your coordinates before confirming."
                type="warning"
                showIcon
              />

              <Space>
                <Button onClick={() => setCurrentStep(3)}>Back</Button>
                <Button
                  type="primary"
                  icon={<CheckCircleOutlined />}
                  loading={loading}
                  onClick={handleBooking}
                  disabled={!selectedDate || !selectedTime}
                >
                  Confirm Booking
                </Button>
              </Space>
            </div>
          )}
        </Form>
      </Card>
    </div>
  );
};

export default BookAppointmentPage;
