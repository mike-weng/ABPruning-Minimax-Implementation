

/*
 * Chung Chun Weng chungchunw 695023
 * Kevin Lu klu2 695824
 */

import java.util.ArrayList;
import java.util.HashMap;

import aiproj.hexifence.Move;
import aiproj.hexifence.Piece;

public class ABPruningStrategy implements MoveStrategy {
	private ArrayList<Node> firstLevelNodes;
	int startingDepth;

	@Override
	public Move bestMove(Board board, int p, int depth) {
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		// the children node of the rootNode
		this.firstLevelNodes = new ArrayList<Node>();
		this.startingDepth = depth;

		Board newBoard = new Board(board);
		Node rootNode = new Node(null, newBoard);
		int bestValue = recursiveBestMove(rootNode, alpha, beta, true, p,
				depth);

		Move bestMove = null;
		for (Node n : firstLevelNodes) {
			// iterate the children of rootnode and get the move with the same
			// value of the best value
			int evaluationValue = n.getValue();
			if (evaluationValue == bestValue) {
				bestMove = n.getMove();
				break;
			}
		}

		return bestMove;
	}

	// minimax algorithm with alpha beta pruning
	public int recursiveBestMove(Node node, int alpha, int beta,
			boolean maximisingPlayer, int p, int depth) {

		// set the previous move for when popping back up and undo the move
		Move prevMove = new Move();
		if (depth != startingDepth) {
			prevMove.P = node.getMove().P;
			prevMove.Row = node.getMove().Row;
			prevMove.Col = node.getMove().Col;

		}

		int bestValue;
		if (depth == 0) {
			// leaf node therefore use evaluation function to calculate value
			bestValue = node.getBoard().calcEvaluationValue(p);
		} else if (maximisingPlayer) {
			// Given that we are at a maximizing node, we know that we are
			// guaranteed to be able to get this state of the board
			// hence we are able to update alpha
			bestValue = alpha;

			Board parentBoard = node.getBoard();
			// sort the edgesInGame to get a better ordering for better pruning
			ArrayList<Edge> sortedEdgesInGame = sort(parentBoard,
					parentBoard.edgesInGame, maximisingPlayer);
			for (Edge e : sortedEdgesInGame) {
				Move m = new Move();
				// determine which color the move should be
				if (p == 1) {
					m.P = Piece.BLUE;
				} else {
					m.P = Piece.RED;
				}

				m.Row = e.getX();
				m.Col = e.getY();

				ArrayList<Hexagon> hexagonsCaptured = parentBoard
						.checkNumberOfHexagonsCaptured(m);

				if (parentBoard.isValidMove(m)) {
					// Updating the board state so that when recursiveBestMove
					// is called it will be equivalent to going down the tree
					parentBoard.update(m);
					node.setMove(m);
				} else {
					continue;
				}

				int childValue = 0;

				// if the move can capture a hexagon next recursion goes to Max
				// again
				if (hexagonsCaptured.size() >= 1) {
					childValue = recursiveBestMove(node, bestValue, beta, true,
							p, depth - 1);

				} else {
					childValue = recursiveBestMove(node, bestValue, beta, false,
							p, depth - 1);
				}

				// if we find a better board state which is equivalent to a
				// higher evaluation value then update the value of the node
				// (note that this is not the same value as alpha, it is the
				// value that the node currently is)
				// bestValue here is our alpha

				bestValue = Math.max(bestValue, childValue);

				// store the children node of the rootNode and their values
				if (depth == startingDepth) {
					Move newMove = new Move();
					newMove.P = m.P;
					newMove.Row = m.Row;
					newMove.Col = m.Col;
					Node newNode = new Node(m, null);
					newNode.setValue(bestValue);
					firstLevelNodes.add(newNode);
				}

				// To see if we can prune compare the value of the maximising
				// node with the beta value which is also the guaranteed value
				// for the minimising player

				if (bestValue >= beta) {
					break;
				}
			}
		} else {
			// Given that we are at a minimizing node, we know that we are
			// guaranteed to be abel to get this state of the board
			// hence we update beta to this value
			bestValue = beta;
			Board parentBoard = node.getBoard();
			for (Edge e : parentBoard.edgesInGame.values()) {

				Move m = new Move();
				if (p == 1) {
					m.P = Piece.RED;
				} else {
					m.P = Piece.BLUE;
				}

				m.Row = e.getX();
				m.Col = e.getY();

				ArrayList<Hexagon> hexagonsCaptured = parentBoard
						.checkNumberOfHexagonsCaptured(m);
				if (parentBoard.isValidMove(m)) {

					// First input the edge
					parentBoard.update(m);
					node.setMove(m);
				} else {
					continue;
				}

				// if the move can capture a hexagon next recursion goes to Min
				// again
				int childValue = 0;
				if (hexagonsCaptured.size() >= 1) {
					childValue = recursiveBestMove(node, alpha, bestValue,
							false, p, depth - 1);

				} else {
					childValue = recursiveBestMove(node, alpha, bestValue, true,
							p, depth - 1);
				}

				// if we find a better board state which is equivalent to a
				// lower evaluation value then update the value of the node
				// (note that this is not the same value as alpha, it is the
				// value that the node currently is)
				bestValue = Math.min(bestValue, childValue);

				if (depth == startingDepth) {
					Move newMove = new Move();
					newMove.P = m.P;
					newMove.Row = m.Row;
					newMove.Col = m.Col;
					Node newNode = new Node(m, null);
					newNode.setValue(bestValue);
					firstLevelNodes.add(newNode);
				}

				// To see if we can prune compare the value of the maximising
				// node with the beta value which is also the guaranteed value
				// for the minimising player

				if (bestValue <= alpha) {
					break;
				}
			}

		}

		if (depth != startingDepth) {
			node.getBoard().undoMove(prevMove);
		}
		return bestValue;
	}

	// sort the edgesInGame to have better ordering for pruning
	public ArrayList<Edge> sort(Board board, HashMap<Key, Edge> edgesInGame,
			boolean maximisingPlayer) {
		ArrayList<Edge> sortedEdgesInGame = new ArrayList<>();

		if (maximisingPlayer) {
			// put all the edges that will lead to capturing 2 hexagons at the
			// start, in descending order
			for (int i = 2; i >= 0; i--) {
				for (Edge e : edgesInGame.values()) {
					Move m = new Move();
					m.P = Piece.RED;
					m.Row = e.getX();
					m.Col = e.getY();
					int numHexagonsCaptured = board
							.checkNumberOfHexagonsCaptured(m).size();
					if (numHexagonsCaptured == i) {
						sortedEdgesInGame.add(e);
					}
				}
			}
		} else {
			// put all the edges that will lead to capturing no hexagons at the
			// start, in ascending order
			for (int i = 0; i < 3; i++) {
				for (Edge e : edgesInGame.values()) {
					Move m = new Move();
					m.P = Piece.RED;
					m.Row = e.getX();
					m.Col = e.getY();
					int numHexagonsCaptured = board
							.checkNumberOfHexagonsCaptured(m).size();
					if (numHexagonsCaptured == i) {
						sortedEdgesInGame.add(e);
					}
				}
			}
		}

		return sortedEdgesInGame;
	}
}
