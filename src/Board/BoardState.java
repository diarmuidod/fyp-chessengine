package Board;

import java.util.HashMap;

public class BoardState {
    private static int[] squares;
    private static boolean whiteToMove;

    public BoardState(String FEN) {
        squares = new int[64];
        loadPositionFromFEN(FEN);
    }

    //move format "e2e4"
    public Move makeMove(String move) {
        int startSquare = (((move.charAt(0) - 97) - 8) + (move.charAt(1) - 48) * 8);
        int targetSquare = (((move.charAt(2) - 97) - 8) + (move.charAt(3) - 48) * 8);

        System.out.println(startSquare);
        System.out.println(targetSquare);

        squares[targetSquare] = squares[startSquare];
        squares[startSquare] = Piece.NONE;

        return new Move(startSquare, targetSquare);
    }

    public void loadPositionFromFEN(String FEN) {
        HashMap<Character, Integer> pieceType = new HashMap<>();
        pieceType.put('p', Piece.PAWN);
        pieceType.put('n', Piece.KNIGHT);
        pieceType.put('b', Piece.BISHOP);
        pieceType.put('r', Piece.ROOK);
        pieceType.put('q', Piece.QUEEN);
        pieceType.put('k', Piece.KING);

        String FENtoBoard = FEN.split(" ", 2)[0];
        int file = 0, rank = 7, type, colour;

        for (int i = 0; i < FENtoBoard.length(); i++) {
            char symbol = FENtoBoard.charAt(i);
            if(symbol == '/') {
                file = 0;
                rank--;
            } else {
                if(Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    type = pieceType.get(Character.toLowerCase(symbol));
                    colour = Character.isUpperCase(symbol) ? Piece.WHITE : Piece.BLACK;
                    squares[(rank*8) + file] = type | colour;
                    file++;
                }
            }
        }
    }

    public void printBoard() {
        int mark = 64;

        for (int i = 0; i < 8; i++) {
            for (int j = mark - 8; j < mark; j++) {
                System.out.print("[" + Piece.getPieceSymbol(squares[j]) + "]  ");
            }
            mark -= 8;
            System.out.println();
        }
    }
}
