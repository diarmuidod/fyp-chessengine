package Debug;

import Board.Board;
import Board.Move;
import GameManager.Game;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

public class MoveGenTest {
    Perft perft;
    Game game;
    int count = 0;
    int diff = 0;

    private MoveGenTest() {}

    public MoveGenTest(Game game) {
        this.game = game;
        this.perft = new Perft();
    }

    public void debug() {
        game.printBoard();
        System.out.println();
        game.printBoard(game.moveGenerator.getAttackedSquares(game.board, false));
        System.out.println("King in check: " + game.moveGenerator.kingInCheck(game.board, true));

        getDiffBulk();
        //System.out.println(count);
        getPerft(5);
        System.out.println("Bad positions: " + diff);
    }

    public void getDiffBulk() {
        List<String> pyFenMoveStrings = new LinkedList();
        List<String> diffList = new LinkedList();
        diff = 0;
        count = 0;

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

    public void getDiff(String fen, String pyMoves) {
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

    public void getPerft(int depth) {
        for (int i = 1; i <= depth; i++) {
            System.out.println("Perft at depth " + i + ": " + perft.perft(i, game.board));
        }
    }

    public void writePerft(int depth, Board board) {
        if(depth == 0) {
            writeOutput(game.saveGameToFEN(), game.moveGenerator.getLegalMoves(board));
        } else {
            for (Move m : game.moveGenerator.getLegalMoves(board)) {
                writePerft(depth - 1, game.board = game.moveGenerator.makeMove(m, board));
            }
        }
    }

    public void writeOutput(String fen, List<Move> moves) {
        try {
            StringBuilder out = new StringBuilder(fen + "--");

            for(int i = 0; i < moves.size(); i++) {
                if(i != moves.size() - 1) {
                    out.append(moves.get(i).move).append(", ");
                } else {
                    out.append(moves.get(i).move).append("\n");
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\my_fen_list.txt", true));
            writer.append(out.toString());

            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void comparePythonToJava() {
        String pyPath = "C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\fen_list.txt";
        String myPath = "C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\my_fen_list.txt";

        List<String> pythonOutput = new LinkedList<>();
        List<String> javaOutput = new LinkedList<>();

        List<String> pyOut = new LinkedList<>();
        List<String> myOut = new LinkedList<>();
        List<String> result = new LinkedList<>();

        String[] pythonMoves = {};
        String[] javaMoves = {};
        String fen = "";

        try {
            Scanner pythonReader = new Scanner(new FileInputStream(pyPath));
            Scanner javaReader = new Scanner(new FileInputStream(myPath));

            while(pythonReader.hasNextLine()) {
                pythonOutput.add(pythonReader.nextLine());
            }

            while(javaReader.hasNextLine()) {
                javaOutput.add(javaReader.nextLine());
            }

            Collections.sort(pythonOutput);
            Collections.sort(javaOutput);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String pythonMoveArray = "";
        String javaMoveArray;

        for(String pyLine : pythonOutput) {
            fen = pyLine.split("--")[0];
            if(!pyLine.equals("")) pythonMoveArray = pyLine.split("--")[1];

            pyOut = Arrays.asList(pythonMoves);
            myOut = Arrays.asList(javaMoves);
            result = new LinkedList<>();
        }

        for(String javaLine : javaOutput) {
            if(fen.equals(javaLine.split("--")[0])) {
                pythonMoves = pythonMoveArray.split(", ");
                javaMoveArray = javaLine.split("--")[1];
                javaMoves = javaMoveArray.split(", ");
            }
        }

        for(String s : pyOut) {
            if(!myOut.contains(s)) result.add(s);
        }

        for(String s : myOut) {
            if(!pyOut.contains(s)) result.add("!" + s);
        }

        if(result.size() != 0) System.out.println(fen + result);
    }
}
