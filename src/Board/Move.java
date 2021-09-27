package Board;

import java.util.UUID;

public class Move {
    UUID id;
    UUID parentId;
    String move;

    public int startSquare;
    public int targetSquare;
    public Flag[] moveFlag;

    Move(int startSquare, int targetSquare, Flag... moveFlag) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
        this.moveFlag = moveFlag;
    }

    public String moveFromIndex(int index) {
        char number = (char) ((index / 8) + 49);
        char letter = (char) ((index % 8) + 97);
        return letter + String.valueOf(number);
    }

    public String toString() {
        return moveFromIndex(startSquare) + " - " + moveFromIndex(targetSquare);
    }

    public enum Flag {
        PROMOTE_QUEEN("Q"),
        PROMOTE_ROOK("R"),
        PROMOTE_KNIGHT("K"),
        PROMOTE_BISHOP("B");

        String flag;

        Flag(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return this.flag;
        }
    }
}
