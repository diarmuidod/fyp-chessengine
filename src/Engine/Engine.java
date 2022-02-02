package Engine;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import Board.Zobrist;
import GameManager.Game;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;


public class Engine {
    //https://medium.com/@ishaan.gupta0401/monte-carlo-tree-search-application-on-chess-5573fc0efb75

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    private static final MoveGenerator moveGenerator = new MoveGenerator();
    private static final Random rand = new Random();
    private static Hashtable<Long, Node> transpositionTable;
    LinkedList<Node> pathToRoot;

    public Node root;

    public Engine() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/test?" + "user=root&password=");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        root = new Node();
        transpositionTable = new Hashtable<>();
        transpositionTable.put(Zobrist.getZobristKey(root.boardState), root);
        pathToRoot = new LinkedList<>();
    }

    public Move getBestMove(List<Move> movesPlayed, long timeInSeconds) {
        Node node = findMoveNode(movesPlayed);

        if (getGameState(node) != Game.GameState.ONGOING) return null;

        boolean timeRemaining = true;
        long startTime = System.currentTimeMillis();

        double currentUCB;

        if (node.children == null) {
            node.children = generateChildren(node);
        }

        System.out.println("Node children: " + node.children);

        while (timeRemaining) {
            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(node);
            Node expandedChild = expansion(selectedChild);
            double result = rollout(expandedChild);
            node = backpropagation(expandedChild, result);
        }

        node = findMoveNode(movesPlayed);

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

        Move bestMove = bestMoveNode.move;

        //trust me, I hate it too
        for (Move m : moveGenerator.getLegalMoves(bestMoveNode.boardState)) {
            if (bestMoveNode.boardState.equals(moveGenerator.makeMove(m, node.boardState))) {
                bestMove = m;
            }
        }

        return Objects.requireNonNull(bestMove);
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

    public void trainEngine(long timeInSeconds) {
        int iterations = 0;
        if (getGameState(root) != Game.GameState.ONGOING) return;

        boolean timeRemaining = true;
        long startTime = System.currentTimeMillis();

        if (root.children == null) {
            root.children = generateChildren(root);
        }

        while (timeRemaining) {
            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(root);
            Node expandedChild = expansion(selectedChild);
            double result = rollout(expandedChild);
            root = backpropagation(expandedChild, result);
            iterations++;
        }

        for (Node child : root.children) {
            System.out.println(child.move + ", N: " + child.N + ", n: " + child.n + ", v: " + child.v + ", UCB: " + getUCB(child));
        }
        System.out.println();
        System.out.println("Iterations: " + iterations);
        System.out.println("Positions:  " + transpositionTable.size());
    }

    public Node selection(Node node) {
        double currentUCB;

        if (node.children == null) node.children = generateChildren(node);

        Node selectedChild = node.children.get(0);

        for (Node n : node.children) {
            currentUCB = getUCB(n);

            if (n.boardState.whiteToMove) {
                if (currentUCB > getUCB(selectedChild)) {
                    selectedChild = n;
                }
            } else {
                if (currentUCB < getUCB(selectedChild)) {
                    selectedChild = n;
                }
            }
        }

        return selectedChild;
    }

    public Node expansion(Node node) {
        if (node.children == null) return node;

        Node currentChild = node.children.get(0);
        double currentUCB;

        for (Node n : node.children) {
            currentUCB = getUCB(n);

            if (node.boardState.whiteToMove) {
                if (currentUCB > getUCB(currentChild)) {
                    currentChild = n;
                }
            } else {
                if (currentUCB < getUCB(currentChild)) {
                    currentChild = n;
                }
            }
        }

        return expansion(Objects.requireNonNull(currentChild));
    }

    public double rollout(Node node) {
        if (getGameState(node) != Game.GameState.ONGOING) {
            if (getGameState(node) == Game.GameState.WHITE_WINS) return 1;
            if (getGameState(node) == Game.GameState.BLACK_WINS) return -1;
            if (getGameState(node) == Game.GameState.DRAW) return 0;
        }

        if (node.children == null) node.children = generateChildren(node);
        Node child = node.children.get(rand.nextInt(node.children.size()));
        return rollout(child);
    }

    public Node backpropagation(Node node, double reward) {
        node.n += 1;
        node.v += reward;

        while (true) {
            node.N += 1;
            if (node.parents == null) return node;
            node = node.parents.get(0);
        }
    }

    public double getUCB(Node node) {
        return node.v + Math.sqrt(2) * Math.sqrt(Math.log(Math.max(node.N, 1)) / Math.max(node.n, 1));
    }

    public void storeSearchResults() throws SQLException {
        Set<Map.Entry<Long, Node>> entrySet = transpositionTable.entrySet();
        stmt = conn.createStatement();

        long key;
        Node node;
        String move;

        for (Entry<Long, Node> entry : entrySet) {
            key = entry.getKey();
            node = entry.getValue();
            move = node.move.toString();

            rs = stmt.executeQuery("SELECT * FROM nodeTbl WHERE zobristKey = " + key);

            if (!rs.next()) { //node does not exist in database
                stmt.executeUpdate("INSERT INTO nodeTbl VALUES (" + key + ", " + move + ", " + node.N + ", " + node.n + ", " + node.v + ")");

                if (node.parents != null) {
                    for (Node parent : node.parents) {
                        long parentKey = Zobrist.getZobristKey(parent.boardState);
                        rs = stmt.executeQuery("SELECT * FROM parentChildTbl WHERE parentKey = " + parentKey);

                        if (!rs.next()) { //parent child relationship not in db
                            stmt.executeUpdate("INSERT INTO parentChildTbl VALUES (" + parentKey + ", " + key + ")");
                        }
                    }
                }
            } else { //node exists in database

            }
        }

        if (stmt != null) stmt.close();
        if (rs != null) rs.close();

        transpositionTable = new Hashtable<>();
    }

    public List<Node> generateChildren(Node node) {
        List<Node> children = new LinkedList<>();

        for (Move move : moveGenerator.getLegalMoves(node.boardState)) {
            children.add(new Node(node, move));
        }

        List<Node> toRemove = new LinkedList<>();
        for (Node child : children) {
            Node exists = transpositionTable.get(Zobrist.getZobristKey(child.boardState));
            if (exists != null) { //found known position by transposition
                exists.parents.add(node);
                toRemove.add(child);
            } else { //found new position
                transpositionTable.put(Zobrist.getZobristKey(node.boardState), child);
            }
        }

        for (Node value : toRemove) {
            Node exists = transpositionTable.get(Zobrist.getZobristKey(value.boardState));
            children.add(exists);
        }

        children.removeAll(toRemove);

        return children;
    }

    public Game.GameState getGameState(Node node) {
        if (node.boardState.fiftyMoveCount >= 50) {
            return Game.GameState.DRAW;
        }

        pathToRoot.clear();
        while(node.parents != null) {
            pathToRoot.add(node);
            node = node.parents.get(node.parents.size() - 1);
        }

        for(Node n : pathToRoot) {
            if(Collections.frequency(pathToRoot, n) >= 3) return Game.GameState.DRAW; //threefold repetition
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

        return Game.GameState.ONGOING;
    }

    private static class Node {
        List<Node> children;
        double N; //How often parent node has been visited
        double n; //How often this node has been visited
        double v; //Result of child nodes games

        List<Node> parents;
        Move move;
        Board boardState;

        Node() {
            parents = null;
            move = null;
            children = null;
            boardState = new Board();

            N = 0.0d;
            n = 0.0d;
            v = 0.0d;
        }

        Node(Node parent, Move move) {
            this.parents = new LinkedList<>();
            parents.add(parent);
            this.move = move;
            children = null;
            boardState = moveGenerator.makeMove(move, parent.boardState);

            N = 0.0d;
            n = 0.0d;
            v = 0.0d;
        }

        public String toString() {
            return move.toString();
        }
    }
}
