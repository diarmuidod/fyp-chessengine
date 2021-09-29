import Board.Move;
import GameManager.Game;

import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game chess = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        chess.printBoard();

        List<Move> moves = chess.moveGenerator.getLegalMoves(chess.board);

        System.out.println("Moves: " + moves.size());
        for(Move m : moves) {
            System.out.println(m);
        }
    }
}
