package Debug;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;

public class Perft {
    private final MoveGenerator moveGenerator = new MoveGenerator();

    public char getFile(int index) {
        return (char) ((index % 8) + 97);
    }

    public char getRank(int index) {
        return (char) ((index / 8) + 49);
    }

    public String squareFromIndex(int index) {
        char number = getRank(index);
        char letter = getFile(index);

        return letter + String.valueOf(number);
    }

    public long perft(int depth, Board board) {
        long moves = 0;

        if(depth < 1) return 1;

        if (depth == 1) return moveGenerator.getLegalMoves(board).size();

        for (Move m : moveGenerator.getLegalMoves(board)) {
            moves += perft(depth - 1, moveGenerator.makeMove(m, board));
        }

        return moves;
    }

    public int getIndexFromSquare(String square) {
        int file = Integer.parseInt("" + ((square.charAt(0) - 97)) * 8) - 1;
        int rank = Integer.parseInt("" + square.charAt(1));

        return file + rank;
    }
}
