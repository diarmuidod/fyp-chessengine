package GameManager;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import Engine.Engine;
import Utils.Utils;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
        mctsEngine = new Engine();
    }

    public Game(String FEN) {
        board = new Board(FEN);
        moveGenerator = new MoveGenerator();
        movesPlayed = new LinkedList<>();
        input = new Scanner(System.in);
        gameState = getGameState(board);
        mctsEngine = new Engine();
    }

    //Generate FEN String from the current position
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

    //Prints full board
    public void printBoard() {
        board.printBoard();
    }

    //Retrieves all fully legal moves
    public List<Move> getLegalMoves() {
        return moveGenerator.getLegalMoves(board);
    }

    //Prints the board, populated only with the set bits of a bitset
    public void printBoard(BitSet bitset) {
        board.printBoard(bitset);
    }

    //Determines game state
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

    public enum GameState {
        ONGOING,
        WHITE_WINS,
        BLACK_WINS,
        DRAW
    }
}
