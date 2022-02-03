package GameManager;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import Debug.Perft;
import Engine.Engine;
import Utils.Utils;

import java.sql.SQLException;
import java.util.*;

public class Game {
    //Stole this from the internet - https://gist.github.com/Dani4kor/e1e8b439115878f8c6dcf127a4ed5d3e
    private static final String FEN_REGEX = "\\s*^(((?:[rnbqkpRNBQKP1-8]+\\/){7})[rnbqkpRNBQKP1-8]+)\\s([b|w])\\s(-|[K|Q|k|q]{1,4})\\s(-|[a-h][1-8])\\s(\\d+\\s\\d+)$";
    //Made this myself
    private static final String MOVE_REGEX = "((O-O(-O)?)|(?:[KQNBR]([a-h1-8]?x?[a-h][1-8])|((?:[a]x)?[b][2-7])| ((?:[b]x)?[ac][2-7])|((?:[c]x)?[bd][2-7])|((?:[d]x)?[ce][2-7])|((?:[e]x)?[df][2-7])|((?:[f]x)?[eg][2-7])|((?:[g]x)?[fh][2-7]))|(?:((?:[h]x)?[g][2-7])|((?:[a]x)?[b][18])|((?:[b]x)?[ac][18])|((?:[c]x)?[bd][18])|((?:[d]x)?[ce][18])|((?:[e]x)?[df][18])|((?:[f]x)?[eg][18])|((?:[g]x)?[fh][18])|((?:[h]x)?[g][18]))(=[QNBR]))[+#]?";
    public Board board;
    public MoveGenerator moveGenerator;
    public List<Move> movesPlayed;
    public Scanner input;
    public GameState gameState;
    public Engine mctsEngine;

    public Game() {
        board = new Board();
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
        gameState = getGameState(board);
        mctsEngine = loadEngine();
    }
    public Game(Engine engine) {
        board = new Board();
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
        gameState = getGameState(board);
        mctsEngine = engine;
    }


    public Game(String FEN) {
        board = new Board(FEN);
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
        gameState = getGameState(board);
        mctsEngine = loadEngine();
    }

    public Game(String FEN, Engine engine) {
        board = new Board(FEN);
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
        gameState = getGameState(board);
        mctsEngine = engine;
    }

    public void playGame() throws SQLException {
        Perft perft = new Perft();
        Move activeMove = null;
        List<Move> legalMoves;

        while (getGameState(board) == GameState.ONGOING) {
            legalMoves = moveGenerator.getLegalMoves(board);

            if (board.whiteToMove) {
                printBoard();
                System.out.println();

                if (board.whiteToMove) {
                    System.out.println("White to move");
                } else {
                    System.out.println("Black to move");
                }

                System.out.println("Legal Moves: " + getLegalMoves());
                System.out.print("Enter move: ");
                String inp = input.nextLine();

                if (inp.equals("quit") || inp.equals("exit")) {
                    saveEngine();
                    break;
                }

                for (Move m : legalMoves) {
                    if (m.move.equals(inp)) {
                        activeMove = m;
                        break;
                    }
                }
            } else {
                mctsEngine.trainEngine(5);
                activeMove = mctsEngine.getBestMove(movesPlayed, 0);
            }

            board = moveGenerator.makeMove(Objects.requireNonNull(activeMove), board);
            movesPlayed.add(activeMove);
        }

        switch (getGameState(board)) {
            case WHITE_WINS:
                System.out.println("White wins!");
                break;
            case BLACK_WINS:
                System.out.println("Black wins!");
                break;
            case DRAW:
                System.out.println("It's a draw!");
                break;
        }
        System.out.println(saveGameToFEN());
        printBoard();
    }

    public String saveGameToFEN() {
        StringBuilder fen = new StringBuilder();
        int emptySquares = 0;

        int mark = 64;

        for (int i = 0; i < 8; i++) {
            for (int j = mark - 8; j < mark; j++) {
                if (board.allPieces.get(j)) {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }

                    boolean isWhite = board.whitePieces.get(j);
                    if (board.pawnPieces.get(j)) fen.append(isWhite ? "P" : "p");
                    if (board.knightPieces.get(j)) fen.append(isWhite ? "N" : "n");
                    if (board.bishopPieces.get(j)) fen.append(isWhite ? "B" : "b");
                    if (board.rookPieces.get(j)) fen.append(isWhite ? "R" : "r");
                    if (board.queenPieces.get(j)) fen.append(isWhite ? "Q" : "q");
                    if (board.kingPieces.get(j)) fen.append(isWhite ? "K" : "k");
                } else {
                    emptySquares++;
                }
                if (j == mark - 1) {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }

                    if (j != 7) fen.append("/");
                }
            }
            mark -= 8;
        }

        if (board.whiteToMove) {
            fen.append(" w ");
        } else {
            fen.append(" b ");
        }

        if (board.whiteKingSide) fen.append("K");
        if (board.whiteQueenSide) fen.append("Q");
        if (board.blackKingSide) fen.append("k");
        if (board.blackQueenSide) fen.append("q");

        if (board.enPassantSquare == -1) {
            fen.append(" - ");
        } else {
            fen.append(" ").append(Utils.moveFromIndex(board.enPassantSquare)).append(" ");
        }

        fen.append(board.fiftyMoveCount).append(" ");

        fen.append((movesPlayed.size() / 2) + 1);

        return fen.toString();
    }

    public String saveGameToPGN() {
        return null;
    }

    public void printBoard() {
        board.printBoard();
    }

    public List<Move> getLegalMoves() {
        return moveGenerator.getLegalMoves(board);
    }

    public void printBoard(BitSet bitset) {
        board.printBoard(bitset);
    }

    public GameState getGameState(Board board) {
        if (board.fiftyMoveCount >= 50) {
            return GameState.DRAW;
        }

        //no legal moves
        if (getLegalMoves().size() == 0) {
            //white in check
            if (moveGenerator.kingInCheck(board, true)) {
                return GameState.BLACK_WINS;
            }

            //white in check
            if (moveGenerator.kingInCheck(board, false)) {
                return GameState.WHITE_WINS;
            }

            return GameState.DRAW;
        }

        return GameState.ONGOING;
    }

    public Engine loadEngine() {
        return new Engine();
    }

    public void saveEngine() {

    }

    public enum GameState {
        ONGOING,
        WHITE_WINS,
        BLACK_WINS,
        DRAW
    }
}
