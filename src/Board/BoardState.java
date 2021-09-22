package Board;

import java.util.HashMap;

public class BoardState {
    private BitBoard board;
    private static int enPassantSquare;
    private static boolean whiteToMove;
    private static final String REGEX = "(?:(?:O-O(?:-O)?)|(?:[KQNBR](?:[a-h1-8]?x?[a-h][1-8])|(?:(?:[a]x)?[b][2-7])|(?:(?:[b]x)?[ac][2-7])|(?:(?:[c]x)?[bd][2-7])|(?:(?:[d]x)?[ce][2-7])|(?:(?:[e]x)?[df][2-7])|(?:(?:[f]x)?[eg][2-7])|(?:(?:[g]x)?[fh][2-7]))|(?:(?:(?:[h]x)?[g][2-7])|(?:(?:[a]x)?[b][18])|(?:(?:[b]x)?[ac][18])|(?:(?:[c]x)?[bd][18])|(?:(?:[d]x)?[ce][18])|(?:(?:[e]x)?[df][18])|(?:(?:[f]x)?[eg][18])|(?:(?:[g]x)?[fh][18])|(?:(?:[h]x)?[g][18]))(?:=[QNBR]))[+#]?"; //move format - e2e4, e7e5, etc.
    
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
