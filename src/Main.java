import Board.Board;
import Board.Move;
import Board.Perft;
import GameManager.Game;

import java.io.*;
import java.util.*;

public class Main {
    //thinking about writing a python script to pull all positions at n depth from a given fen, as well
    //as the count of legal moves for each position. do the same for my program, and compare to pinpoint bugs.

    static Perft perft = new Perft();
    static Game game = new Game("r4k1r/Pp1p1ppp/1P3nbN/nPp5/BB2P3/q4N2/Pp1P2PP/R2Q1RK1 w - c6 0 3");
    static int diff = 0;
    static int count = 0;

    /*
        game.printBoard();
        System.out.println();
        game.printBoard(game.moveGenerator.getAttackedSquares(game.board, false));
        System.out.println("King in check: " + game.moveGenerator.kingInCheck(game.board, true));
     */

    public static void main(String[] args) {
        //game.printBoard();
        game.playGame();
        //getDiffBulk();
        //System.out.println(count);
        //getPerft(5);
        //System.out.println(count);
        System.out.println("Bad positions: " + diff);
    }

    public static void getDiffBulk() {
        List<String> pyFenMoveStrings = new LinkedList();
        List<String> diffList = new LinkedList();
        try {
            //txt file obtained from getFenFromPerft.py, passing the fen and depth required
            FileInputStream fis = new FileInputStream("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\fen_list.txt");
            Scanner scan = new Scanner(fis);

            while (scan.hasNextLine()) {
                String s = scan.nextLine();
                if(s.split("--").length != 2) continue;
                String fen = s.split("--")[0];
                String moves = s.split("--")[1];

                //System.out.println(fen);
                //System.out.println(moves);
                //System.out.println();

                getDiff(fen, moves);
                count++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDiff(String fen, String pyMoves) {
        String[] pythonOutput = pyMoves.split(", ");
        List<String> pyOut = Arrays.asList(pythonOutput);

        List<String> myOut = new LinkedList<>();
        game.board = new Board(fen);
        for(Move m : game.moveGenerator.getLegalMoves(game.board)) myOut.add(m.move);

        List<String> result = new LinkedList<>();

        Collections.sort(myOut);
        Collections.sort(pyOut);

        //System.out.println("My output: " + myOut);
        //System.out.println("Py output: " + pyOut);

        for(String s : pyOut) {
            if(!myOut.contains(s)) result.add(s);
        }

        for(String s : myOut) {
            if(!pyOut.contains(s)) result.add("!" + s);
        }

        if(result.size() != 0) {
            diff++;
            System.out.println(fen);
            System.out.println("My output: " + myOut);
            System.out.println("Py output: " + pyOut);
            System.out.println("Result: " + result + "\n");
        }
    }

    public static void getPerft(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.println("Perft at depth " + i + ": " + perft.perft(i, game.board));
        }
    }
}
