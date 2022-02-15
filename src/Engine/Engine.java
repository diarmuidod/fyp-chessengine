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
    ResultSet rs= null;

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
            if(gamesPlayed % 64 == 0) storeSearchResults();

            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(root);
            Node expandedChild = expansion(selectedChild);
            double result = rollout(expandedChild);
            root = backpropagation(expandedChild, result);

            gameLength.add(pathToRoot.size() / 2);
            System.out.println("Moves in game: " + pathToRoot.size() / 2);

            pathToRoot.clear();
            gamesPlayed++;
        }

        System.out.println("\nGames played: " + gameLength.size());

        int avgGame = 0;
        for(Integer i : gameLength) avgGame += i;
        System.out.println("Average game length: " + avgGame / gameLength.size());

        storeSearchResults();

        if (stmt != null) stmt.close();
        if(conn != null) conn.close();
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

        transpositionTable.put(Zobrist.getZobristKey(node.boardState), node);

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
        long key;
        Node node;
        String move;

        System.out.println("Start Storing: " + formatter.format(new Date(System.currentTimeMillis())));

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

        System.out.println("Finish Storing: " + formatter.format(new Date(System.currentTimeMillis())));

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
        String sql = "SELECT * FROM nodeTbl WHERE zobristKey IN (SELECT childKey FROM parentChildTbl WHERE parentKey = "
                     + Zobrist.getZobristKey(node.boardState) + ")";

        long key;
        double parentVisits, childVisits, value;
        try {
            rs = stmt.executeQuery(sql);
            boolean found;
            while(rs.next()) {
                key = rs.getLong(1);
                parentVisits = rs.getLong(2);
                childVisits = rs.getLong(3);
                value = rs.getLong(4);

                found = false;
                for(Node n : children) {
                    if(Zobrist.getZobristKey(n.boardState) == key) {
                        n.N = parentVisits;
                        n.n = childVisits;
                        n.v = value;
                        found = true;
                        break;
                    }
                }

                if(!found) System.out.println("Child of " + key + " not found.");
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
