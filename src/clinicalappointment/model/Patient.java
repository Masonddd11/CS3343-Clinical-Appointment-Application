package clinicalappointment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    private String id;
    private String name;
    private double x;
    private double y;
    private String hkid;
    private int age;
    private String sex;
    private String dob;
    private String illnessRecord;
    private String healthRecord;
    private String email;
    private String phone;

    public Patient() {
        this.id = "";
        this.name = "";
        this.x = 0.0;
        this.y = 0.0;
        this.hkid = "";
        this.age = 0;
        this.sex = "";
        this.dob = "";
        this.illnessRecord = "";
        this.healthRecord = "";
        this.email = "";
        this.phone = "";
    }

    public Patient(String name, double x, double y) {
        this();
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getHkid() { return hkid; }
    public int getAge() { return age; }
    public String getSex() { return sex; }
    public String getDob() { return dob; }
    public String getIllnessRecord() { return illnessRecord; }
    public String getHealthRecord() { return healthRecord; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setHkid(String hkid) { this.hkid = hkid; }
    public void setAge(int age) { this.age = age; }
    public void setSex(String sex) { this.sex = sex; }
    public void setDob(String dob) { this.dob = dob; }
    public void setIllnessRecord(String illnessRecord) { this.illnessRecord = illnessRecord; }
    public void setHealthRecord(String healthRecord) { this.healthRecord = healthRecord; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return name + " @ (" + x + "," + y + ")";
    }
}
