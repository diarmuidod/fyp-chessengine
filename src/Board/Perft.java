package Board;

public class Perft {
    private MoveGenerator moveGenerator = new MoveGenerator();

    public void runPerft(String d, String f, String m) {
    	int startSquare = -1, targetSquare = -1;
        int depth = Integer.parseInt(d);
        String FEN = f;

        String startSq = "";
        String targetSq = "";
/*
	if(!(m == null || m == "")) {
		startSq = "" + m.charAt(0) + m.charAt(1);
		targetSq = "" + m.charAt(2) + m.charAt(3);
		
        	startSquare = getIndexFromSquare(startSq);
        	targetSquare = getIndexFromSquare(targetSq);
        }
*/

        Board board = new Board(FEN);

//        if(!(m == null || m == "")) board = moveGenerator.makeMove(startSquare, targetSquare, board);

        for(Move move : moveGenerator.getLegalMoves(board)) {
        	System.out.println(move + " " + perft(depth - 1, board));
        }
        
        System.out.println("\n" + perft(depth, board));
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
