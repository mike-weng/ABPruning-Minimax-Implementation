package aiproj.hexifence;

/*
 * Chung Chun Weng chungchunw 695023
 * Kevin Lu klu2 695824
 */

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import aiproj.hexifence.*;

public class klu2 implements Player, Piece {

	private int myPieceColor;
	private Board board;
	private MoveStrategy strategy;
	private int depth;
	private ArrayList<DepthForBranchingFactor> optimalDepth;
	private boolean boardIsInvalid;

	@Override
	public int init(int n, int p) {
		// TODO Auto-generated method stub
		this.myPieceColor = p;
		this.board = new Board(n);
		this.strategy = new ABPruningStrategy();
		this.boardIsInvalid = false;
		this.optimalDepth = new ArrayList<DepthForBranchingFactor>();
		// Setup the depth of our search
		if (n == 2) {
			this.depth = 4;
		} else if (n == 3) {
			this.depth = 3;
		} else {
			this.depth = 2;
		}
		int currentDepth = this.depth;
		int currentBranchingFactor = board.numPossibleMoves();
		DepthForBranchingFactor firstDepth = new DepthForBranchingFactor(
				currentDepth, currentBranchingFactor);
		optimalDepth.add(firstDepth);

		// Calculate when to change the depth of search
		while (currentBranchingFactor > currentDepth) {
			int numberOfMovesMadeForExtraDepth = (int) (currentBranchingFactor
					- Math.floor(Math.pow(currentBranchingFactor,
							(float) currentDepth / (currentDepth + 1))));
			currentBranchingFactor -= numberOfMovesMadeForExtraDepth;
			currentDepth += 1;
			DepthForBranchingFactor nextDepth = new DepthForBranchingFactor(
					currentDepth, currentBranchingFactor);
			optimalDepth.add(nextDepth);
		}

		Collections.reverse(optimalDepth);

		// strategy.bestMove(board, 1, 3);
		// System.out.println(ABPruningStrategy.i);
		// System.exit(0);
		//

		if (board == null) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public Move makeMove() {
		int depth = this.depth;

		// As we know that the optimal depth always
		for (DepthForBranchingFactor d : optimalDepth) {
			if (board.numPossibleMoves() > d.branchingFactor) {
				continue;
			} else {
				depth = d.depth;
				break;
			}
		}
		// use strategy to make best move
		Move m = strategy.bestMove(board, myPieceColor, depth);
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
			board.update(m);
			if (hexagonsOpponentCaptured.size() > 0) {
				return 1;
			} else {
				return 0;
			}
		} else {
			this.boardIsInvalid = !this.boardIsInvalid;
			return -1;
		}
	}

	@Override
	public int getWinner() {
		// TODO Auto-generated method stub
		int numB = 0;
		int numR = 0;
		boolean completed = true;
		
		if (this.boardIsInvalid) {
			return Piece.INVALID;
		}
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
			return Piece.EMPTY;
		}

	}

	@Override
	public void printBoard(PrintStream output) {
		board.printBoard(output);
	}

	private class DepthForBranchingFactor {
		public int depth;
		public int branchingFactor;

		public DepthForBranchingFactor(int depth, int branchingFactor) {
			this.depth = depth;
			this.branchingFactor = branchingFactor;
		}
	}

}
