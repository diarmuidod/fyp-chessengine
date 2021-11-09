import Board.Move;
import Board.Perft;
import GameManager.Game;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {
    static Perft perft = new Perft();
    static Game game = new Game("7K/2p1pP1R/q1pB2Q1/P3P3/1rB1p1PR/6pP/1k1b3P/2n1rb2 w - - 0 1");

    public static void main(String[] args) {
        //reason for current bug - -7 is bottom left offset, and is skipped, but here is needed.
        //solution - check termination by file/rank adjacency instead?
        //game.playGame();
        //getDiff();
        //getPerft(5);
    }

    public static void getDiff() {
        String pyMoves = "Kg8, Kg7, Rg7, R7h6, R7h5, Qg8, Qg7, Qh6, Qf6, Qe6, Qh5, Qg5, Qf5, Qxe4, Bxe7, Bxc7, Bc5, Bxb4, R4h6, R4h5, Be6, Bxa6, Bd5, Bb5, Bd3, Bb3, Be2, Ba2, Bxf1, hxg3, f8=Q, f8=R, f8=B, f8=N, e6, g5";
        String[] pythonOutput = pyMoves.split(", ");
        List<String> pyOut = Arrays.asList(pythonOutput);

        List<String> myOut = new LinkedList<>();
        for(Move m : game.moveGenerator.getLegalMoves(game.board)) myOut.add(m.move);

        List<String> result = new LinkedList<>();

        game.printBoard();
        System.out.println();

        Collections.sort(myOut);
        Collections.sort(pyOut);

        System.out.println("My output: " + myOut);
        System.out.println("Py output: " + pyOut);

        for(String s : pyOut) {
            if(!myOut.contains(s)) result.add(s);
        }

        for(String s : myOut) {
            if(!pyOut.contains(s)) result.add("!" + s);
        }

        System.out.println("\nThe Result: " + result + "\n\n");
    }

    public static void getPerft(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.println("Perft at depth " + i + ": " + perft.perft(i, game.board));
        }
    }
}
