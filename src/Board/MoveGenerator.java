package Board;

import java.util.BitSet;

public class MoveGenerator {
    private static final int[] pawnOffsets   = {8, 16, 7, 9}; //white offsets, values inverted for black
    private static final int[] knightOffsets = {6, 15, 17, 10, -6, -15, -17, -10};
    private static final int[] bishopOffsets = {7, 9, -7, -9};
    private static final int[] rookOffsets   = {8, 1, -8, -1};
    private static final int[] queenOffsets  = {7, 8, 9, 1, -7, -8, -9, -1};
    private static final int[] kingOffsets   = {7, 8, 9, 1, 2, -7, -8, -9, -1, -2}; //redundant but idc, its my code, leave me alone

    public BitSet getPawnMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet pawnMoves = new BitSet(64);
        if(whiteToPlay) {
            //forward one
            if(!currentBoard.allPieces.get(position + pawnOffsets[0])) pawnMoves.set(position + pawnOffsets[0]);
            //forward two
            if(!currentBoard.allPieces.get(position + pawnOffsets[0]) && !currentBoard.allPieces.get(position + pawnOffsets[1])) {
                pawnMoves.set(position + pawnOffsets[1]);
            }

            //capture left and right, including en passant
            //beware an edge case involving a horizontal pin making en passant illegal.
            if(currentBoard.blackPieces.get(position + pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2]) pawnMoves.set(position + pawnOffsets[2]);
            if(currentBoard.blackPieces.get(position + pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3]) pawnMoves.set(position + pawnOffsets[3]);

            //promotions

        } else {
            //forward one
            if(!currentBoard.allPieces.get(position - pawnOffsets[0])) pawnMoves.set(position - pawnOffsets[0]);
            //forward two
            if(!currentBoard.allPieces.get(position - pawnOffsets[0]) && !currentBoard.allPieces.get(position - pawnOffsets[1])) {
                pawnMoves.set(position - pawnOffsets[1]);
            }

            //capture left and right, including en passant
            if(currentBoard.blackPieces.get(position - pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2]) pawnMoves.set(position - pawnOffsets[2]);
            if(currentBoard.blackPieces.get(position - pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3]) pawnMoves.set(position - pawnOffsets[3]);
        }
        return pawnMoves;
    }

    public BitSet getKnightMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet knightMoves = new BitSet(64);

        int rank = position / 8, file = position % 8;

        if(whiteToPlay) {
            if(rank >= 2) {
                if(!currentBoard.whitePieces.get(position + knightOffsets[5])) knightMoves.set(position + knightOffsets[5]);
                if(!currentBoard.whitePieces.get(position + knightOffsets[6])) knightMoves.set(position + knightOffsets[6]);
            }

            if(rank <= 5) {
                if(!currentBoard.whitePieces.get(position + knightOffsets[1])) knightMoves.set(position + knightOffsets[1]);
                if(!currentBoard.whitePieces.get(position + knightOffsets[2])) knightMoves.set(position + knightOffsets[2]);
            }

            if(file >= 2) {
                if(!currentBoard.whitePieces.get(position + knightOffsets[0])) knightMoves.set(position + knightOffsets[0]);
                if(!currentBoard.whitePieces.get(position + knightOffsets[4])) knightMoves.set(position + knightOffsets[4]);
            }

            if(file <= 5) {
                if(!currentBoard.whitePieces.get(position + knightOffsets[3])) knightMoves.set(position + knightOffsets[3]);
                if(!currentBoard.whitePieces.get(position + knightOffsets[7])) knightMoves.set(position + knightOffsets[7]);
            }
        } else {
            if(rank >= 2) {
                if(!currentBoard.blackPieces.get(position + knightOffsets[5])) knightMoves.set(position + knightOffsets[5]);
                if(!currentBoard.blackPieces.get(position + knightOffsets[6])) knightMoves.set(position + knightOffsets[6]);
            }

            if(rank <= 5) {
                if(!currentBoard.blackPieces.get(position + knightOffsets[1])) knightMoves.set(position + knightOffsets[1]);
                if(!currentBoard.blackPieces.get(position + knightOffsets[2])) knightMoves.set(position + knightOffsets[2]);
            }

            if(file >= 2) {
                if(!currentBoard.blackPieces.get(position + knightOffsets[0])) knightMoves.set(position + knightOffsets[0]);
                if(!currentBoard.blackPieces.get(position + knightOffsets[4])) knightMoves.set(position + knightOffsets[4]);
            }

            if(file <= 5) {
                if (!currentBoard.blackPieces.get(position + knightOffsets[3])) knightMoves.set(position + knightOffsets[3]);
                if (!currentBoard.blackPieces.get(position + knightOffsets[7])) knightMoves.set(position + knightOffsets[7]);
            }
        }
        return knightMoves;
    }

    public BitSet getBishopMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet bishopMoves = new BitSet(64);
        return bishopMoves;
    }

    public BitSet getRookMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet rookMoves = new BitSet(64);
        return rookMoves;
    }

    public BitSet getQueenMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet queenMoves = new BitSet(64);
        queenMoves.or(getBishopMoves(position, whiteToPlay, currentBoard));
        queenMoves.or(getRookMoves(position, whiteToPlay, currentBoard));
        return queenMoves;
    }

    public BitSet getKingMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet kingMoves = new BitSet(64);
        return kingMoves;
    }

    public boolean pieceIsPinned(int position, boolean whiteToPlay, BitBoard currentBoard) {
        return false;
    }

    public boolean isValidMove(int startSquare, int targetSquare, boolean whiteToPlay, BitBoard currentBoard) {
        //no piece at square
        if(!currentBoard.allPieces.get(startSquare)) return false;
        //no white piece at square for whites move
        if(!(whiteToPlay && currentBoard.whitePieces.get(startSquare))) return false;
        //no black piece at square for blacks move
        if(!(!whiteToPlay && currentBoard.blackPieces.get(startSquare))) return false;

        //check if piece is pinned to the king
        if(pieceIsPinned(startSquare, whiteToPlay, currentBoard)) return false;

        return true;
    }
}
//