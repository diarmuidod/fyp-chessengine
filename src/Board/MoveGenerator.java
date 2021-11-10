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
        int sideToMove = currentBoard.whiteToMove ? 1 : -1;
        BitSet sideToWait = !currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        boolean inRange;


        //forward one
        inRange = (position + (pawnOffsets[0] * sideToMove) >= 0 && position + (pawnOffsets[0] * sideToMove) <= 63);
        if(inRange) {
            if (!currentBoard.allPieces.get(position + (pawnOffsets[0] * sideToMove)))
                pawnMoves.set(position + (pawnOffsets[0] * sideToMove));
        }

        //forward two
        inRange = (position + (pawnOffsets[1] * sideToMove) >= 0 && position + (pawnOffsets[1] * sideToMove) <= 63);
        if(inRange) {
            if (position / 8 == 1 || position / 8 == 6) {
                if (!currentBoard.allPieces.get(position + (pawnOffsets[0] * sideToMove))
                        && !currentBoard.allPieces.get(position + (pawnOffsets[1] * sideToMove))) {
                    pawnMoves.set(position + (pawnOffsets[1] * sideToMove));
                }
            }
        }

        //captures
        inRange = (position + (pawnOffsets[2] * sideToMove) >= 0 && position + (pawnOffsets[2] * sideToMove) <= 63);
        if (inRange && (Math.abs(getFile(position) - getFile(position + (pawnOffsets[2] * sideToMove))) == 1)) {
            if (sideToWait.get(position + (pawnOffsets[2] * sideToMove)) || currentBoard.enPassantSquare == position + (pawnOffsets[2] * sideToMove)) {
                pawnMoves.set(position + (pawnOffsets[2] * sideToMove));
            }
        }

        inRange = (position + (pawnOffsets[3] * sideToMove) >= 0 && position + (pawnOffsets[3] * sideToMove) <= 63);
        if (inRange && (Math.abs(getFile(position) - getFile(position + (pawnOffsets[3] * sideToMove))) == 1)) {
            if (sideToWait.get(position + (pawnOffsets[3] * sideToMove)) || currentBoard.enPassantSquare == position + (pawnOffsets[3] * sideToMove)) {
                pawnMoves.set(position + (pawnOffsets[3] * sideToMove));
            }
        }

        return pawnMoves;
    }

    public BitSet getPawnAttacks(int position, Board currentBoard, boolean whiteToMove) {
        BitSet pawnAttacks = new BitSet(64);
        int sideToMove = whiteToMove ? 1 : -1;

        if (!currentBoard.pawnPieces.get(position)) return pawnAttacks;
        boolean inRange;

        inRange = (position + (pawnOffsets[2] * sideToMove) >= 0 && position + (pawnOffsets[2] * sideToMove) <= 63);
        if (inRange && (Math.abs(getFile(position) - getFile(position + (pawnOffsets[2] * sideToMove))) == 1)) {
            pawnAttacks.set(position + (pawnOffsets[2] * sideToMove));
        }

        inRange = (position + (pawnOffsets[3] * sideToMove) >= 0 && position + (pawnOffsets[3] * sideToMove) <= 63);
        if (inRange && (Math.abs(getFile(position) - getFile(position + (pawnOffsets[3] * sideToMove))) == 1)) {
            pawnAttacks.set(position + (pawnOffsets[3] * sideToMove));
        }

        return pawnAttacks;
    }

    public BitSet getKnightMoves(int position, Board currentBoard) {
        BitSet knightMoves = new BitSet(64);
        BitSet sideToMove = currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        int rank = (position / 8) + 1, file = (position % 8) + 1;

        for (int knightOffset : knightOffsets) {
            if (position + knightOffset < 0 || position + knightOffset > 63) continue;

            if(((position + knightOffset) / 8) + 1 == rank) continue;
            if(((position + knightOffset) % 8) + 1 == file) continue;

            if (rank == 1 && knightOffset < 0) continue;
            if (rank == 2 && knightOffset <= -15) continue;
            if (rank == 8 && knightOffset > 0) continue;
            if (rank == 7 && knightOffset >= 15) continue;

            if (file == 1 && (knightOffset == 15 || knightOffset == 6 || knightOffset == -10 || knightOffset == -17)) continue;
            if (file == 2 && (knightOffset == 6 || knightOffset == -10)) continue;
            if (file == 8 && (knightOffset == -15 || knightOffset == -6 || knightOffset == 10 || knightOffset == 17)) continue;
            if (file == 7 && (knightOffset == -6 || knightOffset == 10)) continue;

            if (!sideToMove.get(position + knightOffset)) {
                knightMoves.set(position + knightOffset);
            }
        }
        return knightMoves;
    }

    public BitSet getKnightAttacks(int position) {
        BitSet knightAttacks = new BitSet(64);
        int rank = (position / 8) + 1, file = position % 8;

        for (int knightOffset : knightOffsets) {
            if (position + knightOffset < 0 || position + knightOffset > 63) continue;

            if(Math.abs(getFile(position) - getFile(position + knightOffset)) == 2 && Math.abs(getRank(position) - getRank(position + knightOffset)) == 1) {
                knightAttacks.set(position + knightOffset);
            } else if(Math.abs(getFile(position) - getFile(position + knightOffset)) == 1 && Math.abs(getRank(position) - getRank(position + knightOffset)) == 2) {
                knightAttacks.set(position + knightOffset);
            }
        }
        return knightAttacks;
    }

    public BitSet getBishopMoves(int position, Board currentBoard) {
        BitSet bishopMoves = new BitSet(64);
        int offsetPos;

        BitSet sideToMove = currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        BitSet sideToWait = !currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        for (int bishopOffset : bishopOffsets) {
            offsetPos = position;
            offsetPos += bishopOffset;

            while (true) {
                //out of bounds
                if (offsetPos > 63 || offsetPos < 0) break;

                //non-adjacent indices, both are always adjacent
                if(Math.abs(getRank(offsetPos - bishopOffset) - getRank(offsetPos)) != 1) {
                    //System.out.println("Rank - " + (Math.abs(getRank(position) - getRank(offsetPos))) + ", File - " + (Math.abs(getFile(position) - getFile(offsetPos))));
                    break;
                }

                if(Math.abs(getFile(offsetPos - bishopOffset) - getFile(offsetPos)) != 1) {
                    break;
                }

                //can't capture own pieces
                if (sideToMove.get(offsetPos)) {
                    break;
                }

                //capture
                if (sideToWait.get(offsetPos)) {
                    bishopMoves.set(offsetPos);
                    break;
                }

                //normal move
                bishopMoves.set(offsetPos);
                offsetPos += bishopOffset;
            }
        }

        return bishopMoves;
    }

    public BitSet getBishopAttacks(int position, Board currentBoard, boolean whiteToMove) {
        BitSet bishopAttacks = new BitSet(64);
        int offsetPos;

        BitSet sideToMove = whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        BitSet sideToWait = !whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        for (int bishopOffset : bishopOffsets) {
            offsetPos = position;
            offsetPos += bishopOffset;

            while (true) {
                //out of bounds
                if (offsetPos > 63 || offsetPos < 0) break;

                //non-adjacent indices, both are always adjacent
                if(Math.abs(getRank(offsetPos - bishopOffset) - getRank(offsetPos)) != 1) {
                    //System.out.println("Rank - " + (Math.abs(getRank(position) - getRank(offsetPos))) + ", File - " + (Math.abs(getFile(position) - getFile(offsetPos))));
                    break;
                }

                if(Math.abs(getFile(offsetPos - bishopOffset) - getFile(offsetPos)) != 1) {
                    break;
                }

                if (sideToMove.get(offsetPos)) {
                    bishopAttacks.set(offsetPos);
                    break;
                }

                if (sideToWait.get(offsetPos)) {
                    bishopAttacks.set(offsetPos);
                    break;
                }

                bishopAttacks.set(offsetPos);
                offsetPos += bishopOffset;
            }
        }

        return bishopAttacks;
    }

    public BitSet getRookMoves(int position, Board currentBoard) {
        BitSet rookMoves = new BitSet(64);
        int offsetPos;

        BitSet sideToMove = currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        BitSet sideToWait = !currentBoard.whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        for (int rookOffset : rookOffsets) {
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

                if (sideToWait.get(offsetPos)) {
                    rookMoves.set(offsetPos);
                    break;
                }

                rookMoves.set(offsetPos);
                offsetPos += rookOffset;
            }
        }

        return rookMoves;
    }

    public BitSet getRookAttacks(int position, Board currentBoard, boolean whiteToMove) {
        BitSet rookAttacks = new BitSet(64);
        int offsetPos;

        BitSet sideToMove = whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;
        BitSet sideToWait = !whiteToMove ? currentBoard.whitePieces : currentBoard.blackPieces;

        for (int rookOffset : rookOffsets) {
            offsetPos = position;
            offsetPos += rookOffset;

            while (true) {
                //started on edge, no legal moves
                if (slidingMoveEdgeCase(rookOffset, position)) break;

                //out of bounds
                if (offsetPos > 63 || offsetPos < 0) break;

                //about to wrap around, disallow
                if (slidingMoveEdgeCase(rookOffset, offsetPos) && !sideToMove.get(offsetPos)) {
                    rookAttacks.set(offsetPos);
                    break;
                }

                if (sideToMove.get(offsetPos)) {
                    rookAttacks.set(offsetPos);
                    break;
                }

                if (sideToWait.get(offsetPos)) {
                    rookAttacks.set(offsetPos);
                    break;
                }

                rookAttacks.set(offsetPos);
                offsetPos += rookOffset;
            }
        }

        return rookAttacks;
    }

    private boolean slidingMoveEdgeCase(int offset, int position) {
        if (offset == bishopOffsets[0]) {
            return (position % 8 == 0 || position >= 56);
        }

        if (offset == bishopOffsets[1]) {
            return (position % 8 == 7 || position >= 56);
        }

        if (offset == bishopOffsets[2]) {
            return (position % 8 == 0 || position % 8 == 7 || position < 8);
        }

        if (offset == bishopOffsets[3]) {
            return (position % 8 == 7 || position % 8 == 0 || position < 8);
        }

        if (offset == rookOffsets[0]) {
            return (position >= 56);
        }

        if (offset == rookOffsets[1]) {
            return (position % 8 == 7);
        }

        if (offset == rookOffsets[2]) {
            return (position < 8);
        }

        if (offset == rookOffsets[3]) {
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

    public BitSet getQueenAttacks(int position, Board currentBoard, boolean whiteToMove) {
        BitSet queenAttacks = new BitSet(64);

        queenAttacks.or(getBishopAttacks(position, currentBoard, whiteToMove));
        queenAttacks.or(getRookAttacks(position, currentBoard, whiteToMove));

        return queenAttacks;
    }

    public BitSet getKingMoves(int position, Board currentBoard) {
        BitSet kingMoves = new BitSet(64);
        BitSet attackedSquares = getAttackedSquares(currentBoard, !currentBoard.whiteToMove);

        boolean inRange;

        if (currentBoard.whiteToMove) {
            for (int kingOffset : kingOffsets) {
                if (position % 8 == 0 && (kingOffset == 7 || kingOffset == -1 || kingOffset == -9)) continue;
                if (position % 8 == 7 && (kingOffset == 9 || kingOffset == 1 || kingOffset == -7)) continue;

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
                if (position % 8 == 0 && (kingOffset == 7 || kingOffset == -1 || kingOffset == -9)) continue;
                if (position % 8 == 7 && (kingOffset == 9 || kingOffset == 1 || kingOffset == -7)) continue;

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

    public BitSet getKingAttacks(int position) {
        BitSet kingAttacks = new BitSet(64);
        boolean inRange;

        for (int kingOffset : kingOffsets) {
            if (position % 8 == 0 && (kingOffset == 7 || kingOffset == -1 || kingOffset == -9)) continue;
            if (position % 8 == 7 && (kingOffset == 9 || kingOffset == 1 || kingOffset == -7)) continue;
            inRange = (position + kingOffset >= 0 && position + kingOffset <= 63);
            if (inRange) {
                kingAttacks.set(position + kingOffset);
            }
        }

        return kingAttacks;
    }

    public boolean kingInCheck(Board currentBoard, boolean kingToCheck) {
        BitSet attackingSide = getAttackedSquares(currentBoard, !kingToCheck);
        BitSet defendingSide = kingToCheck ? (BitSet) currentBoard.whitePieces.clone() : (BitSet) currentBoard.blackPieces.clone();

        defendingSide.and(currentBoard.kingPieces);
        attackingSide.and(defendingSide);

        return (attackingSide.nextSetBit(0) != -1);
    }

    public BitSet getAttackedSquares(Board currentBoard, boolean whiteToPlay) {
        BitSet attackedSquares = new BitSet(64);
        BitSet sideToPlay = whiteToPlay ? currentBoard.whitePieces : currentBoard.blackPieces;

        for (int i = sideToPlay.nextSetBit(0); i >= 0; i = sideToPlay.nextSetBit(i + 1)) {
            if (currentBoard.pawnPieces.get(i)) {
                attackedSquares.or(getPawnAttacks(i, currentBoard, whiteToPlay));
            } else if (currentBoard.knightPieces.get(i)) {
                attackedSquares.or(getKnightAttacks(i));
            } else if (currentBoard.bishopPieces.get(i)) {
                attackedSquares.or(getBishopAttacks(i, currentBoard, whiteToPlay));
            } else if (currentBoard.rookPieces.get(i)) {
                attackedSquares.or(getRookAttacks(i, currentBoard, whiteToPlay));
            } else if (currentBoard.queenPieces.get(i)) {
                attackedSquares.or(getQueenAttacks(i, currentBoard, whiteToPlay));
            } else if (currentBoard.kingPieces.get(i)) {
                attackedSquares.or(getKingAttacks(i));
            }
        }

        return attackedSquares;
    }

    public Board makeMove(Move move, Board currentBoard) {
        return makeMove(move.startSquare, move.targetSquare, currentBoard, move.moveFlag);
    }

    public Board makeMove(int startSquare, int targetSquare, Board currentBoard, List<Move.Flag> flags) {
        Board board = currentBoard.copy();

        board.enPassantSquare = -1;

        board.allPieces.clear(startSquare);
        board.allPieces.set(targetSquare);

        board.whitePieces.clear(startSquare);
        board.whitePieces.clear(targetSquare);
        board.blackPieces.clear(startSquare);
        board.blackPieces.clear(targetSquare);

        if (board.whiteToMove) {
            board.whitePieces.set(targetSquare);
        } else {
            board.blackPieces.set(targetSquare);
        }

        board.pawnPieces.clear(targetSquare);
        board.knightPieces.clear(targetSquare);
        board.bishopPieces.clear(targetSquare);
        board.rookPieces.clear(targetSquare);
        board.queenPieces.clear(targetSquare);

        if (board.pawnPieces.get(startSquare)) {
            board.pawnPieces.clear(startSquare);

            if(flags != null) {
                if(flags.contains(Move.Flag.PROMOTE_QUEEN)) {
                    board.queenPieces.set(targetSquare);
                } else if(flags.contains(Move.Flag.PROMOTE_ROOK)) {
                    board.rookPieces.set(targetSquare);
                } else if(flags.contains(Move.Flag.PROMOTE_KNIGHT)) {
                    board.knightPieces.set(targetSquare);
                } else if(flags.contains(Move.Flag.PROMOTE_BISHOP)) {
                    board.bishopPieces.set(targetSquare);
                } else {
                    board.pawnPieces.set(targetSquare);
                }
            }

            if(targetSquare == currentBoard.enPassantSquare) {
                if(currentBoard.whiteToMove) {
                    board.allPieces.clear(targetSquare - 8);
                    board.pawnPieces.clear(targetSquare - 8);
                } else {
                    board.allPieces.clear(targetSquare + 8);
                    board.pawnPieces.clear(targetSquare + 8);
                }
            }

            if (Math.abs(startSquare - targetSquare) == 16) {
                if (currentBoard.whiteToMove) {
                    board.enPassantSquare = targetSquare - 8;
                } else {
                    board.enPassantSquare = targetSquare + 8;
                }
            }
        } else if (board.knightPieces.get(startSquare)) {
            board.knightPieces.clear(startSquare);
            board.knightPieces.set(targetSquare);
        } else if (board.bishopPieces.get(startSquare)) {
            board.bishopPieces.clear(startSquare);
            board.bishopPieces.set(targetSquare);
        } else if (board.rookPieces.get(startSquare)) {
            board.rookPieces.clear(startSquare);
            board.rookPieces.set(targetSquare);

            if (currentBoard.whiteToMove) {
                if (startSquare == 7) {
                    board.whiteKingSide = false;
                } else if (startSquare == 0) {
                    board.whiteQueenSide = false;
                }
            } else {
                if (startSquare == 63) {
                    board.blackKingSide = false;
                } else if (startSquare == 56) {
                    board.blackQueenSide = false;
                }
            }
        } else if (board.queenPieces.get(startSquare)) {
            board.queenPieces.clear(startSquare);
            board.queenPieces.set(targetSquare);
        } else if (board.kingPieces.get(startSquare)) {
            board.kingPieces.clear(startSquare);
            board.kingPieces.set(targetSquare);

            if (targetSquare - startSquare == 2) { //castle short
                board.rookPieces.clear(startSquare + 3);
                board.rookPieces.set(startSquare + 1);

                board.allPieces.clear(startSquare + 3);
                board.allPieces.set(startSquare + 1);

                if(board.whiteToMove) {
                    board.whitePieces.set(startSquare + 1);
                } else {
                    board.blackPieces.set(startSquare + 1);
                }
            }

            if (targetSquare - startSquare == -2) { //castle long
                board.rookPieces.clear(startSquare - 4);
                board.rookPieces.set(startSquare - 1);

                board.allPieces.clear(startSquare - 4);
                board.allPieces.set(startSquare - 1);

                if(board.whiteToMove) {
                    board.whitePieces.set(startSquare - 1);
                } else {
                    board.blackPieces.set(startSquare -
                            1);
                }
            }

            //king move disallows all castling
            if (currentBoard.whiteToMove) {
                board.whiteKingSide = false;
                board.whiteQueenSide = false;
            } else {
                board.blackKingSide = false;
                board.blackQueenSide = false;
            }
        }

        //associated rook has been captured
        if (targetSquare == 7) board.whiteKingSide = false;
        if (targetSquare == 0) board.whiteQueenSide = false;
        if (targetSquare == 63) board.blackKingSide = false;
        if (targetSquare == 56) board.blackQueenSide = false;

        //50 move rule checks
        if(currentBoard.allPieces.get(targetSquare) || currentBoard.pawnPieces.get(startSquare)) {
            board.fiftyMoveCount = 0;
        } else {
            board.fiftyMoveCount++;
        }

        board.whiteToMove = !board.whiteToMove;

        return board;
    }

    public boolean isValidMove(int startSquare, int targetSquare, Board currentBoard) {
        //ensure there's a piece to move to begin with
        if (currentBoard.whiteToMove && !currentBoard.whitePieces.get(startSquare)) return false;
        if (!currentBoard.whiteToMove && !currentBoard.blackPieces.get(startSquare)) return false;

        //determine if king will be in check after move
        if (kingInCheck(makeMove(startSquare, targetSquare, currentBoard, null), currentBoard.whiteToMove)) return false;

        //determine whether the move is among the legal moves for that piece
        if (currentBoard.pawnPieces.get(startSquare)) return getPawnMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.knightPieces.get(startSquare)) return getKnightMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.bishopPieces.get(startSquare)) return getBishopMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.rookPieces.get(startSquare)) return getRookMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.queenPieces.get(startSquare)) return getQueenMoves(startSquare, currentBoard).get(targetSquare);
        if (currentBoard.kingPieces.get(startSquare)) return getKingMoves(startSquare, currentBoard).get(targetSquare);

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
                    if (j >= 56 && j <= 63 || j <= 7) {
                        moves.add(new Move(i, j, board, this, Move.Flag.PROMOTE_QUEEN));
                        moves.add(new Move(i, j, board, this, Move.Flag.PROMOTE_ROOK));
                        moves.add(new Move(i, j, board, this, Move.Flag.PROMOTE_BISHOP));
                        moves.add(new Move(i, j, board, this, Move.Flag.PROMOTE_KNIGHT));
                    } else if(j == board.enPassantSquare){
                        moves.add(new Move(i, j, board, this, Move.Flag.EN_PASSANT));
                    } else {
                        moves.add(new Move(i, j, board, this));
                    }
                }
            } else if (board.knightPieces.get(i)) {
                BitSet knightMoves = getKnightMoves(i, board);
                for (int j = knightMoves.nextSetBit(0); j >= 0; j = knightMoves.nextSetBit(j + 1)) {
                    if (!isValidMove(i, j, board)) continue;

                    moves.add(new Move(i, j, board, this));
                }
            } else if (board.bishopPieces.get(i)) {
                BitSet bishopMoves = getBishopMoves(i, board);
                for (int j = bishopMoves.nextSetBit(0); j >= 0; j = bishopMoves.nextSetBit(j + 1)) {
                    if (!isValidMove(i, j, board)) continue;

                    moves.add(new Move(i, j, board, this));
                }
            } else if (board.rookPieces.get(i)) {
                BitSet rookMoves = getRookMoves(i, board);
                for (int j = rookMoves.nextSetBit(0); j >= 0; j = rookMoves.nextSetBit(j + 1)) {
                    if (!isValidMove(i, j, board)) continue;

                    moves.add(new Move(i, j, board, this));
                }
            } else if (board.queenPieces.get(i)) {
                BitSet queenMoves = getQueenMoves(i, board);
                for (int j = queenMoves.nextSetBit(0); j >= 0; j = queenMoves.nextSetBit(j + 1)) {
                    if (!isValidMove(i, j, board)) continue;

                    moves.add(new Move(i, j, board, this));
                }
            } else if (board.kingPieces.get(i)) {
                BitSet kingMoves = getKingMoves(i, board);
                for (int j = kingMoves.nextSetBit(0); j >= 0; j = kingMoves.nextSetBit(j + 1)) {
                    if (!isValidMove(i, j, board)) continue;

                    if (i - j == -2) {
                        moves.add(new Move(i, j, board, this, Move.Flag.CASTLE_SHORT));
                    } else if (i - j == 2) {
                        moves.add(new Move(i, j, board, this, Move.Flag.CASTLE_LONG));
                    } else {
                        moves.add(new Move(i, j, board, this));
                    }
                }
            }
        }

        return moves;
    }

    public char getFile(int index) {
        return (char) ((index % 8) + 97);
    }

    public char getRank(int index) {
        return (char) ((index / 8) + 49);
    }
}