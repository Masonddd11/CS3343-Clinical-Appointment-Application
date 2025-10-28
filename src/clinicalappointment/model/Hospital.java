package clinicalappointment.model;

public class Hospital {
    private final int nodeId;
    private final String name;
    private final double x;
    private final double y;

    public Hospital(int nodeId, String name) {
        this(nodeId, name, 0.0, 0.0);
    }

    public Hospital(int nodeId, String name, double x, double y) {
        this.nodeId = nodeId;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    // Default constructor for frameworks
    public Hospital() {
        this.nodeId = 0;
        this.name = "";
        this.x = 0.0;
        this.y = 0.0;
    }

    public int getNodeId() { return nodeId; }
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() { return name + "(node=" + nodeId + ")"; }
}
