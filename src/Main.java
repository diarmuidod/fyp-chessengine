import Board.Move;
import GameManager.Game;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game chess = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        List<Move> moves = chess.moveGenerator.getLegalMoves(chess.board);

        for(int i = 0; i <= 5; i++) {
            System.out.println("Positions at depth " + i + ": " + chess.moveGenerator.perft(i, chess.board));
        }
    }
}
