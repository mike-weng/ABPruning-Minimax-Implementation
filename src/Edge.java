

public class Edge {
	private final int x;
	private final int y;
	private String state;
	private Hexagon ownedByHexagon;

	public Edge(int x, int y, Hexagon hexagon) {
		this.x = x;
		this.y = y;
		this.state = "+";
		this.ownedByHexagon = hexagon;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Hexagon getOwnedByHexagon() {
		return ownedByHexagon;
	}

	public void setOwnedByHexagon(Hexagon ownedByHexagon) {
		this.ownedByHexagon = ownedByHexagon;
	}

	@Override
	public String toString() {
		return "Edge [x=" + x + ", y=" + y + ", ownedByHexagon="
				+ ownedByHexagon + "]";
	}

	public boolean compareEdgeFromInput(int x, int y) {
		if (this.x == x && this.y == y) {
			return true;
		} else {
			return false;
		}
	}

}
