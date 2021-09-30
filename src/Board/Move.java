package Board;

public class Move {
    public int startSquare;
    public int targetSquare;
    public Flag[] moveFlag;

    public Board board;

    public Move(int startSquare, int targetSquare, Board board, Flag... moveFlag) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
        this.board = board;
        this.moveFlag = moveFlag;
    }

    public Move(String move) {

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
        CASTLE_SHORT("O-O"),
        CASTLE_LONG("O-O-O"),
        CAPTURE("x"),
        PROMOTE_QUEEN("=Q"),
        PROMOTE_KNIGHT("=N"),
        PROMOTE_BISHOP("=B"),
        PROMOTE_ROOK("=R");

        String flag;

        Flag(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return this.flag;
        }
    }
}
