

/*
 * Chung Chun Weng chungchunw 695023
 * Kevin Lu klu2 695824
 */

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import aiproj.hexifence.*;

public class Board implements Piece {
	public static final String FREE_STATE = "+";
	public static final String NON_EXIST = "-";
	public HashMap<Key, Hexagon> hexagonsInGame;
	protected HashMap<Key, Edge> edgesInGame;
	protected String stateOfGame[][];
	protected int dimensionOfBoard;

	public Board(int n) {
		// Create the hexagons
		this.hexagonsInGame = generateHexagons(n);

		// Use the created hexagons to create the edges
		this.edgesInGame = generateEdges(hexagonsInGame);

		// Treating j as the column and i as the row
		setupStateOfGame(n);

	}

	// initialize the state of the game
	private void setupStateOfGame(int n) {
		this.dimensionOfBoard = 4 * n - 1;
		this.stateOfGame = new String[dimensionOfBoard][dimensionOfBoard];
		for (int i = 0; i < dimensionOfBoard; i++) {
			for (int j = 0; j < dimensionOfBoard; j++) {
				Key key = new Key(i, j);
				if (edgesInGame.containsKey(key)) {
					this.stateOfGame[i][j] = FREE_STATE;
				} else {
					this.stateOfGame[i][j] = NON_EXIST;
				}
			}
		}
	}

	// create a deep copy of a board by iteratively replicating each object
	public Board(Board board) {
		this.edgesInGame = new HashMap<Key, Edge>();
		for (Map.Entry<Key, Edge> entry : board.edgesInGame.entrySet()) {
			Key key = entry.getKey();
			Edge edge = entry.getValue();
			Edge newEdge = new Edge(edge.getX(), edge.getY(), null);
			newEdge.setState(edge.getState());
			this.edgesInGame.put(key, newEdge);
		}
		this.hexagonsInGame = new HashMap<Key, Hexagon>();
		for (Map.Entry<Key, Hexagon> entry : board.hexagonsInGame.entrySet()) {
			Key key = entry.getKey();
			Hexagon hexagon = entry.getValue();
			Hexagon newHexagon = new Hexagon(hexagon.getX(), hexagon.getY());
			newHexagon.setCapturedBy(hexagon.getCapturedBy());
			this.hexagonsInGame.put(key, newHexagon);
		}
		this.stateOfGame = new String[board.dimensionOfBoard][board.dimensionOfBoard];
		for (int i = 0; i < board.dimensionOfBoard; i++) {
			for (int j = 0; j < board.dimensionOfBoard; j++) {
				this.stateOfGame[i][j] = board.stateOfGame[i][j];
			}
		}
		this.dimensionOfBoard = board.dimensionOfBoard;
		// TODO Auto-generated constructor stub
	}

	// revert back to the previous board state before the specified move
	public void undoMove(Move move) {
		Key key = new Key(move.Row, move.Col);
		Edge edge = this.edgesInGame.get(key);
		edge.setState(FREE_STATE);

		// if hexagons were captured by the move, we need to revert it back
		ArrayList<Hexagon> hexagonsOpponentCaptured = this
				.checkNumberOfHexagonsCaptured(move);
		if (hexagonsOpponentCaptured.size() > 0) {
			for (Hexagon hexagon : hexagonsOpponentCaptured) {
				this.stateOfGame[hexagon.getX() * 2 + 1][hexagon.getY() * 2
						+ 1] = NON_EXIST;
			}
		}
		this.stateOfGame[move.Row][move.Col] = FREE_STATE;
	}

