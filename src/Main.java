import Board.Board;
import Board.Move;
import GameManager.Game;

public class Main {
    public static void main(String[] args) {

        if(args.length == 3) {
            Game chess = new Game(args[1]);
            chess.moveGenerator.perft(Integer.parseInt(args[0]), chess.board);
        }
        //known problems:
        //"7k/3p4/5N1K/2P5/8/8/8/8 w - - 0 1" -> Ne8 and Ng8 are missing

        Game chess = new Game("7k/3p4/5N1K/2P5/8/8/8/8 w - - 0 1");
        chess.printBoard();
        System.out.println();
        System.out.println("King in Check: " + chess.moveGenerator.kingInCheck(chess.board, chess.board.whiteToMove));
        System.out.println();

        System.out.println(chess.getLegalMoves() + "\n\nMoves: " + chess.getLegalMoves().size());
        System.out.println();

        for (int i = 0; i < 16; i++) System.out.println("Moves at depth " + i + ": " + chess.moveGenerator.perft(i, chess.board));
    }
}
