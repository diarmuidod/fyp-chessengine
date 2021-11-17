package Engine;

import Board.Board;
import Board.Move;
import Board.MoveGenerator;
import GameManager.Game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Engine implements Serializable {
    //https://medium.com/@ishaan.gupta0401/monte-carlo-tree-search-application-on-chess-5573fc0efb75
    //https://www.geeksforgeeks.org/serialization-in-java/

    private static final MoveGenerator moveGenerator = new MoveGenerator();
    private static final Random rand = new Random();

    Node root;

    public Engine() {
        root = new Node();
    }

    public Move getBestMove(Node node, long timeInSeconds) {
        if(getGameState(node.boardState) != Game.GameState.ONGOING) return null;

        boolean timeRemaining = true;
        long startTime = System.currentTimeMillis();

        double currentUCB;
        double maxUCB = Double.NEGATIVE_INFINITY;
        double minUCB = Double.POSITIVE_INFINITY;

        if(node.children.size() == 0) {
            node.children = generateChildren(node);
        }

        while(timeRemaining) {
            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(node);
            Node expandedChild = expansion(selectedChild);
            double result = rollout(expandedChild);
            node = backpropagation(expandedChild, result);
        }

        Node bestMoveNode = null;

        for(Node child : node.children) {
            currentUCB = getUCB(child);
            if(node.boardState.whiteToMove) {
                if(currentUCB > maxUCB) {
                    currentUCB = maxUCB;
                    bestMoveNode = child;
                }
            } else {
                if(currentUCB < minUCB) {
                    currentUCB = minUCB;
                    bestMoveNode = child;
                }
            }
        }

        Move bestMove = null;

        //trust me, I hate it too
        for(Move m : moveGenerator.getLegalMoves(bestMoveNode.boardState)) {
            if(bestMoveNode.boardState.equals(moveGenerator.makeMove(m, node.boardState))) {
                bestMove = m;
            }
        }
        return Objects.requireNonNull(bestMove);
    }

    public void trainEngine(long timeInSeconds) {
        if(getGameState(root.boardState) != Game.GameState.ONGOING) return;

        boolean timeRemaining = true;
        long startTime = System.currentTimeMillis();

        if(root.children == null) {
            root.children = generateChildren(root);
        }

        while(timeRemaining) {
            timeRemaining = System.currentTimeMillis() < startTime + (timeInSeconds * 1000);

            Node selectedChild = selection(root);
            Node expandedChild = expansion(selectedChild);
            double result = rollout(expandedChild);
            root = backpropagation(expandedChild, result);

        }
    }

    public Node selection(Node node) {
        double currentUCB;
        double maxUCB = Double.NEGATIVE_INFINITY;
        double minUCB = Double.POSITIVE_INFINITY;
        Node selectedChild = null;

        if(node.children == null) node.children = generateChildren(node);

        for(Node n : node.children) {
            currentUCB = getUCB(n);

            if(node.boardState.whiteToMove) {
                if (currentUCB > maxUCB) {
                    selectedChild = n;
                }
            } else {
                if (currentUCB < minUCB) {
                    selectedChild = n;
                }
            }
        }

        return selectedChild;
    }

    public Node expansion(Node node) {
        if(node.children == null) return node;

        double currentUCB;
        double maxUCB = Double.NEGATIVE_INFINITY;
        double minUCB = Double.POSITIVE_INFINITY;

        Node currentChild = node.children.get(0);

        for(Node n : node.children) {
            currentUCB = getUCB(n);

            if(node.boardState.whiteToMove) {
                if (currentUCB > maxUCB) {
                    currentChild = n;
                }
            } else {
                if (currentUCB < minUCB) {
                    currentChild = n;
                }
            }
        }

        return expansion(Objects.requireNonNull(currentChild));
    }

    public float rollout(Node node) {
        if(getGameState(node.boardState) != Game.GameState.ONGOING) {
            if(getGameState(node.boardState) == Game.GameState.WHITE_WINS) return 1;
            if(getGameState(node.boardState) == Game.GameState.BLACK_WINS) return -1;
            if(getGameState(node.boardState) == Game.GameState.DRAW) return 0;
        }

        node.children = generateChildren(node);
        Node child = node.children.get(rand.nextInt(node.children.size()));
        return rollout(child);
    }

    public Node backpropagation(Node node, double reward) {
        node.n += 1;
        node.v += reward;

        while(node.parent != null) {
            node.N += 1;
            node = node.parent;
        }

        return node;
    }

    public double getUCB(Node node) {
        //please don't ask me to explain this
        return node.v + (2 * (Math.sqrt(Math.log(node.N + Math.exp(1) + (Math.pow(10, -6)) / node.n + Math.pow(10, -10)))));
    }

    public List<Node> generateChildren(Node node) {
        List<Node> children = new LinkedList<>();

        for(Move move : moveGenerator.getLegalMoves(node.boardState)) {
            children.add(new Node(node, move));
        }

        return children;
    }

    public Game.GameState getGameState(Board board) {
        if(board.fiftyMoveCount >= 50) {
            return Game.GameState.DRAW;
        }

        //no legal moves
        if(moveGenerator.getLegalMoves(board).size() == 0) {
            //white in check
            if (moveGenerator.kingInCheck(board, true)) {
                return Game.GameState.BLACK_WINS;
            }

            //white in check
            if(moveGenerator.kingInCheck(board, false)) {
                return Game.GameState.WHITE_WINS;
            }

            return Game.GameState.DRAW;
        }

        return Game.GameState.ONGOING;
    }

     static class Node {
        Node parent;
        List<Node> children;
        Board boardState;

        long N; //How often parent node has been visited
        long n; //How often this node has been visited
        long v; //Result of child nodes games

        public Node() {
            parent = null;
            children = null;
            boardState = new Board();

            N = 0;
            n = 0;
            v = 0;
        }

        public Node(Node parent, Move move) {
            this.parent = parent;
            children = null;
            boardState = moveGenerator.makeMove(move, parent.boardState);

            N = 0;
            n = 0;
            v = 0;
        }
    }
}