	// update the board state
	public void update(Move move) {
		int p = move.P;
		int x = move.Row;
		int y = move.Col;

		// check if the move will capture any hexagons
		ArrayList<Hexagon> hexagonsCaptured = this
				.checkNumberOfHexagonsCaptured(move);
		if (hexagonsCaptured.size() > 0) {
			// Then if there are hexagons capture then update the hexagon
			// coordinates
			for (Hexagon hexagon : hexagonsCaptured) {
				// create a hexagon move and re update the board with the move
				Move hexagonMove = new Move();
				hexagonMove.P = move.P;
				hexagonMove.Col = hexagon.getY() * 2 + 1;
				hexagonMove.Row = hexagon.getX() * 2 + 1;
				this.update(hexagonMove);
			}
		}
		if (p == Piece.BLUE) {
			// Check if it is a hexagon coordinate
			if (x % 2 == 1 && y % 2 == 1) {
				this.stateOfGame[x][y] = "b";

				// Scale the key so that it is consistent with the coordinates
				// that were
				// used to calculate the edge coordinates

				Key key = new Key((x - 1) / 2, (y - 1) / 2);

				Hexagon hexagon = hexagonsInGame.get(key);
				hexagon.setCapturedBy("b");
			} else {
				// not hexagon coordinate
				Key key = new Key(x, y);
				Edge edge = edgesInGame.get(key);
				this.stateOfGame[x][y] = "B";
				edge.setState("B");
			}
		} else if (p == Piece.RED) {
			if (x % 2 == 1 && y % 2 == 1) {
				this.stateOfGame[x][y] = "r";
				Key key = new Key((x - 1) / 2, (y - 1) / 2);
				Hexagon hexagon = hexagonsInGame.get(key);
				hexagon.setCapturedBy("r");
			} else {
				Key key = new Key(x, y);
				Edge edge = edgesInGame.get(key);
				this.stateOfGame[x][y] = "R";
				edge.setState("R");
			}
		}
	}

	// check how many hexagons can be captured after the specified move
	public ArrayList<Hexagon> checkNumberOfHexagonsCaptured(Move m) {
		int x = m.Row;
		int y = m.Col;
		ArrayList<Hexagon> hexagonsCaptured = new ArrayList<Hexagon>();

		// Iterate through all the edges and
		for (Hexagon hexagon : hexagonsInGame.values()) {
			ArrayList<Edge> freeEdges = new ArrayList<Edge>();
			for (Edge edge : hexagon.getAvailableEdges()) {
				Key key = new Key(edge.getX(), edge.getY());
				Edge edgeInGame = edgesInGame.get(key);
				if (edgeInGame.getState() == FREE_STATE) {
					freeEdges.add(edge);
				}
			}
			// check for number of edge that is free in a hexagon, if 1 then
			// the move can be used to capture the hexagon
			if (freeEdges.size() == 1) {
				Edge edge = freeEdges.get(0);
				if (edge.getX() == x && edge.getY() == y) {
					hexagonsCaptured.add(hexagon);
				}
			}
		}
		return hexagonsCaptured;
	}

	// check if move is valid
	public boolean isValidMove(Move m) {
		Key key = new Key(m.Row, m.Col);
		if (edgesInGame.containsKey(key)) {
			Edge edge = edgesInGame.get(key);
			if (edge.getState() == FREE_STATE) {
				return true;
			} else {
				return false;
			}
		}
		// edgesInGame doesn't contain the edge
		return false;
	}

	public static int k = 0;

	public Move makeMove(int p) {
		k++;
		int random = new Random().nextInt(edgesInGame.values().size());
		Edge edge = (Edge) edgesInGame.values().toArray()[random];
		Move move = new Move();
		move.Row = edge.getX();
		move.Col = edge.getY();
		move.P = p;

		if (isValidMove(move)) {
			// return move;
			if (this.checkNumberOfHexagonsCaptured(move).size() != 0) {
				return move;
			} else {
				if (k > 30) {
					return move;
				} else {
					return makeMove(p);
				}
			}
		} else {
			return makeMove(p);
		}
	}

	private HashMap<Key, Hexagon> generateHexagons(int n) {
		HashMap<Key, Hexagon> hexagonsInGame = new HashMap<Key, Hexagon>();

		boolean halfWayReached = false;
		int halfWay = (int) Math.ceil((double) n / 2);
		for (int i = 0; i < 2 * n - 1; i++) {
			if (!halfWayReached) {
				int j = 0;
				while (j <= halfWay + i) {
					Hexagon hexagon = new Hexagon(i, j);
					Key key = new Key(i, j);
					hexagonsInGame.put(key, hexagon);
					j += 1;
				}
			} else {
				int j = i - halfWay;
				while (j < 2 * n - 1) {
					Hexagon hexagon = new Hexagon(i, j);
					Key key = new Key(i, j);
					hexagonsInGame.put(key, hexagon);
					j += 1;
				}
			}

			if (i == halfWay) {
				halfWayReached = true;
			}
		}
		return hexagonsInGame;
	}

