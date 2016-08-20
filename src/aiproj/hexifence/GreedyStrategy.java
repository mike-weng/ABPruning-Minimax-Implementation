package aiproj.hexifence;

/*
 * Chung Chun Weng chungchunw 695023
 * Kevin Lu klu2 695824
 */

import java.util.Random;

public class GreedyStrategy implements MoveStrategy{
	private int count = 0;
	@Override
	public Move bestMove(Board board, int p, int depth) {
		count++;
		int random = new Random().nextInt(board.edgesInGame.values().size());
		Edge edge = (Edge) board.edgesInGame.values().toArray()[random];
		Move move = new Move();
		move.Row = edge.getX();
		move.Col = edge.getY();
		move.P = p;
		
		if (board.isValidMove(move)) {
			if (board.checkNumberOfHexagonsCaptured(move).size() != 0) {
				return move;
			} else {
				if (count > 50) {
					return move;
				} else {
					return bestMove(board, p, depth);
				}
			}
		} else {
			return bestMove(board, p, depth);
		}
	}

}
