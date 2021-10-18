import Board.Perft;
import GameManager.Game;

public class Main {
    public static void main(String[] args) {
        Perft perft = new Perft();

        //For use in tandem with perftree, for debugging Move Generation
        if(args.length > 0) {
            Game chess = new Game(args[1]);
            if(args.length == 3) {
                String startSq = "" + args[2].charAt(0) + args[2].charAt(1);
                String targetSq = "" + args[2].charAt(2) + args[2].charAt(3);

                int startSquare = perft.getIndexFromSquare(startSq);
                int targetSquare = perft.getIndexFromSquare(targetSq);

                chess.board = chess.moveGenerator.makeMove(startSquare, targetSquare, chess.board);
            }

            perft.perft(Integer.parseInt(args[0]), chess.board);
            return;
        }

        Game chess = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        chess.printBoard();
        System.out.println();
        chess.printBoard(chess.moveGenerator.getAttackedSquares(chess.board, chess.board.whiteToMove));
        //System.out.println("King in Check: " + chess.moveGenerator.kingInCheck(chess.board, chess.board.whiteToMove));
        System.out.println();

        System.out.println(chess.getLegalMoves() + "\n\nMoves: " + chess.getLegalMoves().size());

        for (int i = 0; i < 6; i++) System.out.println("Moves at depth " + i + ": " + perft.perft(i, chess.board));
    }
}
