package Board;

import java.util.HashMap;

public class BoardState {
    private BitBoard board;
    private static int enPassantSquare;
    private static boolean whiteToMove;
    private static String moveRegex = "[a-hA-H][1-8][a-hA-H][1-8]"; //move format - e2e4, e7e5, etc.

    //private static String sanRegex = "((?:(?:O-O(?:-O)?)|(?:[KQNBR](?:[a-h1-8]?x?[a-h][1-8])|(?:([a-h]x)?[a-h][1-7])|(?:([a-h]x)?[a-h][8])(?:=[QNBR]))))[+#]?";

    public BoardState() {
        enPassantSquare = -1;
        board = new BitBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        whiteToMove = true;
    }

    public BoardState(String FEN) {
        enPassantSquare = -1;
        board = new BitBoard(FEN);
        whiteToMove = FEN.split(" ", 2)[1].charAt(0) == 'w';
    }

    public Move makeMove(String move) {
        //System.out.println("Debug - En Passant: " + enPassantSquare);

        int startSquare = (((move.charAt(0) - 97) - 8) + (move.charAt(1) - 48) * 8);
        int targetSquare = (((move.charAt(2) - 97) - 8) + (move.charAt(3) - 48) * 8);

        //squares[targetSquare] = squares[startSquare];
        //squares[startSquare] = Piece.NONE;

        return new Move(startSquare, targetSquare);
    }

    public void printBoard() {
        board.printBoard();
    }
}
