package Board;

import java.util.HashMap;

public class BoardState {
    private static int[] squares;
    private static int enPassantSquare;
    private static boolean whiteToMove;
    private static String moveRegex = "[a-hA-H][1-8][a-hA-H][1-8]";

    public BoardState(String FEN) {
        squares = new int[64];
        loadPositionFromFEN(FEN);
    }

    //move format "e2e4"
    public Move makeMove(String move) {

        int startSquare = (((move.charAt(0) - 97) - 8) + (move.charAt(1) - 48) * 8);
        int targetSquare = (((move.charAt(2) - 97) - 8) + (move.charAt(3) - 48) * 8);

        squares[targetSquare] = squares[startSquare];
        squares[startSquare] = Piece.NONE;

        return new Move(startSquare, targetSquare);
    }

    public boolean validateMove(String moveToValidate) {
        if(moveToValidate == null) return false;
        if(!moveToValidate.matches("[a-hA-H][1-8][a-hA-H][1-8]")) return false;

        int startSquare = (((moveToValidate.charAt(0) - 97) - 8) + (moveToValidate.charAt(1) - 48) * 8);
        int targetSquare = (((moveToValidate.charAt(2) - 97) - 8) + (moveToValidate.charAt(3) - 48) * 8);

        if(startSquare == targetSquare) return false;
        if(squares[startSquare] == Piece.NONE) return false;

        if((squares[startSquare] & Piece.PAWN) == Piece.PAWN) return validatePawnKnightKing(Piece.PAWN);
        if((squares[startSquare] & Piece.KNIGHT) == Piece.KNIGHT) return validatePawnKnightKing(Piece.KNIGHT);
        if((squares[startSquare] & Piece.BISHOP) == Piece.BISHOP) return validatePawnKnightKing(Piece.BISHOP);
        if((squares[startSquare] & Piece.ROOK) == Piece.ROOK) return validatePawnKnightKing(Piece.ROOK);
        if((squares[startSquare] & Piece.QUEEN) == Piece.QUEEN) return validatePawnKnightKing(Piece.QUEEN);
        if((squares[startSquare] & Piece.KING) == Piece.KING) return validatePawnKnightKing(Piece.KING);

        return true;
    }

    public boolean validatePawnKnightKing(int pieceType) {
        switch(pieceType) {
            case Piece.PAWN:
                break;
            case Piece.KNIGHT:
                break;
            case Piece.KING:
                break;
        }
        return true;
    }

    public boolean validateBishopRookQueen(int pieceType) {
        switch(pieceType) {
            case Piece.BISHOP:
                break;
            case Piece.ROOK:
                break;
            case Piece.QUEEN:
                break;
        }
        return true;
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
