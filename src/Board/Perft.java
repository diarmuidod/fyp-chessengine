package Board;

public class Perft {
    private MoveGenerator moveGenerator = new MoveGenerator();

    public long perft(int depth, Board board) {
        long moves = 0;

        if (depth == 0) {
            return 1;
        }

        for (Move m : moveGenerator.getLegalMoves(board)) {
            moves += perft(depth - 1, moveGenerator.makeMove(m.startSquare, m.targetSquare, board));
        }

        return moves;
    }

    public int getIndexFromSquare(String square) {
        int file = Integer.parseInt("" + ((square.charAt(0) - 97)) * 8) - 1;
        int rank = Integer.parseInt("" + square.charAt(1));

        return file + rank;
    }
}
