package clinicalappointment.model;

public class Hospital {
    private final int nodeId;
    private final String name;
    private final double x;
    private final double y;
    private final String district;

    public Hospital(int nodeId, String name) {
        this(nodeId, name, 0.0, 0.0, "");
    }

    public Hospital(int nodeId, String name, double x, double y) {
        this(nodeId, name, x, y, "");
    }

    public Hospital(int nodeId, String name, double x, double y, String district) {
        this.nodeId = nodeId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.district = district == null ? "" : district;
    }

    // Default constructor for frameworks
    public Hospital() {
        this.nodeId = 0;
        this.name = "";
        this.x = 0.0;
        this.y = 0.0;
        this.district = "";
    }

    public int getNodeId() { return nodeId; }
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getDistrict() { return district; }

    @Override
    public String toString() { return name + "(node=" + nodeId + ")"; }
}
