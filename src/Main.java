import java.util.List;

import Board.Board;
import Board.Move;
import Board.Perft;
import GameManager.Game;

public class Main {
    public static void main(String[] args) {
        Perft perft = new Perft();
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        //Giouco Piano starting position
        //Game game = new Game("r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
        Game game = new Game(fen);
        game.playGame();

        //for (int i = 0; i < 6; i++) {
        //    System.out.println("Perft at depth " + i + ": " + perft.perft(i, game.board));
        //}
    }
}
