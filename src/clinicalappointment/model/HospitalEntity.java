package clinicalappointment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hospitals")
public class HospitalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private double lat;
    private double lon;
    private String district;
    private String region;

    public HospitalEntity() {}

    public HospitalEntity(String name, double lat, double lon, String district, String region) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.district = district;
        this.region = region;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}

