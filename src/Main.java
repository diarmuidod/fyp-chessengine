import Board.Perft;
import GameManager.Game;

public class Main {
    public static void main(String[] args) {
        Perft perft = new Perft();

        //For use in tandem with perftree, for debugging Move Generation
        if(args.length == 3) {
            perft.runPerft(args[0], args[1], args[2]);
        } else if(args.length == 2) {
            perft.runPerft(args[0], args[1]);
        }
    }
}
