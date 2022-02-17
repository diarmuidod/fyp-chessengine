package Engine;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import Board.Zobrist;
import GameManager.Game;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.log;
import static java.lang.Math.sqrt;


public class Engine {
    //https://medium.com/@ishaan.gupta0401/monte-carlo-tree-search-application-on-chess-5573fc0efb75

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    LinkedList<Integer> gameLength = new LinkedList<>();
    private static final MoveGenerator moveGenerator = new MoveGenerator();
    private static final Random rand = new Random();
    private static Hashtable<Long, Node> transpositionTable;
    LinkedList<Node> pathToRoot;

    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public Node root;

    public Engine() {
        Zobrist.readRandomNumbers();
        root = new Node();
        transpositionTable = new Hashtable<>();
        transpositionTable.put(Zobrist.getZobristKey(root.boardState), root);
        pathToRoot = new LinkedList<>();
    }

    public Move getBestMove(List<Move> movesPlayed) {
        double currentUCB;
        Node node = findMoveNode(movesPlayed);

        if (getGameState(node) != Game.GameState.ONGOING) return null;

        if (node.children == null) {
            node.children = generateChildren(node);
            return node.children.get(rand.nextInt(node.children.size() - 1)).move;
        }

        Node bestMoveNode = node.children.get(0);

        for (Node child : node.children) {
            currentUCB = getUCB(child);
            if (child.boardState.whiteToMove) {
                if (currentUCB > getUCB(bestMoveNode)) {
                    bestMoveNode = child;
                }
            } else {
                if (currentUCB < getUCB(bestMoveNode)) {
                    bestMoveNode = child;
                }
            }
        }

        return Objects.requireNonNull(bestMoveNode.move);
    }

    public Node findMoveNode(List<Move> movesPlayed) {
        Node leafNode = root;

        for (Move move : movesPlayed) {
            if (leafNode.children == null) {
                leafNode.children = generateChildren(leafNode);
            }

            for (Node child : leafNode.children) {
                if (child.move.isEqual(move)) {
                    leafNode = child;
                    break;
                }
            }
        }

        return leafNode;
    }

    public void trainEngine(long timeInSeconds) throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chessdb", "root", "");
        stmt = conn.createStatement();

        System.out.println("Start Training: " + formatter.format(new Date(System.currentTimeMillis())));
        if (getGameState(root) != Game.GameState.ONGOING) return;

        boolean timeRemaining = true;
        long startTime = System.currentTimeMillis();

        if (root.children == null) {
            root.children = generateChildren(root);
        }

        int gamesPlayed = 0;

        while (timeRemaining) {
            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(root);
            Node expandedChild = expansion(selectedChild);
            long result = rollout(expandedChild);
            root = backpropagation(expandedChild, result);
            gamesPlayed++;

            if (gamesPlayed % 64 == 0) storeSearchResults();

            //System.out.println(pathToRoot);
            pathToRoot.clear();
        }

        System.out.println("\nGames played: " + gameLength.size());

        int avgGame = 0;
        for (Integer i : gameLength) avgGame += i;
        System.out.println("Average game length: " + avgGame / gameLength.size());

