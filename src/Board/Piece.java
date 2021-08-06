package Board;

import java.util.HashMap;

public class Piece {
    public static final int NONE = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;
    public static final int WHITE = 8;
    public static final int BLACK = 16;

    public static final HashMap<Integer, Character> pieceSymbolMap = new HashMap<Integer, Character>()
            {{ put(WHITE | PAWN, 'P');  put(WHITE | KNIGHT, 'N'); put(WHITE | BISHOP, 'B'); put(WHITE | ROOK, 'R'); put(WHITE | QUEEN, 'Q'); put(WHITE | KING, 'K');
               put(BLACK | PAWN, 'p');  put(BLACK | KNIGHT, 'n'); put(BLACK | BISHOP, 'b'); put(BLACK | ROOK, 'r'); put(BLACK | QUEEN, 'q'); put(BLACK | KING, 'k');
               put(NONE, ' '); }};

    public static char getPieceSymbol(int pieceValue) {
        return pieceSymbolMap.get(pieceValue);
    }
}
