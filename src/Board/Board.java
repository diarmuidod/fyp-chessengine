package Board;

import java.util.BitSet;

public class Board {
    final BitSet allPieces = new BitSet(64);

    final BitSet whitePieces = new BitSet(64);
    final BitSet blackPieces = new BitSet(64);

    final BitSet pawnPieces = new BitSet(64);
    final BitSet knightPieces = new BitSet(64);
    final BitSet bishopPieces = new BitSet(64);
    final BitSet rookPieces = new BitSet(64);
    final BitSet queenPieces = new BitSet(64);
    final BitSet kingPieces = new BitSet(64);

    public boolean whiteToMove;

    boolean whiteKingSide;
    boolean whiteQueenSide;
    boolean blackKingSide;
    boolean blackQueenSide;

    int enPassantSquare = -1;

    public Board() {
        loadPositionFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Board(String FEN) {
        loadPositionFromFEN(FEN);
    }

    public void loadPositionFromFEN(String FEN) {
        String FENtoBoard = FEN.split(" ", 6)[0];
        String sideToMove = FEN.split(" ", 6)[1];
        String castlingRights = FEN.split(" ", 6)[2];
        String enPassant = FEN.split(" ", 6)[3];

        int file = 0, rank = 7;

        whiteToMove = sideToMove.contains("w");

        whiteKingSide = castlingRights.contains("K");
        whiteQueenSide = castlingRights.contains("Q");
        blackKingSide = castlingRights.contains("k");
        blackQueenSide = castlingRights.contains("q");

        enPassantSquare = enPassant.equals("-") ? -1 : (enPassant.charAt(0) - 97) + ((Character.getNumericValue(enPassant.charAt(1)) - 1) * 8);

        for (int i = 0; i < FENtoBoard.length(); i++) {
            char symbol = FENtoBoard.charAt(i);
            if (symbol == '/') {
                file = 0;
                rank--;
            } else {
                if (Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    allPieces.set((rank * 8) + file);

                    if (Character.isUpperCase(symbol)) {
                        whitePieces.set((rank * 8) + file);
                    } else {
                        blackPieces.set((rank * 8) + file);
                    }

                    switch (Character.toUpperCase(symbol)) {
                        case 'P':
                            pawnPieces.set((rank * 8) + file);
                            break;
                        case 'N':
                            knightPieces.set((rank * 8) + file);
                            break;
                        case 'B':
                            bishopPieces.set((rank * 8) + file);
                            break;
                        case 'R':
                            rookPieces.set((rank * 8) + file);
                            break;
                        case 'Q':
                            queenPieces.set((rank * 8) + file);
                            break;
                        case 'K':
                            kingPieces.set((rank * 8) + file);
                            break;
                    }
                    file++;
                }
            }
        }
    }

    public Board copy() {
        Board newBoard = new Board();
        newBoard.allPieces.clear();
        newBoard.allPieces.or(this.allPieces);

        newBoard.whitePieces.clear();
        newBoard.whitePieces.or(this.whitePieces);
        
        newBoard.blackPieces.clear();
        newBoard.blackPieces.or(this.blackPieces);

        newBoard.pawnPieces.clear();
        newBoard.pawnPieces.or(this.pawnPieces);

        newBoard.knightPieces.clear();
        newBoard.knightPieces.or(this.knightPieces);

        newBoard.bishopPieces.clear();
        newBoard.bishopPieces.or(this.bishopPieces);

        newBoard.rookPieces.clear();
        newBoard.rookPieces.or(this.rookPieces);

        newBoard.queenPieces.clear();
        newBoard.queenPieces.or(this.queenPieces);

        newBoard.kingPieces.clear();
        newBoard.kingPieces.or(this.kingPieces);

        newBoard.whiteToMove = whiteToMove;

        newBoard.whiteKingSide = whiteKingSide;
        newBoard.whiteQueenSide = whiteQueenSide;
        newBoard.blackKingSide = blackKingSide;
        newBoard.blackQueenSide = blackQueenSide;

        newBoard.enPassantSquare = this.enPassantSquare;

        return newBoard;
    }

    public void printBoard() {
        int mark = 64;

        for (int i = 0; i < 8; i++) {
            for (int j = mark - 8; j < mark; j++) {
                if (!allPieces.get(j)) {
                    System.out.print("[ ] ");
                } else {
                    boolean isWhite = whitePieces.get(j);
                    if (pawnPieces.get(j)) System.out.print(isWhite ? "[P] " : "[p] ");
                    if (knightPieces.get(j)) System.out.print(isWhite ? "[N] " : "[n] ");
                    if (bishopPieces.get(j)) System.out.print(isWhite ? "[B] " : "[b] ");
                    if (rookPieces.get(j)) System.out.print(isWhite ? "[R] " : "[r] ");
                    if (queenPieces.get(j)) System.out.print(isWhite ? "[Q] " : "[q] ");
                    if (kingPieces.get(j)) System.out.print(isWhite ? "[K] " : "[k] ");
                }
            }
            mark -= 8;
            System.out.println();
        }
    }

    public void printBoard(BitSet bitset) {
        int mark = 64;

        for (int i = 0; i < 8; i++) {
            for (int j = mark - 8; j < mark; j++) {
                if (!bitset.get(j)) {
                    System.out.print("[ ] ");
                } else {
                    System.out.print("[*] ");
                }
            }
            mark -= 8;
            System.out.println();
        }
    }

    public static String toBinaryString(BitSet bitSet) {
        int bits = 64;
        StringBuilder sb = new StringBuilder(bitSet.length());
        for (int i = bits - 1; i >= 0; i--) {
            sb.append(bitSet.get(i) ? '1' : '0');
            if (i % 8 == 0) sb.append(' ');
        }
        return sb.toString();
    }
/*
    public String toString() {
        return toBinaryString(allPieces) + '\n' +
                toBinaryString(whitePieces) + '\n' +
                toBinaryString(blackPieces) + '\n' +
                toBinaryString(pawnPieces) + '\n' +
                toBinaryString(knightPieces) + '\n' +
                toBinaryString(bishopPieces) + '\n' +
                toBinaryString(rookPieces) + '\n' +
                toBinaryString(queenPieces) + '\n' +
                toBinaryString(kingPieces) + '\n';
    }
 */
}
