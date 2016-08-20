

import java.io.PrintStream;
import java.util.ArrayList;

import aiproj.hexifence.*;

public class chungchunw implements Player, Piece {

	private int myPieceColor;
	private Board board;
	private MoveStrategy strategy;

	@Override
	public int init(int n, int p) {
		// TODO Auto-generated method stub
		this.myPieceColor = p;
		this.board = new Board(n);
		this.strategy = new GreedyStrategy();

		// strategy.bestMove(board, 1);
		// System.out.println(ABPruningStrategy.i);
		// System.exit(0);
		//

		if (board == null) {
			return -1;
		} else {
			return 0;
		}
	}

	public void recursivePrint(Node node, PrintStream output) {
		if (node.getChildren() == null) {
			return;
		}
		for (Node child : node.getChildren()) {
			recursivePrint(child, output);
		}
	}

	@Override
	public Move makeMove() {
		Move m = strategy.bestMove(board, myPieceColor, 0);
		board.update(m);
		return m;
		// call board to get the move
	}

	@Override
	public int opponentMove(Move m) {
		// Check for validity of opponents move, and then update state of the
		// game
		if (board.isValidMove(m)) {
			ArrayList<Hexagon> hexagonsOpponentCaptured = board
					.checkNumberOfHexagonsCaptured(m);
			// First input the edge
			board.update(m);
			if (hexagonsOpponentCaptured.size() > 0) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return -1;
		}
	}

	@Override
	public int getWinner() {
		// TODO Auto-generated method stub
		int numB = 0;
		int numR = 0;
		boolean completed = true;

		for (int i = 0; i < this.board.dimensionOfBoard; i++) {
			for (int j = 0; j < this.board.dimensionOfBoard; j++) {
				if (this.board.stateOfGame[i][j].equals("b")) {
					numB++;
				} else if (this.board.stateOfGame[i][j].equals("r")) {
					numR++;
				} else if (this.board.stateOfGame[i][j].equals("+")) {
					completed = false;
					break;
				}
			}
		}
		if (completed) {
			if (numB > numR) {
				return Piece.BLUE;
			} else if (numB < numR) {
				return Piece.RED;
			} else {
				return Piece.DEAD;
			}
		} else {
			return 0;
		}

	}

	@Override
	public void printBoard(PrintStream output) {
		board.printBoard(output);
		// TODO Auto-generated method stub

	}

}
