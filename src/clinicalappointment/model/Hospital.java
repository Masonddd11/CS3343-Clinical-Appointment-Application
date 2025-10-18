package clinicalappointment.model;

public class Hospital {
	private final int nodeId;
	private final String name;

	public Hospital(int nodeId, String name) {
		this.nodeId = nodeId;
		this.name = name;
	}

	public int getNodeId() { return nodeId; }
	public String getName() { return name; }

	@Override
	public String toString() { return name + "(node=" + nodeId + ")"; }
}

