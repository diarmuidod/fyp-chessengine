package GameManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import Board.BoardState;
import Board.Move;

public class Game {
    BoardState board;
    List<Move> movesPlayed;
    Scanner input;
    String currentMove;

    private static final String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public Game() {
        board = new BoardState(startFEN);
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
    }

    public Game(String FEN) {
        board = new BoardState(FEN);
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
    }

    public void playGame() {

    }
}
