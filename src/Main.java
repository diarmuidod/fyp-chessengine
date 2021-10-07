import Board.Board;
import Board.Move;
import GameManager.Game;

public class Main {
    public static void main(String[] args) {
        if(args.length == 3) {
            Game chess = new Game(args[1]);
            chess.moveGenerator.perft(Integer.parseInt(args[0]), chess.board);
        }

        Game chess = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        chess.printBoard();
        System.out.println();
        chess.printBoard(chess.moveGenerator.getAttackedSquares(chess.board, chess.board.whiteToMove));
        //System.out.println("King in Check: " + chess.moveGenerator.kingInCheck(chess.board, chess.board.whiteToMove));
        System.out.println();

        System.out.println(chess.getLegalMoves() + "\n\nMoves: " + chess.getLegalMoves().size());

        for (int i = 0; i < 6; i++) System.out.println("Moves at depth " + i + ": " + chess.moveGenerator.perft(i, chess.board));
    }
}
