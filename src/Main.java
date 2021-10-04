import GameManager.Game;

public class Main {
    public static void main(String[] args) {
        //known bugs - moves not being filtered for pins and checks properly

        Game chess = new Game("1rbqk2r/1ppp1pp1/p1n2n1p/2b1p3/2B1P3/2NPBN2/PPPQ1PPP/R3K2R w KQk - 0 1");
        chess.printBoard();
        System.out.println();
        //chess.printBoard(chess.moveGenerator.getPawnAttacks(48, chess.board, false));

        System.out.println(chess.getLegalMoves() + "\nMoves: " + chess.getLegalMoves().size());
        System.out.println("King in Check: " + chess.moveGenerator.kingInCheck(chess.board, chess.board.whiteToMove));

        for (int i = 0; i < 5; i++) {
            System.out.println("Moves at depth " + i + ": " + chess.moveGenerator.perft(i, chess.board));
        }
    }
}
