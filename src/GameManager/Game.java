package GameManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;

public class Game {
    public Board board;
    public MoveGenerator moveGenerator;
    public List<Move> movesPlayed;
    public Scanner input;
    public String currentMove;

    private static final String REGEX = "(?:(?:O-O(?:-O)?)|(?:[KQNBR](?:[a-h1-8]?x?[a-h][1-8])|(?:(?:[a]x)?[b][2-7])|(?:(?:[b]x)?[ac][2-7])|(?:(?:[c]x)?[bd][2-7])|(?:(?:[d]x)?[ce][2-7])|(?:(?:[e]x)?[df][2-7])|(?:(?:[f]x)?[eg][2-7])|(?:(?:[g]x)?[fh][2-7]))|(?:(?:(?:[h]x)?[g][2-7])|(?:(?:[a]x)?[b][18])|(?:(?:[b]x)?[ac][18])|(?:(?:[c]x)?[bd][18])|(?:(?:[d]x)?[ce][18])|(?:(?:[e]x)?[df][18])|(?:(?:[f]x)?[eg][18])|(?:(?:[g]x)?[fh][18])|(?:(?:[h]x)?[g][18]))(?:=[QNBR]))[+#]?";

    public Game() {
        board = new Board();
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
    }

    public Game(String FEN) {
        board = new Board(FEN);
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
    }

    public void playGame() {

    }

    public String saveGameToFEN() {
        return null;
    }

    public void printBoard() {
        board.printBoard();
    }
}
