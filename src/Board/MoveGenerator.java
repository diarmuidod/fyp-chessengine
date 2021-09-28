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

    public BitSet getPawnMoves(int position, Board currentBoard) {
        BitSet pawnMoves = new BitSet(64);
        if (currentBoard.whiteToMove) {
            //forward one
            if (!currentBoard.allPieces.get(position + pawnOffsets[0])) pawnMoves.set(position + pawnOffsets[0]);
            //forward two
            if ((position / 8 == 1) || (position / 8) == 6) {
                if (!currentBoard.allPieces.get(position + pawnOffsets[0]) && !currentBoard.allPieces.get(position + pawnOffsets[1])) {
                    pawnMoves.set(position + pawnOffsets[1]);
                }
            }
        } else {
            //forward one
            if (!currentBoard.allPieces.get(position - pawnOffsets[0])) pawnMoves.set(position - pawnOffsets[0]);
            //forward two
            if ((position / 8 == 1) || (position / 8) == 6) {
                if (!currentBoard.allPieces.get(position - pawnOffsets[0]) && !currentBoard.allPieces.get(position - pawnOffsets[1])) {
                    pawnMoves.set(position - pawnOffsets[1]);
                }
            }
        }
        //captures
        pawnMoves.or(getPawnAttacks(position, currentBoard));

        return pawnMoves;
    }

    public BitSet getPawnAttacks(int position, Board currentBoard) {
        BitSet pawnAttacks = new BitSet(64);

        if (!currentBoard.pawnPieces.get(position)) return pawnAttacks;
        boolean inRange;

        if (currentBoard.whiteToMove) {
            inRange = (position + pawnOffsets[2] >= 0 && position + pawnOffsets[2] <= 63);
            if (inRange) {
                if (currentBoard.blackPieces.get(position + pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                    pawnAttacks.set(position + pawnOffsets[2]);
            }

            inRange = (position + pawnOffsets[3] >= 0 && position + pawnOffsets[3] <= 63);
            if (inRange) {
                if (currentBoard.blackPieces.get(position + pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                    pawnAttacks.set(position + pawnOffsets[3]);
            }
        } else {
            inRange = (position - pawnOffsets[2] >= 0 && position - pawnOffsets[2] <= 63);
            if (inRange) {
                if (currentBoard.whitePieces.get(position - pawnOffsets[2]) || currentBoard.enPassantSquare == pawnOffsets[2])
                    pawnAttacks.set(position - pawnOffsets[2]);
            }

            inRange = (position - pawnOffsets[3] >= 0 && position - pawnOffsets[3] <= 63);
            if (inRange) {
                if (currentBoard.whitePieces.get(position - pawnOffsets[3]) || currentBoard.enPassantSquare == pawnOffsets[3])
                    pawnAttacks.set(position - pawnOffsets[3]);
            }
        }

        return pawnAttacks;
    }

    public BitSet getKnightMoves(int position, Board currentBoard) {
        BitSet knightMoves = new BitSet(64);
        boolean inRange;

        int rank = (position / 8) + 1, file = position % 8;

        if (currentBoard.whiteToMove) {
            if (rank >= 2) {
                inRange = position + knightOffsets[5] >= 0 && position + knightOffsets[5] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[5])) {
                        knightMoves.set(position + knightOffsets[5]);
                    }
                }

                inRange = position + knightOffsets[6] >= 0 && position + knightOffsets[6] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[6])) {
                        knightMoves.set(position + knightOffsets[6]);
                    }
                }
            }

            if (rank <= 5) {
                inRange = position + knightOffsets[1] >= 0 && position + knightOffsets[1] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[1])) {
                        knightMoves.set(position + knightOffsets[1]);
                    }
                }

                inRange = position + knightOffsets[2] >= 0 && position + knightOffsets[2] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[2])) {
                        knightMoves.set(position + knightOffsets[2]);
                    }
                }
            }

            if (file >= 2) {
                inRange = position + knightOffsets[0] >= 0 && position + knightOffsets[0] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[0])) {
                        knightMoves.set(position + knightOffsets[0]);
                    }
                }

                inRange = position + knightOffsets[4] >= 0 && position + knightOffsets[4] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[4])) {
                        knightMoves.set(position + knightOffsets[4]);
                    }
                }
            }

            if (file <= 5) {
                inRange = position + knightOffsets[3] >= 0 && position + knightOffsets[3] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[3])) {
                        knightMoves.set(position + knightOffsets[3]);
                    }
                }

                inRange = position + knightOffsets[7] >= 0 && position + knightOffsets[7] <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + knightOffsets[7])) {
                        knightMoves.set(position + knightOffsets[7]);
                    }
                }
            }
        } else {
            if (rank >= 2) {
                inRange = position + knightOffsets[5] >= 0 && position + knightOffsets[5] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[5])) {
                        knightMoves.set(position + knightOffsets[5]);
                    }
                }

                inRange = position + knightOffsets[6] >= 0 && position + knightOffsets[6] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[6])) {
                        knightMoves.set(position + knightOffsets[6]);
                    }
                }
            }

            if (rank <= 5) {
                inRange = position + knightOffsets[1] >= 0 && position + knightOffsets[1] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[1])) {
                        knightMoves.set(position + knightOffsets[1]);
                    }
                }

                inRange = position + knightOffsets[2] >= 0 && position + knightOffsets[2] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[2])) {
                        knightMoves.set(position + knightOffsets[2]);
                    }
                }
            }

            if (file >= 2) {
                inRange = position + knightOffsets[0] >= 0 && position + knightOffsets[0] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[0])) {
                        knightMoves.set(position + knightOffsets[0]);
                    }
                }

                inRange = position + knightOffsets[4] >= 0 && position + knightOffsets[4] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[4])) {
                        knightMoves.set(position + knightOffsets[4]);
                    }
                }
            }

            if (file <= 5) {
                inRange = position + knightOffsets[3] >= 0 && position + knightOffsets[3] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[3])) {
                        knightMoves.set(position + knightOffsets[3]);
                    }
                }

                inRange = position + knightOffsets[7] >= 0 && position + knightOffsets[7] <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + knightOffsets[7])) {
                        knightMoves.set(position + knightOffsets[7]);
                    }
                }
            }
        }
        return knightMoves;
    }

    public BitSet getKnightAttacks(int position, Board currentBoard) {
        BitSet knightAttacks = new BitSet(64);
        boolean inRange;

        int rank = (position / 8) + 1, file = position % 8;

        if (currentBoard.whiteToMove) {
            if (rank >= 2) {
                inRange = position + knightOffsets[5] >= 0 && position + knightOffsets[5] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[5]);
                }

                inRange = position + knightOffsets[6] >= 0 && position + knightOffsets[6] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[6]);
                }
            }

            if (rank <= 5) {
                inRange = position + knightOffsets[1] >= 0 && position + knightOffsets[1] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[1]);
                }

                inRange = position + knightOffsets[2] >= 0 && position + knightOffsets[2] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[2]);
                }
            }

            if (file >= 2) {
                inRange = position + knightOffsets[0] >= 0 && position + knightOffsets[0] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[0]);
                }

                inRange = position + knightOffsets[4] >= 0 && position + knightOffsets[4] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[4]);
                }
            }

            if (file <= 5) {
                inRange = position + knightOffsets[3] >= 0 && position + knightOffsets[3] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[3]);
                }

                inRange = position + knightOffsets[7] >= 0 && position + knightOffsets[7] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[7]);
                }
            }
        } else {
            if (rank >= 2) {
                inRange = position + knightOffsets[5] >= 0 && position + knightOffsets[5] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[5]);
                }

                inRange = position + knightOffsets[6] >= 0 && position + knightOffsets[6] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[6]);
                }
            }

            if (rank <= 5) {
                inRange = position + knightOffsets[1] >= 0 && position + knightOffsets[1] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[1]);
                }

                inRange = position + knightOffsets[2] >= 0 && position + knightOffsets[2] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[2]);
                }
            }

            if (file >= 2) {
                inRange = position + knightOffsets[0] >= 0 && position + knightOffsets[0] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[0]);
                }

                inRange = position + knightOffsets[4] >= 0 && position + knightOffsets[4] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[4]);
                }
            }

            if (file <= 5) {
                inRange = position + knightOffsets[3] >= 0 && position + knightOffsets[3] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[3]);
                }

                inRange = position + knightOffsets[7] >= 0 && position + knightOffsets[7] <= 63;
                if (inRange) {
                    knightAttacks.set(position + knightOffsets[7]);
                }
            }
        }
        return knightAttacks;
    }

    public BitSet getBishopMoves(int position, Board currentBoard) {
        BitSet bishopMoves = new BitSet(64);
        int offsetPos;

        BitSet sideToMove = currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        BitSet sideToWait = !currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        for(int bishopOffset : bishopOffsets) {
            offsetPos = position;
            offsetPos += bishopOffset;

            while (true) {
                //started on edge, no legal moves
                if (slidingMoveEdgeCase(bishopOffset, position)) break;

                //out of bounds
                if (offsetPos > 63 || offsetPos < 0) break;

                //about to wrap around, disallow
                if (slidingMoveEdgeCase(bishopOffset, offsetPos) && !sideToMove.get(offsetPos)) {
                    bishopMoves.set(offsetPos);
                    break;
                }

                //can't capture own pieces
                if (sideToMove.get(offsetPos)) {
                    break;
                }

                if(sideToWait.get(offsetPos)) {
                    bishopMoves.set(offsetPos);
                    break;
                }

                bishopMoves.set(offsetPos);
                offsetPos += bishopOffset;
            }
        }

        return bishopMoves;
    }

    public BitSet getRookMoves(int position, Board currentBoard) {
        BitSet rookMoves = new BitSet(64);
        int offsetPos;

        BitSet sideToMove = currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        BitSet sideToWait = !currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        for(int rookOffset : rookOffsets) {
            offsetPos = position;
            offsetPos += rookOffset;

            while (true) {
                //started on edge, no legal moves
                if (slidingMoveEdgeCase(rookOffset, position)) break;

                //out of bounds
                if (offsetPos > 63 || offsetPos < 0) break;

                //about to wrap around, disallow
                if (slidingMoveEdgeCase(rookOffset, offsetPos) && !sideToMove.get(offsetPos)) {
                    rookMoves.set(offsetPos);
                    break;
                }

                //can't capture own pieces
                if (sideToMove.get(offsetPos)) {
                    break;
                }

                if(sideToWait.get(offsetPos)) {
                    rookMoves.set(offsetPos);
                    break;
                }

                rookMoves.set(offsetPos);
                offsetPos += rookOffset;
            }
        }

        return rookMoves;
    }

    private boolean slidingMoveEdgeCase(int offset, int position) {
        if(offset == bishopOffsets[0]) {
            return (position % 8 == 0 || position >= 56);
        }

        if(offset == bishopOffsets[1]) {
            return (position % 8 == 7 || position >= 56);
        }

        if(offset == bishopOffsets[2]) {
            return (position % 8 == 0 || position < 8);
        }

        if(offset == bishopOffsets[3]) {
            return (position % 8 == 7 || position < 8);
        }

        if(offset == rookOffsets[0]) {
            return (position >= 56);
        }

        if(offset == rookOffsets[1]) {
            return (position % 8 == 7);
        }

        if(offset == rookOffsets[2]) {
            return (position < 8);
        }

        if(offset == rookOffsets[3]) {
            return (position % 8 == 0);
        }

        return false;
    }

    public BitSet getQueenMoves(int position, Board currentBoard) {
        BitSet queenMoves = new BitSet(64);
        queenMoves.or(getBishopMoves(position, currentBoard));
        queenMoves.or(getRookMoves(position, currentBoard));
        return queenMoves;
    }

    public BitSet getKingAttacks(int position, Board currentBoard) {
        BitSet kingAttacks = new BitSet(64);
        boolean inRange;

        if (currentBoard.whiteToMove) {
            for (int kingOffset : kingOffsets) {
                inRange = (position + kingOffset >= 0 && position + kingOffset <= 63);
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + kingOffset)) {
                        kingAttacks.set(position + kingOffset);
                    }
                }
            }
        } else {
            for (int kingOffset : kingOffsets) {
                inRange = (position + kingOffset >= 0 && position + kingOffset <= 63);
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + kingOffset)) {
                        kingAttacks.set(position + kingOffset);
                    }
                }
            }
        }

        return kingAttacks;
    }

    public BitSet getKingMoves(int position, Board currentBoard) {
        BitSet kingMoves = new BitSet(64);
        BitSet attackedSquares = getAttackedSquares(currentBoard);

        boolean inRange;

        if (currentBoard.whiteToMove) {
            for (int kingOffset : kingOffsets) {
                inRange = position + kingOffset >= 0 && position + kingOffset <= 63;
                if (inRange) {
                    if (!currentBoard.whitePieces.get(position + kingOffset) && !attackedSquares.get(position + kingOffset)) {
                        kingMoves.set(position + kingOffset);
                    }
                }
            }

            if (currentBoard.whiteKingSide) {
                if (!attackedSquares.get(position) && !attackedSquares.get(position + 1) && !attackedSquares.get(position + 2)
                        && !currentBoard.allPieces.get(position + 1) && !currentBoard.allPieces.get(position + 2)) {
                    kingMoves.set(position + 2);
                }
            }

            if (currentBoard.whiteQueenSide) {
                if (!attackedSquares.get(position) && !attackedSquares.get(position - 1) && !attackedSquares.get(position - 2)
                        && !currentBoard.allPieces.get(position - 1) && !currentBoard.allPieces.get(position - 2) && !currentBoard.allPieces.get(position - 3)) {
                    kingMoves.set(position - 2);
                }
            }

        } else {
            for (int kingOffset : kingOffsets) {
                inRange = position + kingOffset >= 0 && position + kingOffset <= 63;
                if (inRange) {
                    if (!currentBoard.blackPieces.get(position + kingOffset) && !attackedSquares.get(position + kingOffset)) {
                        kingMoves.set(position + kingOffset);
                    }
                }
            }

            if (currentBoard.blackKingSide) {
                if (!attackedSquares.get(position) && !attackedSquares.get(position + 1) && !attackedSquares.get(position + 2)
                        && !currentBoard.allPieces.get(position + 1) && !currentBoard.allPieces.get(position + 2)) {
                    kingMoves.set(position + 2);
                }
            }

            if (currentBoard.blackQueenSide) {
                if (!attackedSquares.get(position) && !attackedSquares.get(position - 1) && !attackedSquares.get(position - 2)
                        && !currentBoard.allPieces.get(position - 1) && !currentBoard.allPieces.get(position - 2) && !currentBoard.allPieces.get(position - 3)) {
                    kingMoves.set(position - 2);
                }
            }
        }
        return kingMoves;
    }

    public BitSet getAttackedSquares(Board currentBoard) {
        BitSet attackedSquares = new BitSet(64);
        BitSet sideToAttack = currentBoard.whiteToMove ? currentBoard.blackPieces : currentBoard.whitePieces;

        for(int i = sideToAttack.nextSetBit(0); i >= 0; i = sideToAttack.nextSetBit(i + 1)) {
            if(currentBoard.pawnPieces.get(i)) {
                attackedSquares.or(getPawnAttacks(i, currentBoard));
            }
        }


        return attackedSquares;
    }

    private boolean pieceIsPinned(int position, int target, Board currentBoard) {
        Board newPosition = makeMove(position, target, currentBoard);
        BitSet kingPosition = new BitSet(64);

        kingPosition.or(newPosition.kingPieces);


        if (currentBoard.whiteToMove) {
            kingPosition.and(newPosition.whitePieces);
        } else {
            kingPosition.and(newPosition.blackPieces);
        }

        kingPosition.and(getAttackedSquares(newPosition));

        return kingPosition.nextSetBit(0) == -1;
    }

    private boolean kingInCheck(Board currentBoard) {
        BitSet sideToPlay = currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces ;
        sideToPlay.and(currentBoard.kingPieces);
        sideToPlay.and(getAttackedSquares(currentBoard));

        return sideToPlay.nextSetBit(0) != -1;
    }

    //only called if move has been validated already, or should not be validated (for filtering pseudo legal moves)
    public Board makeMove(int startSquare, int targetSquare, Board currentBoard) {
        Board newBoard = currentBoard.copy();

        newBoard.enPassantSquare = -1;
        newBoard.allPieces.clear(startSquare);

        if (currentBoard.whiteToMove) {
            newBoard.whitePieces.clear(startSquare);
        } else {
            newBoard.blackPieces.clear(startSquare);
        }

        if (newBoard.pawnPieces.get(startSquare)) {
            newBoard.pawnPieces.clear(startSquare);
            newBoard.pawnPieces.set(targetSquare);
            if (Math.abs(startSquare - targetSquare) == 16) {
                if (currentBoard.whiteToMove) {
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

            if (currentBoard.whiteToMove) {
                if (startSquare == 7) {
                    newBoard.whiteKingSide = false;
                } else if (startSquare == 0) {
                    newBoard.whiteQueenSide = false;
                }
            } else {
                if (startSquare == 63) {
                    newBoard.blackKingSide = false;
                } else if (startSquare == 56) {
                    newBoard.blackQueenSide = false;
                }
            }
        } else if (newBoard.queenPieces.get(startSquare)) {
            newBoard.queenPieces.clear(startSquare);
            newBoard.queenPieces.set(targetSquare);
        } else if (newBoard.kingPieces.get(startSquare)) {
            newBoard.kingPieces.clear(startSquare);
            newBoard.kingPieces.set(targetSquare);

            if (currentBoard.whiteToMove) {
                newBoard.whiteKingSide = false;
                newBoard.whiteQueenSide = false;
            } else {
                newBoard.blackKingSide = false;
                newBoard.blackQueenSide = false;
            }
        }

        if (targetSquare == 7) newBoard.whiteKingSide = false;
        if (targetSquare == 0) newBoard.whiteQueenSide = false;
        if (targetSquare == 63) newBoard.blackKingSide = false;
        if (targetSquare == 56) newBoard.blackQueenSide = false;

        newBoard.whiteToMove = !currentBoard.whiteToMove;

        return newBoard;
    }

    public boolean isValidMove(int startSquare, int targetSquare, Board currentBoard) {
        //ensure there's a piece to move to begin with
        if (currentBoard.whiteToMove && !currentBoard.whitePieces.get(startSquare)) return false;
        if (!currentBoard.whiteToMove && !currentBoard.blackPieces.get(startSquare)) return false;

        //Only disallows moves violating pins, not every move involving a pin
        //i.e rook pawn king horizontally disallows pawn move, but is allowed arranged vertically
        if (pieceIsPinned(startSquare, targetSquare, currentBoard)) {
            return false;
        }

        //determine if king is in check, and if so, will this move resolve the check
        if (kingInCheck(currentBoard) && kingInCheck(makeMove(startSquare, targetSquare, currentBoard))) {
            return false;
        }

        //determine whether the move is among the legal moves for that piece
        if (currentBoard.pawnPieces.get(startSquare))
            return getPawnMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.knightPieces.get(startSquare))
            return getKnightMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.bishopPieces.get(startSquare))
            return getBishopMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.rookPieces.get(startSquare))
            return getRookMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.queenPieces.get(startSquare))
            return getQueenMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.kingPieces.get(startSquare))
            return getKingMoves(startSquare, currentBoard).get(targetSquare);

        return false;
    }

    public List<Move> getLegalMoves(Board board) {
        List<Move> moves = new LinkedList<>();
        BitSet sideToPlay = board.whiteToMove ? board.whitePieces : board.blackPieces;

        for (int i = sideToPlay.nextSetBit(0); i >= 0; i = sideToPlay.nextSetBit(i + 1)) {
            if (board.pawnPieces.get(i)) {
                BitSet pawnMoves = getPawnMoves(i, board);
                for (int j = pawnMoves.nextSetBit(0); j >= 0; j = pawnMoves.nextSetBit(j + 1)) {
                    if (!isValidMove(i, j, board)) continue;
                    if (j >= 56 && j <= 63) {
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_QUEEN));
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_ROOK));
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_BISHOP));
                        moves.add(new Move(i, j, Move.Flag.PROMOTE_KNIGHT));
                    } else {
                        moves.add(new Move(i, j));
                    }
                }
            } else if (board.knightPieces.get(i)) {
                BitSet knightMoves = getKnightMoves(i, board);
                for (int j = knightMoves.nextSetBit(0); j >= 0; j = knightMoves.nextSetBit(j + 1)) {
                    if (isValidMove(i, j, board)) moves.add(new Move(i, j));
                }
            } else if (board.bishopPieces.get(i)) {
                BitSet bishopMoves = getBishopMoves(i, board);
                for (int j = bishopMoves.nextSetBit(0); j >= 0; j = bishopMoves.nextSetBit(j + 1)) {
                    if (isValidMove(i, j, board)) moves.add(new Move(i, j));
                }
            } else if (board.rookPieces.get(i)) {
                BitSet rookMoves = getRookMoves(i, board);
                for (int j = rookMoves.nextSetBit(0); j >= 0; j = rookMoves.nextSetBit(j + 1)) {
                    if (isValidMove(i, j, board)) moves.add(new Move(i, j));
                }
            } else if (board.queenPieces.get(i)) {
                BitSet queenMoves = getQueenMoves(i, board);
                for (int j = queenMoves.nextSetBit(0); j >= 0; j = queenMoves.nextSetBit(j + 1)) {
                    if (isValidMove(i, j, board)) moves.add(new Move(i, j));
                }
            } else if (board.kingPieces.get(i)) {
                BitSet kingMoves = getKingMoves(i, board);
                for (int j = kingMoves.nextSetBit(0); j >= 0; j = kingMoves.nextSetBit(j + 1)) {
                    if (isValidMove(i, j, board)) moves.add(new Move(i, j));
                }
            }
        }

        return moves;
    }

    public long perft(int depth, Board board) {
        long moves = 0;

        if (depth == 0) {
            return 1;
        }

        for (Move m : getLegalMoves(board)) {
            moves += perft(depth - 1, makeMove(m.startSquare, m.targetSquare, board));
        }

        return moves;
    }
}