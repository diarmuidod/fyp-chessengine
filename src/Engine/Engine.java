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
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    LinkedList<Integer> gameLength = new LinkedList<>();
    LinkedList<Node> pathToRoot;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public Engine() {
        try {
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/chessdb", "root", "");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Zobrist.readRandomNumbers();
        root = new Node();
        root.loadNodeData();
        transpositionTable = new Hashtable<>();
        transpositionTable.put(Zobrist.getZobristKey(root.boardState), root);
        pathToRoot = new LinkedList<>();
    }

    public List<LinkedList<String>> getBestVariations(Game game, int depth, int variations) {
        List<LinkedList<String>> variationList = new LinkedList<>();
        String sideValue = game.board.whiteToMove ? "(n.wValue/n.bValue)" : "(n.bValue/n.wValue)";

        //take top 3 moves
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
                System.out.println(childKey + ", " + move);

                LinkedList<String> variation = getVariation(childKey, game.board.whiteToMove, depth);
                variation.addFirst(move);
                variationList.add(variation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(LinkedList<String> list : variationList) {
            System.out.println(list);
        }
        return variationList;
    }

    public LinkedList<String> getVariation(long zobristKey, boolean whiteToMove, int depth) {
        LinkedList<String> variation = new LinkedList<>();
        String keyMovePair;

        for(int i = 0; i < depth; i++) {
            keyMovePair = getBestMove(zobristKey, whiteToMove);
            //if(keyMovePair == null) break;
            zobristKey = Long.parseLong(keyMovePair.split(",")[0]);
            variation.add(keyMovePair.split(",")[1]);
        }

        return variation;
    }

    public String getBestMove(long zobristKey, boolean whiteToMove) {
        String sideValue = whiteToMove ? "(n.wValue/n.bValue)" : "(n.bValue/n.wValue)";

        String dataSQL = "SELECT p.childKey, p.move FROM nodeTbl AS n JOIN parentChildTbl AS p " +
                         "ON n.zobristKey = p.childKey WHERE p.parentKey = " + zobristKey +
                         " ORDER BY "+ sideValue + " DESC LIMIT 1";

        String keyPair = null;

        try {
            rs = stmt.executeQuery(dataSQL);

            while (rs.next()) {
                keyPair = rs.getString(1) + "," + rs.getString(2);
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
        System.out.println("Start Training: " + formatter.format(new Date(System.currentTimeMillis())));
        if (getGameState(root) != Game.GameState.ONGOING) return;

        boolean timeRemaining = true;
        long startTime = System.currentTimeMillis();

        if (root.children == null) {
            root.children = generateChildren(root);
        }

        long gamesPlayed = 0;

        while (timeRemaining) {
            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(root);
            Node expandedChild = expansion(selectedChild);
            long result = rollout(expandedChild);
            root = backpropagation(expandedChild, result);
            gamesPlayed++;

            if (gamesPlayed % 256 == 0) storeSearchResults();

            pathToRoot.clear();
        }

        storeSearchResults();
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
            node.n.add(1);
            if (reward == 1) {
                node.wV.add(1);
            } else if (reward == -1) {
                node.bV.add(1);
            }

            transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);
            node = node.parent;
        }

        //update root node
        node.n.assign(0);
        node.wV.assign(0);
        node.bV.assign(0);

        for (Node n : node.children) {
            node.n.add(n.n);
            node.wV.add(n.wV);
            node.bV.add(n.bV);
        }

        return node;
    }

    public double getUCB(Node node) {
        if (node.n.isZero()) return 0;

        double exploit = (node.boardState.whiteToMove ? node.wV.doubleValue() : node.bV.doubleValue()) / (node.n.doubleValue());
        double constant = sqrt(2);
        BigInt temp = node.parent.n.copy();
        temp.div(node.n);
        double explore = sqrt(log(temp.doubleValue()));

        return exploit + constant * explore;
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

            String sql = "REPLACE INTO nodeTbl (zobristKey, visits, wValue, bValue) " +
                    "VALUES (" + key + ", " + node.n.toString() + ", " + node.wV.toString() + ", " + node.bV.toString() + ")";

            stmt.execute(sql);

            if (node.parent == null) continue;

            long parentKey = Zobrist.getZobristKey(node.parent.boardState);
            stmt.executeUpdate("INSERT IGNORE INTO parentChildTbl VALUES (" + parentKey + ", " + key + ", \"" + move + "\")");
        }

        System.out.println(transpositionTable.size() + " nodes in " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) + " seconds.");
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
        BigInt n; //How often this node has been visited
        BigInt wV; //White wins from position
        BigInt bV; //Black wins from position

        Node parent;
        Move move;
        Board boardState;

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
            try {
                String dataSQL = "SELECT * FROM nodeTbl WHERE zobristKey = " + Zobrist.getZobristKey(this.boardState);

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
