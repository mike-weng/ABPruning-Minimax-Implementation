package aiproj.hexifence;

/*
 * Chung Chun Weng chungchunw 695023
 * Kevin Lu klu2 695824
 */

import java.util.ArrayList;

public class Hexagon {
	private final int x;
	private final int y;
	private ArrayList<Edge> availableEdges;
	private String capturedBy;

	public Hexagon(int x, int y) {
		this.x = x;
		this.y = y;
		this.availableEdges = generateEdges();
		this.capturedBy = "-";
	}

	public ArrayList<Edge> getAvailableEdges() {
		return availableEdges;
	}

	public int numberOfAvailableEdges() {
		return availableEdges.size();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getCapturedBy() {
		return capturedBy;
	}

	public void setCapturedBy(String capturedBy) {
		this.capturedBy = capturedBy;
	}

	public void setAvailableEdges(ArrayList<Edge> availableEdges) {
		this.availableEdges = availableEdges;
	}

	private ArrayList<Edge> generateEdges() {
		ArrayList<Edge> availableEdges = new ArrayList<Edge>();
		Edge edgeA = new Edge(2 * x, 2 * y, this);
		availableEdges.add(edgeA);
		Edge edgeB = new Edge(2 * x, 2 * y + 1, this);
		availableEdges.add(edgeB);
		Edge edgeC = new Edge(2 * x + 1, 2 * y, this);
		availableEdges.add(edgeC);
		Edge edgeD = new Edge(2 * x + 1, 2 * y + 2, this);
		availableEdges.add(edgeD);
		Edge edgeE = new Edge(2 * x + 2, 2 * y + 1, this);
		availableEdges.add(edgeE);
		Edge edgeF = new Edge(2 * x + 2, 2 * y + 2, this);
		availableEdges.add(edgeF);

		return availableEdges;
	}

	@Override
	public String toString() {
		return "Hexagon [x=" + x + ", y=" + y + "]";
	}
}
