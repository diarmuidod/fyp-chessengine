package Debug;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;

public class Perft {
    private final MoveGenerator moveGenerator = new MoveGenerator();

    //count all positions from given board to a given depth
    //used to determine move generation is operating correctly
    public long perft(int depth, Board board) {
        long moves = 0;

        if (depth < 1) return 1;

        if (depth == 1) return moveGenerator.getLegalMoves(board).size();

        for (Move m : moveGenerator.getLegalMoves(board)) {
            moves += perft(depth - 1, moveGenerator.makeMove(m, board));
        }

        return moves;
    }
}
