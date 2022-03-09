import Debug.MoveGenTest;
import Engine.Engine;
import GUI.UserInterface;
import GameManager.Game;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    static UserInterface ui;
    static Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    static MoveGenTest test = new MoveGenTest(game);

    //  Move generation only fails on below fen, all other test cases are flawless
    //  "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -" at depth 2
    //  cannot locate source of problem, may revisit later. Will move on for now.

    public static void main(String[] args) throws IOException, SQLException {
        Engine engine = new Engine();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        System.out.println("Start Time: " + formatter.format(date));

        engine.trainEngine(36000);

        date = new Date(System.currentTimeMillis());
        System.out.println("End Time: " + formatter.format(date));
    }
}

