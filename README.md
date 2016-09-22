# ABPruning-Minimax-Implementation - HEXIFENCE GAME

In this project, I am implementing a Minimax algorithm using Alpha-Beta pruning to play a board game. This game is played on a hexagonal board of dimension N, which comprises a grid of hexagonal cells. A hexagonal board has dimension N in the sense that each edge of the board comprises N cells.

## Usage
You can import the Referee source files in your Java editor and run it, or you can use the command prompt to run the Referee.
The following describes a scenario to use the command line to run the Referee.
Assume that 
1. you are in a Windows or Unix command prompt where the current directory is the root of the Referee-1-0/bin directory
2. binaries of an implementation of a player class called SimplePlayer is in “../../SimpleDirectory/bin”
3. the full class name of SimplePlayer class is “aiproj.hexifence.SimplePlayer”
4. this implementation uses the aima-core library which is in the directory “../../aima-core.jar”. 
The following command can be used to run the referee:
> java -cp ../../aima-core.jar;../../SimplePlayer/bin;. 
aiproj.hexifence.Referee 6 aiproj.hexifence.SimplePlayer 
aiproj.hexifence.SimplePlayer

## Rules
- A player is chosen arbitrarily to make the first move.
- Each player takes a turn at placing one of their pieces at any free edge on the
board.
- Players cannot move or remove a piece once it has been placed on the board.
- A hexagonal cell is captured once the player places his/her piece on the last
empty edge of that cell.
- The player who captures one or more cells with one move receives an
additional move.
- A cell is free if one or more of its edges are empty, and it has not been
captured.
- The game finishes when there are no free cells left on the board.
- The winner of the game is the player with the most captured cells.
- When a player captures a cell, it may contain pieces of either player on the
edges. The player who places his/her piece on the last empty edge of the cell is
the player who captures the cell.
- When a player captures a cell, the player is given an additional move. If that
additional move also captures a cell, then the player is given another additional move, and so on.

## Board
We number board cells using the notation (row, column), where (0,0) corresponds to the top left-most hexagon as shown below, and (2N-2, 2N-2) corresponds to the bottom right-most hexagon, i.e., row indices increase as we move down the board, while column indices increase as we move to the right. Below is an example of an N = 3 board with the all cells marked.

Two cells are adjacent if they share a common border (e.g., (1,1) has the adjacent
cells (0,0), (0,1), (1,0), (1,2), (2,1) and (2,2), whereas (0,0) has only 3 adjacent cells).

Note that each cell may have up to 5 adjacent cells. The edges for each hexagon are determined using the following coordinate system where the blue, red and green lines indicate edges (0, 0), (6, 3) and (7, 6). As you can see some edges do not exist in our board configuration, e.g. (0, 7), (3, 1) and (9, 3). The tuple representing each edge in this problem can vary between (0, 0) and (4N-2, 4N-2).

Initially, all the edges are empty and no cell is captured on the board.

## Players
There are two players named Blue and Red. Each player has their own set of pieces that they can place on the board. We will denote a piece for player Blue as B (or a blue line), and a piece for player Red as R (or a green line).

## Code structure
Our AI consists of three main parts, our player klu2; our board, which is the data structure that is used to represent the state of the game and several helper functions used by the evaluation function; and lastly, a node data structure that is used in our minimax search.

## Search strategy
Our search strategy uses a minimax search as it is a zero-sum game. Due to the space constraint we chose to only use one node during the minimax search, instead of generating children nodes. Then to search the tree nodes or equivalently our board states, we update the node and then pass it down to the next recursive function call. 

## Evaluation function
Our evaluation function was a simple function that calculated the difference between the number of hexagons that are captured by our player and the opponent player. 

## Creative techniques
We have employed a number of creative technique to decrease the run time of our minimax algorithm, and increase the search depth.

## Discussion
The first thing that we did was using alpha-beta pruning during our minimax search which decreased the run time of our search. This meant that we were able to search deeper in the tree using the same time.
The second thing that we did was to do a primitive sorting of the edges before choosing which branch to traverse. We calculated the evaluation function of our children nodes, and then picked the one with the highest value to go first.

The last thing that we did was increasing the search depth of the search algorithm as the branching factor decreased to calculate when to increase the depth it was calculated via:

b^d = (b-m)^(b+1) this implies that m = b - b^(d/(d+1))

Where: b is the branching factor or number of moves in game
	   d is the depth
           m is the number of moves made
	   (b-m) is the number of moves left in the game

Let: b’ = b - m
      d’ = d + 1

Then it is obvious from the above result that:

b’^d’ = b^d

Hence our branching factor would have to decrease by m for us to increase the search depth by 1.

Then to calculate the next branching factor whereby we can increase our search depth by 1 again, just calculate m’:

b’^d’ = (b’ - m’)^(d’+1) this implies that m’ = b’ - b’^(d’/(d’+1))

So then our branching factor would have to decrease by m’ for us to increase the search depth by 1 again, which is done until our depth is greater than the number of moves left in the game.
