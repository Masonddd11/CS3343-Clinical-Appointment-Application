package clinicalappointment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    private String name;
    private double x;
    private double y;

    public Patient() {
        this.name = "";
        this.x = 0.0;
        this.y = 0.0;
    }

    public Patient(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }

    public void setName(String name) { this.name = name; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() {
        return name + " @ (" + x + "," + y + ")";
    }
}
