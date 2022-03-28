package Engine;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import Board.Zobrist;
import GameManager.Game;
import Utils.BigInt;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.log;
import static java.lang.Math.sqrt;

public class Engine {
    private static final MoveGenerator moveGenerator = new MoveGenerator();
    private static final Random rand = new Random();
    private static Hashtable<Long, Node> transpositionTable;
    public Node root;
    public boolean toggleTraining = false;
    long rootKey;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    LinkedList<Integer> gameLength = new LinkedList<>();
    LinkedList<Node> pathToRoot;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    long timeSinceLastSave;

    public Engine() {
        Zobrist.readRandomNumbersFromDB();
        try {
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/chessdb", "root", "");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        root = new Node();
        root.loadNodeData();
        rootKey = Zobrist.getZobristKey(new Board());
        transpositionTable = new Hashtable<>();
        transpositionTable.put(rootKey, root);
        pathToRoot = new LinkedList<>();
    }

    public List<LinkedList<String>> getBestVariations(Game game, int depth, int variations) {
        List<LinkedList<String>> variationList = new LinkedList<>();
        String sideValue = game.board.whiteToMove ? "(n.wValue/n.visits)" : "(n.bValue/n.visits)";

        //take top n moves
        String dataSQL = "SELECT n.zobristKey, p.move FROM nodeTbl AS n JOIN parentChildTbl AS p ON n.zobristKey = p.childKey " +
                "WHERE p.parentKey = " + Zobrist.getZobristKey(game.board) +
                " ORDER BY " + sideValue + " DESC LIMIT " + variations;

        long childKey;
        String move;

        try {
            rs = stmt.executeQuery(dataSQL);

            while (rs.next()) {
                childKey = rs.getLong(1);
                move = rs.getString(2);

                LinkedList<String> variation = getVariation(childKey, game.board.whiteToMove, depth);
                variation.addFirst(move);
                variationList.add(variation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return variationList;
    }

    public LinkedList<String> getVariation(long zobristKey, boolean whiteToMove, int depth) {
        LinkedList<String> variation = new LinkedList<>();
        String keyMovePair;

        for (int i = 0; i < depth; i++) {
            keyMovePair = getBestMove(zobristKey, whiteToMove);
            whiteToMove = !whiteToMove;
            if (keyMovePair == null) return variation;
            zobristKey = Long.parseLong(keyMovePair.split(",")[0]);
            variation.add(keyMovePair.split(",")[1]);
        }

        return variation;
    }

    public String getBestMove(long zobristKey, boolean whiteToMove) {
        ResultSet moveRS;
        String sideValue = whiteToMove ? "(n.wValue/n.visits)" : "(n.bValue/n.visits)";

        String dataSQL = "SELECT p.childKey, p.move FROM nodeTbl AS n JOIN parentChildTbl AS p " +
                "ON n.zobristKey = p.childKey WHERE p.parentKey = " + zobristKey +
                " ORDER BY " + sideValue + " DESC LIMIT 1";

        String keyPair = null;

        try {
            moveRS = stmt.executeQuery(dataSQL);

            while (moveRS.next()) {
                keyPair = moveRS.getString(1) + "," + moveRS.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return keyPair;
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
        trainEngine(timeInSeconds, root);
    }

    public void trainEngine(long timeInSeconds, Node position) throws SQLException {
        System.out.println("Start Training: " + formatter.format(new Date(System.currentTimeMillis())));
        if (getGameState(position) != Game.GameState.ONGOING) return;

        long startTime = System.currentTimeMillis();

        if (position.children == null) position.children = generateChildren(position);

        timeSinceLastSave = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + (timeInSeconds * 1000)) {
            Node selectedChild = selection(position);
            Node expandedChild = expansion(selectedChild);
            Node result = rollout(expandedChild);
            root = backpropagation(result);

            storeSearchResults();
            pathToRoot.clear();
        }
    }

/*
    public void trainEngine() throws SQLException {
        System.out.println("Start Training: " + formatter.format(new Date(System.currentTimeMillis())));
        if (getGameState(root) != Game.GameState.ONGOING) return;

        if (root.children == null) root.children = generateChildren(root);

        while (toggleTraining) {
            Node selectedChild = selection(root);
            Node expandedChild = expansion(selectedChild);
            Node result = rollout(expandedChild);
            root = backpropagation(result);

            storeSearchResults();
            pathToRoot.clear();
        }
    }

*/

    public Node selection(Node node) {
        if (!pathToRoot.contains(node)) pathToRoot.add(node);
        if (node.children == null) node.children = generateChildren(node);

        Node selectedChild = null;

        while (true) {
            if (node.children == null) node.children = generateChildren(node);

            double maxUCB = Double.NEGATIVE_INFINITY;
            for (Node n : node.children) {
                if (n.isLeafNode()) {
                    transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);
                    return n;
                }

                if (getUCB(n) > maxUCB) {
                    maxUCB = getUCB(n);
                    selectedChild = n;
                }
            }

            node = selectedChild == null ? node.children.get(rand.nextInt(node.children.size())) : selectedChild;
            transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);
        }
    }

    public Node expansion(Node node) {
        transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);
        if (!pathToRoot.contains(node)) pathToRoot.add(node);
        if (node.children == null) node.children = generateChildren(node);
        if (getGameState(node) != Game.GameState.ONGOING) return node;

        Node expandingChild = node.children.get(0);

        double maxUCB = Double.NEGATIVE_INFINITY;
        for (Node n : node.children) {
            if (getUCB(n) > maxUCB) {
                maxUCB = getUCB(n);
                expandingChild = n;
            }
        }
        transpositionTable.put(Zobrist.getZobristKey(node.boardState), expandingChild);
        return expandingChild;
    }

    public Node rollout(Node node) {
        if (!pathToRoot.contains(node)) pathToRoot.add(node);

        if (getGameState(node) != Game.GameState.ONGOING) {
            return node;
        }

        if (node.children == null) node.children = generateChildren(node);
        Node child = node.children.get(rand.nextInt(node.children.size()));
        transpositionTable.put(Zobrist.getZobristKey(node.boardState), child);
        return rollout(child);
    }

    public Node backpropagation(Node node) {
        long reward;

        switch (getGameState(node)) {
            case WHITE_WINS -> reward = 1;
            case BLACK_WINS -> reward = -1;
            default -> reward = 0;
        }

        while (Zobrist.getZobristKey(node.boardState) != rootKey) {
            node.n.add(1);
            if (reward == 1) {
                node.wV.add(1);
            } else if (reward == -1) {
                node.bV.add(1);
            }

            transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);
            node = node.parent;
        }

        node.n.add(1);
        if (reward == 1) {
            node.wV.add(1);
        } else if (reward == -1) {
            node.bV.add(1);
        }

        return node;
    }

    public double getUCB(Node node) {
        if (node.n.isZero()) return 0;

        double exploit = (node.boardState.whiteToMove ? (node.wV.doubleValue() / node.n.doubleValue()) : (node.bV.doubleValue() / node.n.doubleValue()));
        double constant = sqrt(2);
        BigInt temp = node.parent.n.copy();
        temp.div(node.n);
        double explore = sqrt(log(temp.doubleValue()));

        return exploit + constant * explore;
    }

    public void storeSearchResults() throws SQLException {
        int nodesStored = 0;
        long key;
        Node node;
        String move;

        String sql;

        long startTime = System.currentTimeMillis();

        System.out.print("Stored ");

        for (Entry<Long, Node> entry : transpositionTable.entrySet()) {
            key = entry.getKey();
            node = entry.getValue();
            move = node.move.toString();

            sql = "REPLACE INTO nodeTbl (zobristKey, visits, wValue, bValue) " +
                    "VALUES (" + key + ", " + node.n.toString() + ", " + node.wV.toString() + ", " + node.bV.toString() + ")";

            if (node.n.isZero()) continue;

            stmt.execute(sql);
            nodesStored++;

            if (node.parent == null) continue;

            long parentKey = Zobrist.getZobristKey(node.parent.boardState);
            if (parentKey != key)
                stmt.executeUpdate("INSERT IGNORE INTO parentChildTbl VALUES (" + parentKey + ", " + key + ", \"" + move + "\")");
        }

        System.out.println(nodesStored + " nodes in "
                + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) + " seconds. Time since last save: "
                + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeSinceLastSave) + " seconds." + new Date());
        timeSinceLastSave = System.currentTimeMillis();
        transpositionTable.clear();
        root = new Node();
        root.loadNodeData();