	public HashMap<Key, Edge> generateEdges(
			HashMap<Key, Hexagon> hexagonsInGame) {
		HashMap<Key, Edge> edgesInGame = new HashMap<Key, Edge>();
		for (Hexagon hexagon : hexagonsInGame.values()) {
			for (Edge edge : hexagon.getAvailableEdges()) {
				Key key = new Key(edge.getX(), edge.getY());
				edgesInGame.put(key, edge);
			}
		}
		return edgesInGame;
	}

	/*
	 * Calculate the number of free edges in game
	 */
	public int numPossibleMoves() {
		int numPossibleMoves = 0;
		for (Edge edge : edgesInGame.values()) {
			if (edge.getState().equals(FREE_STATE)) {
				numPossibleMoves++;
			}
		}
		return numPossibleMoves;
	}

	/*
	 * Calculate the largest number of cells that can be capture in 1 move
	 */
	public int maxNumCellsInOneMove() {
		int maxNumCells = 0;
		ArrayList<Edge> freeEdges = new ArrayList<Edge>();
		for (Hexagon hexagon : hexagonsInGame.values()) {
			int numFreeEdges = 0;
			Edge freeEdge = null;

			ArrayList<Edge> availableEdges = hexagon.getAvailableEdges();
			// check every available edge and count how many free edges
			for (Edge edge : availableEdges) {
				Key key = new Key(edge.getX(), edge.getY());
				Edge edgeInGame = edgesInGame.get(key);
				if (edgeInGame.getState().equals(FREE_STATE)) {
					freeEdge = edgeInGame;
					numFreeEdges++;
				}
			}
			// if only 1 free edge then hexagon can be completed in 1 move
			if (numFreeEdges == 1) {
				maxNumCells = 1;
				// check if the free edge has previously appeared, if it does
				// then the two hexagon shares one free edge
				if (freeEdges.contains(freeEdge)) {
					// since maximum number of edge that can be captured is 2
					// we can break the loop and return
					maxNumCells = 2;
					break;
				} else {
					freeEdges.add(freeEdge);
				}
			}
		}

		return maxNumCells;
	}

	/*
	 * Calculate number of edges that will complete a hexagon
	 */
	public int numCellsAvailableInOneMove() {
		int numCellsAvailable = 0;
		for (Hexagon hexagon : hexagonsInGame.values()) {
			int numFreeEdges = 0;
			ArrayList<Edge> availableEdges = hexagon.getAvailableEdges();
			// check every available edge and count how many free edges
			for (Edge edge : availableEdges) {
				Key key = new Key(edge.getX(), edge.getY());
				Edge edgeInGame = edgesInGame.get(key);
				if (edgeInGame.getState().equals(FREE_STATE)) {
					numFreeEdges++;
				}
			}
			// if only 1 free edge then hexagon can be completed in 1 move
			if (numFreeEdges == 1) {
				numCellsAvailable++;
			}
		}
		return numCellsAvailable;
	}

	// calculate how many hexagons are captured by each player
	public int calcEvaluationValue(int p) {
		int max = 0;
		int min = 0;
		for (int i = 0; i < this.dimensionOfBoard; i++) {
			for (int j = 0; j < this.dimensionOfBoard; j++) {
				if (p == Piece.BLUE) {
					if (this.stateOfGame[i][j].equals("b")) {
						max++;
					} else if (this.stateOfGame[i][j].equals("r")) {
						min++;
					}
				} else if (p == Piece.RED) {
					if (this.stateOfGame[i][j].equals("r")) {
						max++;
					} else if (this.stateOfGame[i][j].equals("b")) {
						min++;
					}
				}

			}
		}
		// evaluation value is the difference of the two
		return max - min;
	}

	// use the stateOfGame to print out the board
	public void printBoard(PrintStream output) {
		for (int i = 0; i < dimensionOfBoard; i++) {
			for (int j = 0; j < dimensionOfBoard; j++) {
				output.print(stateOfGame[i][j]);
				output.print(" ");
			}
			output.println();
		}
		// TODO Auto-generated method stub

	}

}