        storeSearchResults();

        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }

    public Node selection(Node node) {
        if (!pathToRoot.contains(node)) pathToRoot.add(node);
        if (node.children == null) node.children = generateChildren(node);

        Node selectedChild = node.children.get(0);

        while (true) {
            if (node.children == null) node.children = generateChildren(node);
            if (node.boardState.whiteToMove) {
                double maxUCB = Double.NEGATIVE_INFINITY;
                for (Node n : node.children) {
                    if (n.isLeafNode()) {
                        return n;
                    }

                    if (getUCB(n) > maxUCB) {
                        maxUCB = getUCB(n);
                        selectedChild = n;
                    }
                }
            } else {
                double minUCB = Double.POSITIVE_INFINITY;
                for (Node n : node.children) {
                    if (n.isLeafNode()) {
                        return n;
                    }

                    if (getUCB(n) < minUCB) {
                        minUCB = getUCB(n);
                        selectedChild = n;
                    }
                }
            }

            node = selectedChild;
        }
    }

    public Node expansion(Node node) {
        if (!pathToRoot.contains(node)) pathToRoot.add(node);
        if (node.children == null) node.children = generateChildren(node);
        if (getGameState(node) != Game.GameState.ONGOING) return node;

        Node expandingChild = node.children.get(0);

        if (node.boardState.whiteToMove) {
            double maxUCB = Double.NEGATIVE_INFINITY;
            for (Node n : node.children) {
                double tempUCB = getUCB(n);
                if (tempUCB > maxUCB) expandingChild = n;
            }
        } else {
            double minUCB = Double.POSITIVE_INFINITY;
            for (Node n : node.children) {
                double tempUCB = getUCB(n);
                if (tempUCB < minUCB) expandingChild = n;
            }
        }

        return expandingChild;
    }

    public long rollout(Node node) {
        if (!pathToRoot.contains(node)) pathToRoot.add(node);

        Game.GameState state = getGameState(node);

        if (state != Game.GameState.ONGOING) {
            if (state == Game.GameState.WHITE_WINS) return 1;
            if (state == Game.GameState.BLACK_WINS) return -1;
            if (state == Game.GameState.DRAW) return 0;
        }

        if (node.children == null) node.children = generateChildren(node);
        Node child = node.children.get(rand.nextInt(node.children.size()));

        return rollout(child);
    }

    public Node backpropagation(Node node, long reward) {
        while (node.parent != null) {
            node.n += 1;
            if (reward == 1) {
                node.wV++;
            } else if (reward == -1) {
                node.bV++;
            }

            transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);
            node = node.parent;
        }

        //update root node
        node.n = 0;
        node.wV = 0;
        node.bV = 0;

        for (Node n : node.children) {
            node.n += n.n;
            node.wV += n.wV;
            node.bV += n.bV;
        }

        return node;
    }

    public double getUCB(Node node) {
        double exploit = (node.boardState.whiteToMove ? node.wV : node.bV) / (node.n * 1.0);
        double constant = sqrt(2);
        double explore = sqrt(log(node.parent.n / (node.n * 1.0)));

        return node.n == 0 ? 0 : exploit + constant * explore;
    }

    public double getUCB(double parentN, double thisN, double v) {
        double exploit = v / thisN;
        double constant = sqrt(2);
        double explore = sqrt(log(parentN / thisN));

        return thisN == 0 ? 0 : exploit + constant * explore;
    }

    public void storeSearchResults() throws SQLException {
        long key;
        Node node;
        String move;

        long startTime = System.currentTimeMillis();

        System.out.print("Stored ");

        for (Entry<Long, Node> entry : transpositionTable.entrySet()) {
            key = entry.getKey();
            node = entry.getValue();
            move = node.move.toString();

            String sql = "INSERT INTO nodeTbl (zobristKey, visits, wValue, bValue) " +
                    "VALUES (" + key + ", " + node.n + ", " + node.wV + ", " + node.bV + ") " +
                    "ON DUPLICATE KEY UPDATE " +
                    "visits = visits + " + node.n + "," +
                    "wValue = wValue + " + node.wV + "," +
                    "bValue = bValue + " + node.bV;

            stmt.execute(sql);

            if (node.parent == null) continue;

            long parentKey = Zobrist.getZobristKey(node.parent.boardState);
            stmt.executeUpdate("INSERT IGNORE INTO parentChildTbl VALUES (" + parentKey + ", " + key + ", \"" + move + "\")");

        }

        System.out.println(transpositionTable.size() + " nodes in " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) + " seconds.");

        root = new Node();
        transpositionTable.clear();
        transpositionTable.put(Zobrist.getZobristKey(root.boardState), root);
    }

    public List<Node> generateChildren(Node node) {
        List<Node> children = new LinkedList<>();

        for (Move move : moveGenerator.getLegalMoves(node.boardState)) {
            children.add(new Node(node, move));
        }

        //update children with db data
        String dataSQL = "SELECT * FROM nodeTbl WHERE zobristKey IN " +
                "(SELECT childKey FROM parentChildTbl WHERE parentKey = " + Zobrist.getZobristKey(node.boardState) + ")";

        long key, visits, wValue, bValue;

        try {
            rs = stmt.executeQuery(dataSQL);

            while (rs.next()) {
                key = rs.getLong(1);
                visits = rs.getLong(2);
                wValue = rs.getLong(3);
                bValue = rs.getLong(4);

                for (Node n : children) {
                    if (Zobrist.getZobristKey(n.boardState) == key) {
                        n.n = visits;
                        n.wV = wValue;
                        n.bV = bValue;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return children;
    }

    public Game.GameState getGameState(Node node) {
        if (node.boardState.fiftyMoveCount >= 50) {
            return Game.GameState.DRAW;
        }

        //no legal moves
        if (moveGenerator.getLegalMoves(node.boardState).size() == 0) {
            //white in check
            if (moveGenerator.kingInCheck(node.boardState, true)) {
                return Game.GameState.BLACK_WINS;
            }

            //white in check
            if (moveGenerator.kingInCheck(node.boardState, false)) {
                return Game.GameState.WHITE_WINS;
            }

            return Game.GameState.DRAW;
        }

        //threefold repetition
        for (Node n : pathToRoot) {
            if (Collections.frequency(pathToRoot, Zobrist.getZobristKey(n.boardState)) >= 3) {
                System.out.println("Threefold repetition - " + gameLength.size());
                return Game.GameState.DRAW;
            }
        }

        return Game.GameState.ONGOING;
    }

    public class Node {
        public List<Node> children;
        long n; //How often this node has been visited
        long wV; //White wins from position
        long bV; //Black wins from position

        Node parent;
        Move move;
        Board boardState;

        Node() {
            parent = null;
            move = new Move();
            children = null;
            boardState = new Board();

            n = 0;
            wV = 0;
            bV = 0;
        }

        Node(Node parent, Move move) {
            this.parent = parent;
            this.move = move;
            children = null;
            boardState = moveGenerator.makeMove(move, parent.boardState);

            n = 0;
            wV = 0;
            bV = 0;
        }

        public boolean isLeafNode() {
            return n == 0;
        }

        public String toString() {
            return move.toString();
        }
    }
}
