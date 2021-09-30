import Board.Move;
import GameManager.Game;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //known bugs - moves not being filtered for pins and checks properly

        Game chess = new Game("5k2/8/3r4/8/3B4/8/3K4/8 w - - 0 1");
        chess.printBoard();

        System.out.println(chess.moveGenerator.getLegalMoves(chess.board));
        System.out.println("King in check: " + chess.moveGenerator.kingInCheck(chess.board, chess.board.whiteToMove));

        for(Move m : chess.moveGenerator.getLegalMoves(chess.board)) {
            System.out.println(m + ", " + chess.moveGenerator.perft(2, chess.moveGenerator.makeMove(m.startSquare, m.targetSquare, chess.board)));
        }

        System.out.println("\nMoves: " + chess.moveGenerator.perft(3, chess.board));
    }
}