        transpositionTable.put(rootKey, root);
    }

    public List<Node> generateChildren(Node node) {
        List<Node> children = new LinkedList<>();

        for (Move move : moveGenerator.getLegalMoves(node.boardState)) {
            children.add(new Node(node, move));
        }

        //update children with db data
        String dataSQL = "SELECT * FROM nodeTbl WHERE zobristKey IN " +
                "(SELECT childKey FROM parentChildTbl WHERE parentKey = " + Zobrist.getZobristKey(node.boardState) + ")";

        long key;
        String visits, wValue, bValue;

        try {
            rs = stmt.executeQuery(dataSQL);

            while (rs.next()) {
                key = rs.getLong(1);
                visits = rs.getString(2);
                wValue = rs.getString(3);
                bValue = rs.getString(4);

                for (Node n : children) {
                    if (Zobrist.getZobristKey(n.boardState) == key) {
                        n.n = new BigInt(visits);
                        n.wV = new BigInt(wValue);
                        n.bV = new BigInt(bValue);
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
        public Move move;
        public Board boardState;
        BigInt n; //How often this node has been visited
        BigInt wV; //White wins from position
        BigInt bV; //Black wins from position
        Node parent;

        Node() {
            parent = null;
            move = new Move();
            children = null;
            boardState = new Board();

            n = new BigInt(0);
            wV = new BigInt(0);
            bV = new BigInt(0);
        }

        Node(Node parent, Move move) {
            this.parent = parent;
            this.move = move;
            children = null;
            boardState = moveGenerator.makeMove(move, parent.boardState);

            n = new BigInt(0);
            wV = new BigInt(0);
            bV = new BigInt(0);
        }

        public boolean isLeafNode() {
            return n.isZero();
        }

        public void loadNodeData() {
            String dataSQL;
            try {
                if (Zobrist.getZobristKey(this.boardState) == rootKey) {
                    dataSQL = "UPDATE nodeTbl SET visits = " +
                            "(SELECT SUM(n.visits) " +
                            "FROM nodeTbl AS n JOIN parentChildTbl AS p " +
                            "ON n.zobristKey = p.childKey " +
                            "WHERE p.parentKey = " + rootKey + ") " +
                            "WHERE zobristKey = " + rootKey;
                    stmt.execute(dataSQL);

                    dataSQL = "UPDATE nodeTbl SET wValue = " +
                            "(SELECT SUM(n.wValue) " +
                            "FROM nodeTbl AS n JOIN parentChildTbl AS p " +
                            "ON n.zobristKey = p.childKey " +
                            "WHERE p.parentKey = " + rootKey + ") " +
                            "WHERE zobristKey = " + rootKey;
                    stmt.execute(dataSQL);

                    dataSQL = "UPDATE nodeTbl SET bValue = " +
                            "(SELECT SUM(n.bValue) " +
                            "FROM nodeTbl AS n JOIN parentChildTbl AS p " +
                            "ON n.zobristKey = p.childKey " +
                            "WHERE p.parentKey = " + rootKey + ") " +
                            "WHERE zobristKey = " + rootKey;
                    stmt.execute(dataSQL);
                }

                dataSQL = "SELECT * FROM nodeTbl WHERE zobristKey = " + Zobrist.getZobristKey(this.boardState);

                rs = stmt.executeQuery(dataSQL);

                while (rs.next()) {
                    long key = rs.getLong(1);

                    if (Zobrist.getZobristKey(this.boardState) == key) {
                        this.n.assign(rs.getString(2));
                        this.wV.assign(rs.getString(3));
                        this.bV.assign(rs.getString(4));
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public String toString() {
            return move.toString();
        }
    }
}
