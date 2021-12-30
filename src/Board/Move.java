package Board;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Move {
    public String move;
    public int startSquare;
    public int targetSquare;
    public Flag moveFlag;

    //internal use only (ideally want to clean this up, generation isn't particularly great, not a priority)
    public Move(int startSquare, int targetSquare, Board board, MoveGenerator moveGenerator, Flag moveFlag) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
        this.moveFlag = moveFlag;

        move = getMoveInSAN(board, moveGenerator);
    }

    //translate user input to board representation
    public Move(String move, Board board) {
        this.move = move;

        int start = 0, target = 0;
        if (!Character.isUpperCase(move.charAt(0))) { // pawn move
            BitSet sideToMove = (BitSet) (board.whiteToMove ? board.whitePieces.clone() : board.blackPieces.clone());

            //check each pawn of the side to move until match is found, for start and target squares
            for (int i = board.pawnPieces.nextSetBit(0); i >= 0; i = board.pawnPieces.nextSetBit(i + 1)) {
                if (sideToMove.get(i) && (getFile(i) == move.charAt(0))) {
                    this.startSquare = i;
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
        StringBuilder move = new StringBuilder();

        //Castling moves
        if (moveFlag.equals(Flag.CASTLE_SHORT)) return Flag.CASTLE_SHORT.getFlag();
        if (moveFlag.equals(Flag.CASTLE_LONG)) return Flag.CASTLE_LONG.getFlag();

        if (board.pawnPieces.get(startSquare)) { //Pawn Moves
            if (board.allPieces.get(targetSquare) || moveFlag.equals(Flag.EN_PASSANT)) {
                move = new StringBuilder(moveFromIndex(startSquare).charAt(0) + "x" + moveFromIndex(targetSquare));
            } else {
                move = new StringBuilder(moveFromIndex(targetSquare));
            }

            if (moveFlag.equals(Flag.PROMOTE_QUEEN)) move.append(Flag.PROMOTE_QUEEN.getFlag());
            if (moveFlag.equals(Flag.PROMOTE_KNIGHT)) move.append(Flag.PROMOTE_KNIGHT.getFlag());
            if (moveFlag.equals(Flag.PROMOTE_BISHOP)) move.append(Flag.PROMOTE_BISHOP.getFlag());
            if (moveFlag.equals(Flag.PROMOTE_ROOK)) move.append(Flag.PROMOTE_ROOK.getFlag());
        } else { //Piece Moves
            if (board.kingPieces.get(startSquare)) {
                move.append("K");
            } else {
                BitSet pieceType = new BitSet(64);
                if (board.queenPieces.get(startSquare)) {
                    pieceType = board.queenPieces;
                    move.append("Q");
                } else if (board.knightPieces.get(startSquare)) {
                    pieceType = board.knightPieces;
                    move.append("N");
                } else if (board.bishopPieces.get(startSquare)) {
                    pieceType = board.bishopPieces;
                    move.append("B");
                }
                if (board.rookPieces.get(startSquare)) {
                    pieceType = board.rookPieces;
                    move.append("R");
                }

                for (int i = pieceType.nextSetBit(0); i >= 0; i = pieceType.nextSetBit(i + 1)) {
                    if (i != startSquare && moveGenerator.isValidMove(i, targetSquare, board)) {
                        if (getFile(startSquare) == getFile(i)) {
                            move.append(getRank(startSquare));
                        } else {
                            move.append(getFile(startSquare));
                        }
                    }
                }
            }

            if (board.allPieces.get(targetSquare)) {
                move.append("x").append(moveFromIndex(targetSquare));
            } else {
                move.append(moveFromIndex(targetSquare));
            }
        }

        if (moveGenerator.kingInCheck(moveGenerator.makeMove(startSquare, targetSquare, board, moveFlag), !board.whiteToMove)) {
            if (moveGenerator.getLegalMoves(moveGenerator.makeMove(startSquare, targetSquare, board, moveFlag)).size() == 0) {
                move.append("#");
            } else {
                move.append("+");
            }
        }

        return move.toString();
    }

    public boolean isEqual(Move that) {
        return this.move.equals(that.move);
    }

    public String toString() {
        return move;
    }

    public enum Flag {
        NONE(""),
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