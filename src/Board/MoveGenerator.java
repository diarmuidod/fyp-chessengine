package Board;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class MoveGenerator {
    private static final int[] pawnOffsets = {8, 16, 7, 9}; //white offsets, values inverted for black
    private static final int[] knightOffsets = {6, 15, 17, 10, -6, -15, -17, -10};
    private static final int[] bishopOffsets = {7, 9, -7, -9};
    private static final int[] rookOffsets = {8, 1, -8, -1};
    private static final int[] kingOffsets = {7, 8, 9, 1, -7, -8, -9, -1};

    public BitSet getPawnMoves(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet pawnMoves = new BitSet(64);
        if (whiteToPlay) {
            //forward one
            if (!currentBoard.allPieces.get(position + pawnOffsets[0])) pawnMoves.set(position + pawnOffsets[0]);
            //forward two
            if (!currentBoard.allPieces.get(position + pawnOffsets[0]) && !currentBoard.allPieces.get(position + pawnOffsets[1])) {
                pawnMoves.set(position + pawnOffsets[1]);
            }
        } else {
            //forward one
            if (!currentBoard.allPieces.get(position - pawnOffsets[0])) pawnMoves.set(position - pawnOffsets[0]);
            //forward two
            if (!currentBoard.allPieces.get(position - pawnOffsets[0]) && !currentBoard.allPieces.get(position - pawnOffsets[1])) {
                pawnMoves.set(position - pawnOffsets[1]);
            }
        }
        //captures
        pawnMoves.or(getPawnAttacks(position, whiteToPlay, currentBoard));

        return pawnMoves;
    }

    public BitSet getPawnAttacks(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet pawnAttacks = new BitSet(64);

        if(!currentBoard.pawnPieces.get(position)) return pawnAttacks;
        boolean inRange;

        if(whiteToPlay) {
            inRange = (position + pawnOffsets[2] >= 0 && position + pawnOffsets[2] <= 63);
            if(inRange) {
                if (currentBoard.blackPieces.get(position + pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                    pawnAttacks.set(position + pawnOffsets[2]);
            }

            inRange = (position + pawnOffsets[3] >= 0 && position + pawnOffsets[3] <= 63);
            if(inRange) {
                if (currentBoard.blackPieces.get(position + pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                    pawnAttacks.set(position + pawnOffsets[3]);
            }
        } else {
            inRange = (position - pawnOffsets[2] >= 0 && position - pawnOffsets[2] <= 63);
            if(inRange) {
                if (currentBoard.whitePieces.get(position - pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                    pawnAttacks.set(position - pawnOffsets[2]);
            }

            inRange = (position - pawnOffsets[3] >= 0 && position - pawnOffsets[3] <= 63);
            if(inRange) {
                if (currentBoard.whitePieces.get(position - pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                    pawnAttacks.set(position - pawnOffsets[3]);
            }
        }

        return pawnAttacks;
    }

    public BitSet getKnightMoves(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet knightMoves = new BitSet(64);
        boolean inRange;

        int rank = (position / 8) + 1, file = position % 8;

        if (whiteToPlay) {
            if (rank >= 2) {
                inRange = position + knightOffsets[5] >= 0 && position + knightOffsets[5] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[5])) {
                        knightMoves.set(position + knightOffsets[5]);
                    }
                }

                inRange = position + knightOffsets[6] >= 0 && position + knightOffsets[6] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[6])) {
                        knightMoves.set(position + knightOffsets[6]);
                    }
                }
            }

            if (rank <= 5) {
                inRange = position + knightOffsets[1] >= 0 && position + knightOffsets[1] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[1])) {
                        knightMoves.set(position + knightOffsets[1]);
                    }
                }

                inRange = position + knightOffsets[2] >= 0 && position + knightOffsets[2] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[2])) {
                        knightMoves.set(position + knightOffsets[2]);
                    }
                }
            }

            if (file >= 2) {
                inRange = position + knightOffsets[0] >= 0 && position + knightOffsets[0] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[0])) {
                        knightMoves.set(position + knightOffsets[0]);
                    }
                }

                inRange = position + knightOffsets[4] >= 0 && position + knightOffsets[4] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[4])) {
                        knightMoves.set(position + knightOffsets[4]);
                    }
                }
            }

            if (file <= 5) {
                inRange = position + knightOffsets[3] >= 0 && position + knightOffsets[3] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[3])) {
                        knightMoves.set(position + knightOffsets[3]);
                    }
                }

                inRange = position + knightOffsets[7] >= 0 && position + knightOffsets[7] <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[7])) {
                        knightMoves.set(position + knightOffsets[7]);
                    }
                }
            }
        } else {
            if (rank >= 2) {
                inRange = position + knightOffsets[5] >= 0 && position + knightOffsets[5] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[5])) {
                        knightMoves.set(position + knightOffsets[5]);
                    }
                }

                inRange = position + knightOffsets[6] >= 0 && position + knightOffsets[6] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[6])) {
                        knightMoves.set(position + knightOffsets[6]);
                    }
                }
            }

            if (rank <= 5) {
                inRange = position + knightOffsets[1] >= 0 && position + knightOffsets[1] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[1])) {
                        knightMoves.set(position + knightOffsets[1]);
                    }
                }

                inRange = position + knightOffsets[2] >= 0 && position + knightOffsets[2] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[2])) {
                        knightMoves.set(position + knightOffsets[2]);
                    }
                }
            }

            if (file >= 2) {
                inRange = position + knightOffsets[0] >= 0 && position + knightOffsets[0] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[0])) {
                        knightMoves.set(position + knightOffsets[0]);
                    }
                }

                inRange = position + knightOffsets[4] >= 0 && position + knightOffsets[4] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[4])) {
                        knightMoves.set(position + knightOffsets[4]);
                    }
                }
            }

            if (file <= 5) {
                inRange = position + knightOffsets[3] >= 0 && position + knightOffsets[3] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[3])) {
                        knightMoves.set(position + knightOffsets[3]);
                    }
                }

                inRange = position + knightOffsets[7] >= 0 && position + knightOffsets[7] <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[7])) {
                        knightMoves.set(position + knightOffsets[7]);
                    }
                }
            }
        }
        return knightMoves;
    }

    public BitSet getBishopMoves(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet bishopMoves = new BitSet(64);

        int offsetPos = position;

        if (whiteToPlay) {
            for (int bishopOffset : bishopOffsets) {
                if(offsetPos >= 0 && offsetPos <= 63) continue;

                //keep iterating until you can't
                while (!currentBoard.whitePieces.get(offsetPos + bishopOffset)) {
                    offsetPos += bishopOffset;
                    if(offsetPos >= 0 && offsetPos <= 63) break;

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
                if(offsetPos >= 0 && offsetPos <= 63) continue;

                //keep iterating until you can't
                while (!currentBoard.blackPieces.get(offsetPos + bishopOffset)) {
                    offsetPos += bishopOffset;
                    if(offsetPos >= 0 && offsetPos <= 63) break;

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

    public BitSet getRookMoves(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet rookMoves = new BitSet(64);

        int offsetPos = position;

        if (whiteToPlay) {
            for (int rookOffset : rookOffsets) {
                if(offsetPos >= 0 && offsetPos <= 63) continue;

                //keep iterating until you can't
                while (!currentBoard.whitePieces.get(offsetPos + rookOffset)) {
                    offsetPos += rookOffset;
                    if(offsetPos >= 0 && offsetPos <= 63) break;

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
                if(offsetPos >= 0 && offsetPos <= 63) continue;

                //keep iterating until you can't
                while (!currentBoard.blackPieces.get(offsetPos + rookOffset)) {
                    offsetPos += rookOffset;
                    if(offsetPos >= 0 && offsetPos <= 63) break;

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

    public BitSet getQueenMoves(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet queenMoves = new BitSet(64);
        queenMoves.or(getBishopMoves(position, whiteToPlay, currentBoard));
        queenMoves.or(getRookMoves(position, whiteToPlay, currentBoard));
        return queenMoves;
    }

    public BitSet getKingAttacks(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet kingAttacks = new BitSet(64);
        boolean inRange;

        if (whiteToPlay) {
            for (int kingOffset : kingOffsets) {
                inRange = (position + kingOffset >= 0 && position + kingOffset <= 63);
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + kingOffset)) {
                        kingAttacks.set(position + kingOffset);
                    }
                }
            }
        } else {
            for (int kingOffset : kingOffsets) {
                inRange = (position + kingOffset >= 0 && position + kingOffset <= 63);
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + kingOffset)) {
                        kingAttacks.set(position + kingOffset);
                    }
                }
            }
        }

        return kingAttacks;
    }

    public BitSet getKingMoves(int position, boolean whiteToPlay, Board currentBoard) {
        BitSet kingMoves = new BitSet(64);
        BitSet attackedSquares = getAttackedSquares(!whiteToPlay, currentBoard);

        boolean inRange;

        if (whiteToPlay) {
            for (int kingOffset : kingOffsets) {
                inRange = position + kingOffset >= 0 && position + kingOffset <= 63;
                if(inRange) {
                    if (!currentBoard.whitePieces.get(position + kingOffset) && !attackedSquares.get(position + kingOffset)) {
                        kingMoves.set(position + kingOffset);
                    }
                }
            }

            if(currentBoard.whiteKingSide) {
                if(!attackedSquares.get(position) && !attackedSquares.get(position + 1) && !attackedSquares.get(position + 2)
                    && !currentBoard.allPieces.get(position + 1) && !currentBoard.allPieces.get(position + 2)) {
                        kingMoves.set(position + 2);
                }
            }

            if(currentBoard.whiteQueenSide) {
                if(!attackedSquares.get(position) && !attackedSquares.get(position - 1) && !attackedSquares.get(position - 2)
                    && !currentBoard.allPieces.get(position - 1) && !currentBoard.allPieces.get(position - 2) && !currentBoard.allPieces.get(position - 3)) {
                        kingMoves.set(position - 2);
                }
            }

        } else {
            for (int kingOffset : kingOffsets) {
                inRange = position + kingOffset >= 0 && position + kingOffset <= 63;
                if(inRange) {
                    if (!currentBoard.blackPieces.get(position + kingOffset) && !attackedSquares.get(position + kingOffset)) {
                        kingMoves.set(position + kingOffset);
                    }
                }
            }

            if(currentBoard.blackKingSide) {
                if(!attackedSquares.get(position) && !attackedSquares.get(position + 1) && !attackedSquares.get(position + 2)
                    && !currentBoard.allPieces.get(position + 1) && !currentBoard.allPieces.get(position + 2)) {
                        kingMoves.set(position + 2);
                }
            }

            if(currentBoard.blackQueenSide) {
                if(!attackedSquares.get(position) && !attackedSquares.get(position - 1) && !attackedSquares.get(position - 2)
                    && !currentBoard.allPieces.get(position - 1) && !currentBoard.allPieces.get(position - 2) && !currentBoard.allPieces.get(position - 3)) {
                        kingMoves.set(position - 2);
                }
            }
        }
        return kingMoves;
    }

    public BitSet getAttackedSquares(boolean whiteToPlay, Board currentBoard) {
        BitSet attackedSquares = new BitSet(64);

        for (int position = 0; position < 64; position++) {
            attackedSquares.or(getPawnAttacks(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getKnightMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getBishopMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getRookMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getQueenMoves(position, !whiteToPlay, currentBoard));
            attackedSquares.or(getKingAttacks(position, !whiteToPlay, currentBoard));
        }

        return attackedSquares;
    }

    public boolean pieceIsPinned(int position, int target, boolean whiteToPlay, Board currentBoard) {
        Board newPosition = makeMove(position, target, whiteToPlay, currentBoard);
        BitSet kingPosition = new BitSet(64);

        kingPosition.or(newPosition.kingPieces);

        if (whiteToPlay) {
            kingPosition.and(newPosition.whitePieces);
        } else {
            kingPosition.and(newPosition.blackPieces);
        }

        kingPosition.and(getAttackedSquares(whiteToPlay, newPosition));

        return kingPosition.nextSetBit(0) != -1;
    }

    //only called if move has been validated already, or should not be validated (for filtering pseudo legal moves)
    public Board makeMove(int startSquare, int targetSquare, boolean whiteToPlay, Board currentBoard) {
        Board newBoard = currentBoard.copy();

        newBoard.enPassantSquare = -1;
        newBoard.allPieces.clear(startSquare);

        if (whiteToPlay) {
            newBoard.whitePieces.clear(startSquare);
        } else {
            newBoard.blackPieces.clear(startSquare);
        }

        if(newBoard.pawnPieces.get(startSquare)) {
            newBoard.pawnPieces.clear(startSquare);
            newBoard.pawnPieces.set(targetSquare);
            if(Math.abs(startSquare - targetSquare) == 16) {
                if(whiteToPlay) {
                    newBoard.enPassantSquare = targetSquare - 8;
                } else {
                    newBoard.enPassantSquare = targetSquare + 8;
                }
            }
        } else if (newBoard.knightPieces.get(startSquare)) {
            newBoard.knightPieces.clear(startSquare);
            newBoard.knightPieces.set(targetSquare);
        } else if (newBoard.bishopPieces.get(startSquare)) {
            newBoard.bishopPieces.clear(startSquare);
            newBoard.bishopPieces.set(targetSquare);
        } else if (newBoard.rookPieces.get(startSquare)) {
            newBoard.rookPieces.clear(startSquare);
            newBoard.rookPieces.set(targetSquare);

            if(whiteToPlay) {
                if(startSquare == 7) {
                    newBoard.whiteKingSide = false;
                } else if(startSquare == 0) {
                    newBoard.whiteQueenSide = false;
                }
            } else {
                if(startSquare == 63) {
                    newBoard.blackKingSide = false;
                } else if(startSquare == 56) {
                    newBoard.blackQueenSide = false;
                }
            }
        } else if (newBoard.queenPieces.get(startSquare)) {
            newBoard.queenPieces.clear(startSquare);
            newBoard.queenPieces.set(targetSquare);
        } else if (newBoard.kingPieces.get(startSquare)) {
            newBoard.kingPieces.clear(startSquare);
            newBoard.kingPieces.set(targetSquare);

            if(whiteToPlay) {
                newBoard.whiteKingSide = false;
                newBoard.whiteQueenSide = false;
            } else {
                newBoard.blackKingSide = false;
                newBoard.blackQueenSide = false;
            }
        }

        if(targetSquare == 7) newBoard.whiteKingSide = false;
        if(targetSquare == 0) newBoard.whiteQueenSide = false;
        if(targetSquare == 63) newBoard.blackKingSide = false;
        if(targetSquare == 56) newBoard.blackQueenSide = false;

        newBoard.whiteToMove = !whiteToPlay;

        return newBoard;
    }

    public boolean isValidMove(int startSquare, int targetSquare, boolean whiteToPlay, Board currentBoard) {
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

    public long perft(int depth, Board board) {
        long moves = 0;
        LinkedList<Board> boards = new LinkedList<>();
        if(depth == 0) {
            return moves;
        }

        return moves += perft(depth - 1, board);
    }

    public List<Move> getLegalMoves(Board board) {
        List<Move> moves = new LinkedList<>();
        BitSet sideToPlay = board.whiteToMove ? board.whitePieces : board.blackPieces;

        for (int i = sideToPlay.nextSetBit(0); i >= 0; i = sideToPlay.nextSetBit(i+1)) {
            if(board.pawnPieces.get(i)) {
                BitSet pawnMoves = getPawnMoves(i, board.whiteToMove, board);
                for (int j = pawnMoves.nextSetBit(0); j >= 0; j = pawnMoves.nextSetBit(j+1)) {
                    if(j >= 56 && j <= 63) {
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_QUEEN));
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_ROOK));
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_BISHOP));
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_KNIGHT));
                    } else {
                        moves.add(new Move(i, j));
                    }
                }
            } else if(board.knightPieces.get(i)) {
                BitSet knightMoves = getKnightMoves(i, board.whiteToMove, board);
                for (int j = knightMoves.nextSetBit(0); j >= 0; j = knightMoves.nextSetBit(j+1)) {
                    if(isValidMove(i, j, board.whiteToMove, board)) moves.add(new Move(i, j));
                }
            } else if(board.bishopPieces.get(i)) {
                BitSet bishopMoves = getBishopMoves(i, board.whiteToMove, board);
                for (int j = bishopMoves.nextSetBit(0); j >= 0; j = bishopMoves.nextSetBit(j+1)) {
                    if(isValidMove(i, j, board.whiteToMove, board)) moves.add(new Move(i, j));
                }
            } else if(board.rookPieces.get(i)) {
                BitSet rookMoves = getRookMoves(i, board.whiteToMove, board);
                for (int j = rookMoves.nextSetBit(0); j >= 0; j = rookMoves.nextSetBit(j+1)) {
                    if(isValidMove(i, j, board.whiteToMove, board)) moves.add(new Move(i, j));
                }
            } else if(board.queenPieces.get(i)) {
                BitSet queenMoves = getQueenMoves(i, board.whiteToMove, board);
                for (int j = queenMoves.nextSetBit(0); j >= 0; j = queenMoves.nextSetBit(j+1)) {
                    if(isValidMove(i, j, board.whiteToMove, board)) moves.add(new Move(i, j));
                }
            } else if(board.kingPieces.get(i)) {
                BitSet kingMoves = getKingMoves(i, board.whiteToMove, board);
                for (int j = kingMoves.nextSetBit(0); j >= 0; j = kingMoves.nextSetBit(j+1)) {
                    if(isValidMove(i, j, board.whiteToMove, board)) moves.add(new Move(i, j));
                }
            }
        }

        return moves;
    }
}