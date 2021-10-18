import Board.Board;
import Board.Move;
import Board.Perft;
import Board.MoveGenerator;
import GameManager.Game;

public class Main {
    public static void main(String[] args) {
        Perft perft = new Perft();
        MoveGenerator moveGenerator = new MoveGenerator();

        if(args.length == 3) {
            int depth = Integer.parseInt(args[0]) - 1;
            String FEN = args[1];
            int start = getIndexFromSquare("" + args[2].charAt(0) + args[2].charAt(1));
            int target = getIndexFromSquare("" + args[2].charAt(2) + args[2].charAt(3));

            Board board = moveGenerator.makeMove(start, target, new Board(FEN));

            long total = perft.perft(depth, board);

            for (Move move : moveGenerator.getLegalMoves(board)) {
                String startSq = squareFromIndex(move.startSquare);
                String targetSq = squareFromIndex(move.targetSquare);

                System.out.println(startSq + targetSq + " " + perft.perft(depth - 1, moveGenerator.makeMove(move.startSquare, move.targetSquare, board)));
            }

            System.out.println("\n" + total);
        } else if(args.length == 2) {
            int depth = Integer.parseInt(args[0]);
            String FEN = args[1];

            Board board = new Board(FEN);

            long total = perft.perft(depth, board);

            for(Move move : moveGenerator.getLegalMoves(board)) {
                String startSq = squareFromIndex(move.startSquare);
                String targetSq = squareFromIndex(move.targetSquare);

                System.out.println(startSq + targetSq + " " + perft.perft(depth - 1, moveGenerator.makeMove(move.startSquare, move.targetSquare, board)));
            }

            System.out.println("\n" + total);
        }
    }

    public static char getFile(int index) {
        return (char) ((index % 8) + 97);
    }

    public static char getRank(int index) {
        return (char) ((index / 8) + 49);
    }

    public static String squareFromIndex(int index) {
        char number = getRank(index);
        char letter = getFile(index);

        return letter + String.valueOf(number);
    }

    public static int getIndexFromSquare(String square) {
        int file = Integer.parseInt("" + ((square.charAt(0) - 97)) * 8) - 1;
        int rank = Integer.parseInt("" + square.charAt(1));

        return file + rank;
    }
}
