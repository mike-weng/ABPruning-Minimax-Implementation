package aiproj.hexifence;
/*
 * Chung Chun Weng chungchunw 695023
 * Kevin Lu klu2 695824
 */

public interface MoveStrategy {
	public Move bestMove(Board board, int p, int depth);
}
