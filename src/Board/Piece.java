package Board;

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

    //Don't hate me, I'll fix it later
    public static char getPieceSymbol(int pieceValue) {
        if(pieceValue == (WHITE | PAWN)) return 'P';
        if(pieceValue == (WHITE | KNIGHT)) return 'N';
        if(pieceValue == (WHITE | BISHOP)) return 'B';
        if(pieceValue == (WHITE | ROOK)) return 'R';
        if(pieceValue == (WHITE | QUEEN)) return 'Q';
        if(pieceValue == (WHITE | KING)) return 'K';

        if(pieceValue == (BLACK | PAWN)) return 'p';
        if(pieceValue == (BLACK | KNIGHT)) return 'n';
        if(pieceValue == (BLACK | BISHOP)) return 'b';
        if(pieceValue == (BLACK | ROOK)) return 'r';
        if(pieceValue == (BLACK | QUEEN)) return 'q';
        if(pieceValue == (BLACK | KING)) return 'k';

        return ' ';
    }
}
