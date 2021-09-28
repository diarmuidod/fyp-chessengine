import Board.Move;
import GameManager.Game;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game chess = new Game("rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR w KQkq - 0 1");

        List<Move> moves = chess.moveGenerator.getLegalMoves(chess.board);

        chess.printBoard();

        //chess.printBoard(chess.moveGenerator.getAttackedSquares(chess.board));

        for(int i = 0; i <= 5; i++) {
            //System.out.println("Positions at depth " + i + ": " + chess.moveGenerator.perft(i, chess.board));
        }


        System.out.println("Moves: " + moves.size());
        for(Move m : moves) {
            System.out.println(m);
        }
    }
}
