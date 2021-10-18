package Board;

public class Perft {
    private MoveGenerator moveGenerator = new MoveGenerator();

    public void runPerft(String d, String f, String m) {
    	int depth = Integer.parseInt(d);
        String FEN = f;
        int start = getIndexFromSquare("" + m.charAt(0) + m.charAt(1));
        int target = getIndexFromSquare("" + m.charAt(2) + m.charAt(3));

        Board board = new Board(FEN);

        long total = perft(depth, board);

        for(Move move : moveGenerator.getLegalMoves(board)) {
            String startSq = squareFromIndex(move.startSquare);
            String targetSq = squareFromIndex(move.targetSquare);

            System.out.println(startSq + targetSq + " " + perft(depth - 1, moveGenerator.makeMove(move.startSquare, move.targetSquare, board)));
        }

        System.out.println("\n" + total);
    }

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
