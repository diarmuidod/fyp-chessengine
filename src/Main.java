import Board.Move;
import GameManager.Game;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game chess = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        List<Move> moves = chess.moveGenerator.getLegalMoves(chess.board);

        for(Move move : moves) {
            System.out.println(chess.moveFromIndex(move.startSquare) + " -> " + chess.moveFromIndex(move.targetSquare));
        }

        System.out.println("Move Count: " + moves.size());
    }
}
