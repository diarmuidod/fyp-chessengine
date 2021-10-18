package Board;

public class Perft {
    private MoveGenerator moveGenerator = new MoveGenerator();

    public void runPerft(String d, String f, String... m) {
        int depth = Integer.parseInt(d);
        String FEN = f;

        String startSq = "" + m[0].charAt(0) + m[0].charAt(1);
        String targetSq = "" + m[0].charAt(2) + m[0].charAt(3);

        int startSquare = getIndexFromSquare(startSq);
        int targetSquare = getIndexFromSquare(targetSq);

        Board board = new Board(FEN);

        if(m == null || m[0] == "") moveGenerator.makeMove(startSquare, targetSquare, new Board(FEN));

        perft(depth, board);
    }

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
