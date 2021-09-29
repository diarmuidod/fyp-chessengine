import Board.Move;
import GameManager.Game;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game chess = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        chess.printBoard();

        for (int i = 0; i < 5; i++) {
            System.out.println("Perft at depth " + i + ": " + chess.moveGenerator.perft(i, chess.board));
        }

        /*
        for (Move move : chess.moveGenerator.getLegalMoves(chess.board)) {
            System.out.println(move + ": " + chess.moveGenerator.perft(4, chess.moveGenerator.makeMove(move.startSquare, move.targetSquare, chess.board)));
        }
         */
    }
}
