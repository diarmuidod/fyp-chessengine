package Debug;

import Board.Board;
import Board.Move;
import GameManager.Game;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class MoveGenTest {
    public Perft perft;
    Game game;
    int count = 0;
    int diff = 0;

    private MoveGenTest() {
    }

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
        Map<String, List<String>> pythonMoves = new HashMap<>();
        Map<String, List<String>> javaMoves = new HashMap<>();
        diff = 0;
        count = 0;

        try {
            //txt file obtained from getFenFromPerft.py, passing the fen and depth required
            FileInputStream fis = new FileInputStream("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\fen_list.txt");
            Scanner scan = new Scanner(fis);

            while (scan.hasNextLine()) {
                String s = scan.nextLine();
                if (s.split("--").length != 2) continue;
                String fen = s.split("--")[0];
                String moves = s.split("--")[1];

                pythonMoves.put(fen, Arrays.asList(moves.split(", ")));
                //System.out.println(fen);
                //System.out.println(moves);
                //System.out.println();

                //getDiff(fen, moves);
                count++;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDiff(String fen, List<String> pyMoves) {
        List<String> myMoves = new LinkedList<>();
        game.board = new Board(fen);
        for (Move m : game.moveGenerator.getLegalMoves(game.board)) myMoves.add(m.move);

        List<String> result = new LinkedList<>();

        Collections.sort(myMoves);
        Collections.sort(pyMoves);

        //System.out.println("My output: " + myOut);
        //System.out.println("Py output: " + pyOut);

        for (String s : pyMoves) {
            if (!myMoves.contains(s)) result.add(s);
        }

        for (String s : myMoves) {
            if (!pyMoves.contains(s)) result.add("!" + s);
        }

        if (result.size() != 0) {
            diff++;
            System.out.println(fen);
            System.out.println("My output: " + myMoves);
            System.out.println("Py output: " + pyMoves);
            System.out.println("Result: " + result + "\n");
        }
    }

    public void getDiff(String fen, String pyMoves) {
        String[] pythonOutput = pyMoves.split(", ");
        List<String> pyOut = Arrays.asList(pythonOutput);

        List<String> myOut = new LinkedList<>();
        game.board = new Board(fen);
        for (Move m : game.moveGenerator.getLegalMoves(game.board)) myOut.add(m.move);

        List<String> result = new LinkedList<>();

        Collections.sort(myOut);
        Collections.sort(pyOut);

        System.out.println("My output: " + myOut);
        System.out.println("Py output: " + pyOut);

        for (String s : pyOut) {
            if (!myOut.contains(s)) result.add(s);
        }

        for (String s : myOut) {
            if (!pyOut.contains(s)) result.add("!" + s);
        }

        if (result.size() != 0) {
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
        if (depth == 0) {
            writeOutput(game.saveGameToFEN(), game.moveGenerator.getLegalMoves(board));
        } else {
            for (Move m : game.moveGenerator.getLegalMoves(board)) {
                writePerft(depth - 1, game.board = game.moveGenerator.makeMove(m, board));
            }
        }
    }

    public void writePerftNumbers(String[] fens, int depth) {
        long result;

        for (int i = 1; i <= depth; i++) {
            System.out.println("Calculating depth " + i);
            for (String fen : fens) {
                game.board = new Board(fen);
                result = perft.perft(i, game.board);

                if (i == 1) {
                    writePerftResults(fen, i, result, true, false);
                } else {
                    writePerftResults(fen, i, result, false, i == depth);
                }
            }
            System.out.println();
        }
    }

    public void writePerftResults(String fen, int depth, long result, boolean writeFen, boolean end) {
        try {
            StringBuilder out = new StringBuilder();
            if (writeFen) {
                out.append(fen).append("\n");
            }

            out.append("Perft at depth ").append(depth).append(": ").append(result).append("\n");

            if (end) out.append("\n");

            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\perft_test_suite_results.txt", true));
            writer.append(out.toString());

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutput(String fen, List<Move> moves) {
        if (moves.size() == 0) {
            System.out.println("No legal moves: " + fen);
            return;
        }
        try {
            StringBuilder out = new StringBuilder(fen + "--");

            for (int i = 0; i < moves.size(); i++) {
                if (i != moves.size() - 1) {
                    out.append(moves.get(i).move).append(", ");
                } else {
                    out.append(moves.get(i).move).append("\n");
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\my_fen_list.txt", true));
            writer.append(out.toString());

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comparePythonToJava() {
        String pyPath = "C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\fen_list.txt";
        String myPath = "C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\my_fen_list.txt";

        Map<String, List<String>> pythonMap = new HashMap<>();
        Map<String, List<String>> javaMap = new HashMap<>();
        Map<String, List<String>> resultMap = new HashMap<>();

        List<String> pythonOutput = new LinkedList<>();
        List<String> javaOutput = new LinkedList<>();

        int diff = 0;

        try {
            Scanner pythonReader = new Scanner(new FileInputStream(pyPath));
            Scanner javaReader = new Scanner(new FileInputStream(myPath));

            while (pythonReader.hasNextLine()) {
                pythonOutput.add(pythonReader.nextLine());
            }

            while (javaReader.hasNextLine()) {
                javaOutput.add(javaReader.nextLine());
            }

            Collections.sort(pythonOutput);
            Collections.sort(javaOutput);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String line : pythonOutput) {
            if (line.split("--").length < 2) {
                System.out.println("Py Fault - " + line.split("--").length + line);
                continue;
            }

            String fen = line.split("--")[0];
            String moves = line.split("--")[1];
            pythonMap.put(fen.split(" ")[0], Arrays.asList(moves.split(", ")));
        }

        for (String line : javaOutput) {
            if (line.split("--").length < 2) {
                System.out.println("Java Fault: " + line.split("--").length + ", " + line);
                continue;
            }

            String fen = line.split("--")[0];
            String moves = line.split("--")[1];
            javaMap.put(fen.split(" ")[0], Arrays.asList(moves.split(", ")));
        }


        for (Map.Entry<String, List<String>> entry : pythonMap.entrySet()) {
            if (javaMap.get(entry.getKey()) == null) {
                resultMap.put(entry.getKey(), new LinkedList<>());
                diff++;
                continue;
            }

            List<String> differences = javaMap.get(entry.getKey()).stream()
                    .filter(element -> !pythonMap.get(entry.getKey()).contains(element))
                    .collect(Collectors.toList());

            if (differences.size() != 0) {
                resultMap.put(entry.getKey(), differences);
                diff++;
            }
        }

        for (Map.Entry<String, List<String>> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }

        System.out.println("Python Length: " + pythonMap.size());
        System.out.println("Java Length: " + javaMap.size());
        System.out.println("Diff: " + diff);
    }
}
