import Board.Move;
import Board.Perft;
import GameManager.Game;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {
    //thinking about writing a python script to pull all positions at n depth from a given fen, as well
    //as the count of legal moves for each position. do the same for my program, and compare to pinpoint bugs.

    static Perft perft = new Perft();
    static Game game = new Game();

    public static void main(String[] args) {
        game.playGame();
        //getDiff();
        //getPerft(5);
    }

    public static void getDiff() {
        String pyMoves = "Bg7, Rxc7, Rxd6, Rxb6, Rc5, Rc4, Rxc3, Bxb7, Bb5+, Bc4, Bxd3, Kxd4, Rh5, Rh4, Rg3, Rf3, Rxh2, Nxf4, Nxd4, Neg3, Nxc3+, Nxg1, Nc1, Qxd3, Qxc3, Qxc2+, Qe1, Qd1, Qc1, Nfg3, Nxh2, exd7, axb6, exf4, exd4, h7, f7, e7, g5, b5, e4";
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

        System.out.println("\nThe Result: " + result + "\n");
    }

    public static void getPerft(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.println("Perft at depth " + i + ": " + perft.perft(i, game.board));
        }
    }
}
