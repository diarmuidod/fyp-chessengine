import Board.Zobrist;
import Engine.Engine;
import GUI.UI;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    //  Move generation only fails on below fen, all other test cases are flawless
    //  "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -" at depth 2
    //  cannot locate source of problem, may revisit later. Will move on for now.

    public static void main(String[] args) throws IOException, SQLException {
        //Zobrist.writeRandomNumbersToDB();
        //new Engine().trainEngine(885);
        new UI();
    }
}