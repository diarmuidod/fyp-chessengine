package Board;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Move {
    String move;
    public int startSquare;
    public int targetSquare;
    public List<Flag> moveFlag;

    //internal use only (ideally want to clean this up, generation isn't particularly great, not a priority)
    public Move(int startSquare, int targetSquare, Board board, MoveGenerator moveGenerator, Flag... moveFlag) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
        this.moveFlag = Arrays.asList(moveFlag);

        move = getMoveInSAN(board, moveGenerator);
    }

    //translate user input to board representation
    public Move(String move, Board board) {
        this.move = move;

        int start = 0, target = 0;
        if(!Character.isUpperCase(move.charAt(0))) { // pawn move
            BitSet sideToMove = (BitSet) (board.whiteToMove ? board.whitePieces.clone() : board.blackPieces.clone());

            //check each pawn of the side to move until match is found, for start and target squares
            for(int i = board.pawnPieces.nextSetBit(0); i >= 0; i = board.pawnPieces.nextSetBit(i + 1)) {
                if(sideToMove.get(i) && (getFile(i) == move.charAt(0))) {
                    this.startSquare = i;
                    continue;
                }

                //only piece moves can have disambiguations i.e. Raxd8
                if(move.contains("x")) {

                }
            }
        }
    }

    public char getFile(int index) {
        return (char) ((index % 8) + 97);
    }

    public char getRank(int index) {
        return (char) ((index / 8) + 49);
    }

    public String moveFromIndex(int index) {
        char number = getRank(index);
        char letter = getFile(index);

        return letter + String.valueOf(number);
    }

    private String getMoveInSAN(Board board, MoveGenerator moveGenerator) {
        String move = "";

        //Castling moves
        if(moveFlag.contains(Flag.CASTLE_SHORT)) return Flag.CASTLE_SHORT.getFlag();
        if(moveFlag.contains(Flag.CASTLE_LONG)) return Flag.CASTLE_LONG.getFlag();

        if(board.pawnPieces.get(startSquare)) { //Pawn Moves
            if(board.allPieces.get(targetSquare) || moveFlag.contains(Flag.EN_PASSANT)) {
                move = moveFromIndex(startSquare).charAt(0) + "x" + moveFromIndex(targetSquare);
            } else {
                move = moveFromIndex(targetSquare);
            }

            if(moveFlag.contains(Flag.PROMOTE_QUEEN)) move += Flag.PROMOTE_QUEEN;
            if(moveFlag.contains(Flag.PROMOTE_KNIGHT)) move += Flag.PROMOTE_KNIGHT;
            if(moveFlag.contains(Flag.PROMOTE_BISHOP)) move += Flag.PROMOTE_BISHOP;
            if(moveFlag.contains(Flag.PROMOTE_ROOK)) move += Flag.PROMOTE_ROOK;
        } else { //Piece Moves
            if(board.kingPieces.get(startSquare)) move += "K";
            if(board.queenPieces.get(startSquare)) move += "Q";
            if(board.knightPieces.get(startSquare)) move += "N";
            if(board.bishopPieces.get(startSquare)) move += "B";
            if(board.rookPieces.get(startSquare)) move += "R";

            if(board.allPieces.get(targetSquare)) {
                move += "x" + moveFromIndex(targetSquare);
            } else {
                move += moveFromIndex(targetSquare);
            }
        }

        if(moveGenerator.kingInCheck(moveGenerator.makeMove(startSquare, targetSquare, board), !board.whiteToMove)) {
            move += "+";
        }

        return move;
    }

    public String toString() {
        return move;
    }

    public enum Flag {
        CASTLE_SHORT("O-O"),
        CASTLE_LONG("O-O-O"),
        PROMOTE_QUEEN("=Q"),
        PROMOTE_KNIGHT("=N"),
        PROMOTE_BISHOP("=B"),
        PROMOTE_ROOK("=R"),
        EN_PASSANT("(EP)");

        String flag;

        Flag(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return this.flag;
        }
    }
}