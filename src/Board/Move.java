package Board;

import Utils.Utils;

import java.util.BitSet;
import java.util.Objects;

public class Move {
    public String move;
    public int startSquare;
    public int targetSquare;
    public Flag moveFlag;

    //Only used for root node in Engine
    public Move() {
        this.move = "null";
        this.startSquare = -1;
        this.targetSquare = -1;
    }

    //Called by MoveGenerator
    public Move(int startSquare, int targetSquare, Board board, MoveGenerator moveGenerator, Flag moveFlag) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
        this.moveFlag = moveFlag;

        move = getMoveInSAN(board, moveGenerator);
    }

    //translate user input
    //incomplete, do not call
    private Move(String move, Board board) {
        this.move = move;

        int start = 0, target = 0;
        if (!Character.isUpperCase(move.charAt(0))) { // pawn move
            BitSet sideToMove = (BitSet) (board.whiteToMove ? board.whitePieces.clone() : board.blackPieces.clone());

            //check each pawn of the side to move until match is found, for start and target squares
            for (int i = board.pawnPieces.nextSetBit(0); i >= 0; i = board.pawnPieces.nextSetBit(i + 1)) {
                if (sideToMove.get(i) && (Utils.getFileChar(i) == move.charAt(0))) {
                    this.startSquare = i;
                }
            }
        }
    }

    //Get square index refers to
    public String squareFromIndex(int index) {
        char number = Utils.getRankChar(index);
        char letter = Utils.getFileChar(index);

        return letter + String.valueOf(number);
    }

    //Get Move represented in Standard Algebraic Notation
    private String getMoveInSAN(Board board, MoveGenerator moveGenerator) {
        StringBuilder move = new StringBuilder();

        //Castling moves
        if (moveFlag.equals(Flag.CASTLE_SHORT)) return Flag.CASTLE_SHORT.getFlag();
        if (moveFlag.equals(Flag.CASTLE_LONG)) return Flag.CASTLE_LONG.getFlag();

        //Pawn Moves
        if (board.pawnPieces.get(startSquare)) {
            if (board.allPieces.get(targetSquare) || moveFlag.equals(Flag.EN_PASSANT)) {
                move = new StringBuilder(squareFromIndex(startSquare).charAt(0) + "x" + squareFromIndex(targetSquare));
            } else {
                move = new StringBuilder(squareFromIndex(targetSquare));
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
                } else if (board.rookPieces.get(startSquare)) {
                    pieceType = board.rookPieces;
                    move.append("R");
                }

                //Validating moves
                for (int i = pieceType.nextSetBit(0); i >= 0; i = pieceType.nextSetBit(i + 1)) {
                    if (i != startSquare && moveGenerator.isValidMove(i, targetSquare, board)) {
                        if (Utils.getFileChar(startSquare) == Utils.getFileChar(i)) {
                            move.append(Utils.getRankChar(startSquare));
                        } else {
                            move.append(Utils.getFileChar(startSquare));
                        }
                    }
                }
            }

            //Check for captures
            if (board.allPieces.get(targetSquare)) {
                move.append("x").append(squareFromIndex(targetSquare));
            } else {
                move.append(squareFromIndex(targetSquare));
            }
        }

        //Check for checks and checkmates
        if (moveGenerator.kingInCheck(moveGenerator.makeMove(startSquare, targetSquare, board, moveFlag), !board.whiteToMove)) {
            if (moveGenerator.getLegalMoves(moveGenerator.makeMove(startSquare, targetSquare, board, moveFlag)).size() == 0) {
                move.append("#");
            } else {
                move.append("+");
            }
        }

        return move.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Move)) return false;
        return this.toString().equals(that.toString());
    }

    public String toString() {
        return move == null ? "null" : move;
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