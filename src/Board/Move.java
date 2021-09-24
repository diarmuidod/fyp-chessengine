package Board;

import java.util.UUID;

public class Move {
    UUID id;
    UUID parentId;
    String move;

    public int startSquare;
    public int targetSquare;

    Move(int startSquare, int targetSquare, Flag ... moveFlag) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
    }

    enum Flag {
        NONE,
        CAPTURE,
        PROMOTE_QUEEN,
        PROMOTE_ROOK,
        PROMOTE_KNIGHT,
        PROMOTE_BISHOP,
        CASTLE_SHORT,
        CASTLE_LONG
    }
}
