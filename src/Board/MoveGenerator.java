package Board;

import java.util.BitSet;

public class MoveGenerator {
    private static final int[] pawnOffsets = {8, 16, 7, 9}; //white offsets, values inverted for black
    private static final int[] knightOffsets = {6, 15, 17, 10, -6, -15, -17, -10};
    private static final int[] bishopOffsets = {7, 9, -7, -9};
    private static final int[] rookOffsets = {8, 1, -8, -1};
    private static final int[] kingOffsets = {7, 8, 9, 1, 2, -7, -8, -9, -1, -2};

    public BitSet getPawnMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet pawnMoves = new BitSet(64);
        if (whiteToPlay) {
            //forward one
            if (!currentBoard.allPieces.get(position + pawnOffsets[0])) pawnMoves.set(position + pawnOffsets[0]);
            //forward two
            if (!currentBoard.allPieces.get(position + pawnOffsets[0]) && !currentBoard.allPieces.get(position + pawnOffsets[1])) {
                pawnMoves.set(position + pawnOffsets[1]);
            }

            //capture left and right, including en passant
            //beware an edge case involving a horizontal pin making en passant illegal.
            if (currentBoard.blackPieces.get(position + pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                pawnMoves.set(position + pawnOffsets[2]);
            if (currentBoard.blackPieces.get(position + pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                pawnMoves.set(position + pawnOffsets[3]);

            //promotions

        } else {
            //forward one
            if (!currentBoard.allPieces.get(position - pawnOffsets[0])) pawnMoves.set(position - pawnOffsets[0]);
            //forward two
            if (!currentBoard.allPieces.get(position - pawnOffsets[0]) && !currentBoard.allPieces.get(position - pawnOffsets[1])) {
                pawnMoves.set(position - pawnOffsets[1]);
            }

            //capture left and right, including en passant
            if (currentBoard.blackPieces.get(position - pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                pawnMoves.set(position - pawnOffsets[2]);
            if (currentBoard.blackPieces.get(position - pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                pawnMoves.set(position - pawnOffsets[3]);
        }
        return pawnMoves;
    }

    public BitSet getKnightMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet knightMoves = new BitSet(64);

        int rank = position / 8, file = position % 8;

        if (whiteToPlay) {
            if (rank >= 2) {
                if (!currentBoard.whitePieces.get(position + knightOffsets[5]))
                    knightMoves.set(position + knightOffsets[5]);
                if (!currentBoard.whitePieces.get(position + knightOffsets[6]))
                    knightMoves.set(position + knightOffsets[6]);
            }

            if (rank <= 5) {
                if (!currentBoard.whitePieces.get(position + knightOffsets[1]))
                    knightMoves.set(position + knightOffsets[1]);
                if (!currentBoard.whitePieces.get(position + knightOffsets[2]))
                    knightMoves.set(position + knightOffsets[2]);
            }

            if (file >= 2) {
                if (!currentBoard.whitePieces.get(position + knightOffsets[0]))
                    knightMoves.set(position + knightOffsets[0]);
                if (!currentBoard.whitePieces.get(position + knightOffsets[4]))
                    knightMoves.set(position + knightOffsets[4]);
            }

            if (file <= 5) {
                if (!currentBoard.whitePieces.get(position + knightOffsets[3]))
                    knightMoves.set(position + knightOffsets[3]);
                if (!currentBoard.whitePieces.get(position + knightOffsets[7]))
                    knightMoves.set(position + knightOffsets[7]);
            }
        } else {
            if (rank >= 2) {
                if (!currentBoard.blackPieces.get(position + knightOffsets[5]))
                    knightMoves.set(position + knightOffsets[5]);
                if (!currentBoard.blackPieces.get(position + knightOffsets[6]))
                    knightMoves.set(position + knightOffsets[6]);
            }

            if (rank <= 5) {
                if (!currentBoard.blackPieces.get(position + knightOffsets[1]))
                    knightMoves.set(position + knightOffsets[1]);
                if (!currentBoard.blackPieces.get(position + knightOffsets[2]))
                    knightMoves.set(position + knightOffsets[2]);
            }

            if (file >= 2) {
                if (!currentBoard.blackPieces.get(position + knightOffsets[0]))
                    knightMoves.set(position + knightOffsets[0]);
                if (!currentBoard.blackPieces.get(position + knightOffsets[4]))
                    knightMoves.set(position + knightOffsets[4]);
            }

            if (file <= 5) {
                if (!currentBoard.blackPieces.get(position + knightOffsets[3]))
                    knightMoves.set(position + knightOffsets[3]);
                if (!currentBoard.blackPieces.get(position + knightOffsets[7]))
                    knightMoves.set(position + knightOffsets[7]);
            }
        }
        return knightMoves;
    }

    public BitSet getBishopMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet bishopMoves = new BitSet(64);

        int offsetPos = position;

        if (whiteToPlay) {
            for (int bishopOffset : bishopOffsets) {
                //keep iterating until you can't
                while (!currentBoard.whitePieces.get(offsetPos + bishopOffset)) {
                    offsetPos += bishopOffset;
                    bishopMoves.set(offsetPos);
                    //stop after a capture
                    if (currentBoard.blackPieces.get(offsetPos + bishopOffset)) {
                        offsetPos = position;
                        break;
                    }
                }
            }
        } else {
            for (int bishopOffset : bishopOffsets) {
                //keep iterating until you can't
                while (!currentBoard.blackPieces.get(offsetPos + bishopOffset)) {
                    offsetPos += bishopOffset;
                    bishopMoves.set(offsetPos);
                    //stop after a capture
                    if (currentBoard.whitePieces.get(offsetPos + bishopOffset)) {
                        offsetPos = position;
                        break;
                    }
                }
            }
        }

        return bishopMoves;
    }

    public BitSet getRookMoves(int position, boolean whiteToPlay, BitBoard currentBoard) {
        BitSet rookMoves = new BitSet(64);

        int offsetPos = position;

        if (whiteToPlay) {
            for (int rookOffset : rookOffsets) {
                //keep iterating until you can't
                while (!currentBoard.whitePieces.get(offsetPos + rookOffset)) {
                    offsetPos += rookOffset;
                    rookMoves.set(offsetPos);
                    //stop after a capture
                    if (currentBoard.blackPieces.get(offsetPos + rookOffset)) {
                        offsetPos = position;
                        break;
                    }
                }
            }
        } else {
            for (int rookOffset : rookOffsets) {
                //keep iterating until you can't
                while (!currentBoard.blackPieces.get(offsetPos + rookOffset)) {
                    offsetPos += rookOffset;
                    rookMoves.set(offsetPos);
                    //stop after a capture
                    if (currentBoard.whitePieces.get(offsetPos + rookOffset)) {
                        offsetPos = position;
                        break;
                    }
                }

            }
        }

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
        BitSet attackedSquares = getAttackedSquares(whiteToPlay, currentBoard);

        if (whiteToPlay) {
            for (int kingOffset : kingOffsets) {
                if (!currentBoard.whitePieces.get(position + kingOffset) && !attackedSquares.get(position + kingOffset)) {
                    kingMoves.set(position + kingOffset);
                }
            }
        } else {
            for (int kingOffset : kingOffsets) {
                if (!currentBoard.blackPieces.get(position + kingOffset) && !attackedSquares.get(position + kingOffset)) {
                    kingMoves.set(position + kingOffset);
                }
            }
        }
        return kingMoves;
    }

    public BitSet getAttackedSquares(boolean whiteToPlay, BitBoard currentBoard) {
        BitSet attackedSquares = new BitSet(64);

        for (int position = 0; position < 64; position++) {
            //Pawns accounted for separately, only piece with moves that can't be a capture
            if (currentBoard.pawnPieces.get(position)) {
                if (!whiteToPlay) {
                    if (currentBoard.blackPieces.get(position + pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                        attackedSquares.set(position + pawnOffsets[2]);
                    if (currentBoard.blackPieces.get(position + pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                        attackedSquares.set(position + pawnOffsets[3]);
                } else {
                    if (currentBoard.blackPieces.get(position - pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                        attackedSquares.set(position - pawnOffsets[2]);
                    if (currentBoard.blackPieces.get(position - pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                        attackedSquares.set(position - pawnOffsets[3]);
                }
            }

            attackedSquares.or(getKnightMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getBishopMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getRookMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getQueenMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getKingMoves(position, !whiteToPlay, currentBoard));
        }

        return attackedSquares;
    }

    public boolean pieceIsPinned(int position, int target, boolean whiteToPlay, BitBoard currentBoard) {
        BitBoard newPosition = makeMove(position, target, whiteToPlay, currentBoard);
        BitSet kingPosition = new BitSet(64);

        kingPosition.or(newPosition.kingPieces);

        if (whiteToPlay) {
            kingPosition.and(newPosition.whitePieces);
        } else {
            kingPosition.and(newPosition.blackPieces);
        }

        BitSet attackedSquares = new BitSet(64);
        attackedSquares.or(getAttackedSquares(whiteToPlay, newPosition));
        kingPosition.and(attackedSquares);

        return kingPosition.nextSetBit(0) != -1;
    }

    //only called if move has been validated already, or should not be validated (for filtering pseudo legal moves)
    public BitBoard makeMove(int startSquare, int targetSquare, boolean whiteToPlay, BitBoard currentBoard) {
        BitBoard newBoard = currentBoard.clone();
        newBoard.allPieces.clear(startSquare);

        if (whiteToPlay) {
            newBoard.whitePieces.clear(startSquare);
        } else {
            newBoard.blackPieces.clear(startSquare);
        }

        if (newBoard.knightPieces.get(startSquare)) {
            newBoard.knightPieces.clear(startSquare);
            newBoard.knightPieces.set(targetSquare);
        } else if (newBoard.bishopPieces.get(startSquare)) {
            newBoard.bishopPieces.clear(startSquare);
            newBoard.bishopPieces.set(targetSquare);
        } else if (newBoard.rookPieces.get(startSquare)) {
            newBoard.rookPieces.clear(startSquare);
            newBoard.rookPieces.set(targetSquare);
        } else if (newBoard.queenPieces.get(startSquare)) {
            newBoard.queenPieces.clear(startSquare);
            newBoard.queenPieces.set(targetSquare);
        } else if (newBoard.kingPieces.get(startSquare)) {
            newBoard.kingPieces.clear(startSquare);
            newBoard.kingPieces.set(targetSquare);
        }

        return newBoard;
    }

    public boolean isValidMove(int startSquare, int targetSquare, boolean whiteToPlay, BitBoard currentBoard) {
        //ensure there's a piece to move to begin with
        if (whiteToPlay && !currentBoard.whitePieces.get(startSquare)) return false;
        if (!whiteToPlay && !currentBoard.blackPieces.get(startSquare)) return false;

        //Only disallows moves violating pins, not every move involving a pin
        //i.e rook pawn king horizontally disallows pawn move, but is allowed arranged vertically
        if (pieceIsPinned(startSquare, targetSquare, whiteToPlay, currentBoard)) {
            return false;
        }

        //determine whether the move is among the legal moves for that piece
        if (currentBoard.pawnPieces.get(startSquare))
            return getPawnMoves(startSquare, whiteToPlay, currentBoard).get(targetSquare);
        if (currentBoard.knightPieces.get(startSquare))
            return getKnightMoves(startSquare, whiteToPlay, currentBoard).get(targetSquare);
        if (currentBoard.bishopPieces.get(startSquare))
            return getBishopMoves(startSquare, whiteToPlay, currentBoard).get(targetSquare);
        if (currentBoard.rookPieces.get(startSquare))
            return getRookMoves(startSquare, whiteToPlay, currentBoard).get(targetSquare);
        if (currentBoard.queenPieces.get(startSquare))
            return getQueenMoves(startSquare, whiteToPlay, currentBoard).get(targetSquare);
        if (currentBoard.kingPieces.get(startSquare))
            return getKingMoves(startSquare, whiteToPlay, currentBoard).get(targetSquare);

        return false;
    }
}