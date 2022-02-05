package Engine;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import Board.Zobrist;
import GameManager.Game;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;


public class Engine {
    //https://medium.com/@ishaan.gupta0401/monte-carlo-tree-search-application-on-chess-5573fc0efb75

    Connection conn = null;
    Statement stmt = null;
    LinkedList<Integer> gameLength = new LinkedList<>();
    private static final MoveGenerator moveGenerator = new MoveGenerator();
    private static final Random rand = new Random();
    private static Hashtable<Long, Node> transpositionTable;
    LinkedList<Node> pathToRoot;

    public Node root;

    public Engine() {
        Zobrist.readRandomNumbers();
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

    public void trainEngine(long timeInSeconds) throws SQLException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        System.out.println("Start Training: " + formatter.format(date));
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

            gameLength.add(pathToRoot.size() /2);
            System.out.println("Moves in game: " + pathToRoot.size() / 2);

            pathToRoot.clear();
        }

        date = new Date(System.currentTimeMillis());
        System.out.println("\nGames played: " + gameLength.size());

        int avgGame = 0;
        for(Integer i : gameLength) avgGame += i;
        System.out.println("Average game length: " + avgGame / gameLength.size());

        System.out.println("Start Storing: " + formatter.format(date));
        storeSearchResults();
    }

    public Node selection(Node node) {
        if(!pathToRoot.contains(node)) pathToRoot.add(node);

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
        if(!pathToRoot.contains(node)) pathToRoot.add(node);

        if (node.children == null) return node;
        if (node.children.size() == 0) return node;
        if (getGameState(node) != Game.GameState.ONGOING) return node;

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
        if(!pathToRoot.contains(node)) pathToRoot.add(node);

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

        if(!transpositionTable.contains(node)) transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);

        while (true) {
            node.N += 1;
            if (node.parents == null) {
                return node;
            }

            node = node.parents.get(node.parents.size() - 1);
        }
    }

    public double getUCB(Node node) {
        return node.v + Math.sqrt(2) * Math.sqrt(Math.log(Math.max(node.N, 1)) / Math.max(node.n, 1));
    }

    public void storeSearchResults() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chessdb", "root", "");
        stmt = conn.createStatement();

        long key;
        Node node;
        String move;

        for (Entry<Long, Node> entry : transpositionTable.entrySet()) {
            key = entry.getKey();
            node = entry.getValue();
            move = node.move.toString();

            String sql = "INSERT INTO nodeTbl (zobristKey, parentVisits, childVisits, nodeValue) " +
                         "VALUES (" + key + ", " + node.N + ", " + node.n + ", " + node.v + ") " +
                         "ON DUPLICATE KEY UPDATE " +
                         "parentVisits = parentVisits + " + node.N + "," +
                         "childVisits = childVisits + " + node.n + "," +
                         "nodeValue = nodeValue + " + node.v;

            stmt.execute(sql);

            if (node.parents == null) continue;

            for (Node parent : node.parents) {
                long parentKey = Zobrist.getZobristKey(parent.boardState);
                stmt.executeUpdate("INSERT IGNORE INTO parentChildTbl VALUES (" + parentKey + ", " + key + ", \"" + move + "\")");
            }
        }

        if (stmt != null) stmt.close();
        if(conn != null) conn.close();

        root = new Node();
        transpositionTable.clear();
        transpositionTable.put(Zobrist.getZobristKey(root.boardState), root);
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
                if(!exists.parents.contains(node)) exists.parents.add(node);
                toRemove.add(child);
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

        for (Node n : pathToRoot) {
            if (Collections.frequency(pathToRoot, Zobrist.getZobristKey(n.boardState)) >= 3)
                return Game.GameState.DRAW; //threefold repetition
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

    public class Node {
        public List<Node> children;
        double N; //How often parent node has been visited
        double n; //How often this node has been visited
        double v; //Result of child nodes games

        List<Node> parents;
        Move move;
        Board boardState;

        Node() {
            parents = null;
            move = new Move();
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
