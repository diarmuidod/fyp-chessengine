package Board;

import java.util.BitSet;

public class BitBoard {
    final BitSet allPieces = new BitSet(64);

    final BitSet whitePieces = new BitSet(64);
    final BitSet blackPieces = new BitSet(64);

    final BitSet pawnPieces = new BitSet(64);
    final BitSet knightPieces = new BitSet(64);
    final BitSet bishopPieces = new BitSet(64);
    final BitSet rookPieces = new BitSet(64);
    final BitSet queenPieces = new BitSet(64);
    final BitSet kingPieces = new BitSet(64);

    final int enPassantSquare = -1;

    public BitBoard() {}

    public BitBoard(String FEN) {
        loadPositionFromFEN(FEN);
    }

    public void setupBoard() {
        loadPositionFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public void loadPositionFromFEN(String FEN) {
        String FENtoBoard = FEN.split(" ", 2)[0];
        int file = 0, rank = 7, type, colour;

        for (int i = 0; i < FENtoBoard.length(); i++) {
            char symbol = FENtoBoard.charAt(i);
            if(symbol == '/') {
                file = 0;
                rank--;
            } else {
                if(Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    allPieces.set((rank*8)+file);
                    if(Character.isUpperCase(symbol)) whitePieces.set((rank*8)+file);
                    if(!Character.isUpperCase(symbol)) blackPieces.set((rank*8)+file);

                    switch(Character.toUpperCase(symbol)) {
                        case 'P':
                            pawnPieces.set((rank*8)+file);
                            break;

                        case 'N':
                            knightPieces.set((rank*8)+file);
                            break;

                        case 'B':
                            bishopPieces.set((rank*8)+file);
                            break;

                        case 'R':
                            rookPieces.set((rank*8)+file);
                            break;

                        case 'Q':
                            queenPieces.set((rank*8)+file);
                            break;

                        case 'K':
                            kingPieces.set((rank*8)+file);
                            break;

                    }
                    file++;
                }
            }
        }
    }

    public void printBoard() {
        int mark = 64;

        for (int i = 0; i < 8; i++) {
            for (int j = mark - 8; j < mark; j++) {
                if(!allPieces.get(j)) {
                    System.out.print("[ ] ");
                } else {
                    boolean isWhite = whitePieces.get(j);
                    if(pawnPieces.get(j)) System.out.print(isWhite ? "[P] " : "[p] ");
                    if(knightPieces.get(j)) System.out.print(isWhite ? "[N] " : "[n] ");
                    if(bishopPieces.get(j)) System.out.print(isWhite ? "[B] " : "[b] ");
                    if(rookPieces.get(j)) System.out.print(isWhite ? "[R] " : "[r] ");
                    if(queenPieces.get(j)) System.out.print(isWhite ? "[Q] " : "[q] ");
                    if(kingPieces.get(j)) System.out.print(isWhite ? "[K] " : "[k] ");
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toBinaryString(allPieces) + '\n');
        sb.append(toBinaryString(whitePieces) + '\n');
        sb.append(toBinaryString(blackPieces) + '\n');
        sb.append(toBinaryString(pawnPieces) + '\n');
        sb.append(toBinaryString(knightPieces) + '\n');
        sb.append(toBinaryString(bishopPieces) + '\n');
        sb.append(toBinaryString(rookPieces) + '\n');
        sb.append(toBinaryString(queenPieces) + '\n');
        sb.append(toBinaryString(kingPieces) + '\n');
        return sb.toString();
    }
}
