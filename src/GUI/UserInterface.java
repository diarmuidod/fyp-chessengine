package GUI;

import Board.Board;
import Board.Move;
import GameManager.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class UserInterface {
    private static final int squareSize = 64;
    private final int xOffset;
    private final int yOffset;

    private final JFrame uiFrame;
    private JPanel boardPanel;
    private JTextArea pgnField;

    private final Game chessGame;

    private LinkedList<PieceUI> pieceList;
    private final Image[] pieceSprites;
    private boolean boardFlipped = false;

    private static PieceUI activePiece = null;
    private static int activePieceStartX = 0;
    private static int activePieceStartY = 0;

    private final Color darkSquares = Color.decode("#769656");
    private final Color lightSquares = Color.decode("#EEEED2");

    public UserInterface() throws IOException {
        chessGame = new Game();
        pieceList = generatePieceList();
        pieceSprites = generatePieceSprites();

        uiFrame = generateFrame();
        xOffset = SwingUtilities.convertPoint(boardPanel, boardPanel.getX(), boardPanel.getY(), uiFrame).x;
        yOffset = SwingUtilities.convertPoint(boardPanel, boardPanel.getX(), boardPanel.getY(), uiFrame).y;
    }

    private JFrame generateFrame() {
        JFrame frame = new JFrame();

        JMenuBar menuBar = generateMenuBar();
        boardPanel = generateBoardPanel();
        JPanel pgnPanel = generatePgnPanel();

        frame.setJMenuBar(menuBar);

        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (activePiece != null) {
                    if (boardPanel.contains(e.getPoint())) {
                        activePiece.xPos = e.getX();
                        activePiece.yPos = e.getY();
                    }
                }
                uiFrame.getContentPane().repaint();
            }
        });

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (activePiece == null) {
                    activePiece = getPiece((e.getX() - xOffset) / squareSize, (e.getY() - yOffset) / squareSize);
                    if (activePiece != null) {
                        activePieceStartX = activePiece.xPos;
                        activePieceStartY = activePiece.yPos;
                    }
                } else {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        activePiece.movePiece(activePieceStartX, activePieceStartY);
                        activePiece = null;
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        Move move;

                        int mouseX = (e.getX() - xOffset) / squareSize;
                        int mouseY = (e.getY() - yOffset) / squareSize;

                        if ((move = validMove(mouseX, mouseY)) != null) {
                            chessGame.board = chessGame.moveGenerator.makeMove(move, chessGame.board);
                            chessGame.movesPlayed.add(move);
                            pieceList = generatePieceList();
                            setPgnText(chessGame.movesPlayed);
                        } else if (activePieceStartX == mouseX && activePieceStartY == mouseY) {
                            activePiece.xPos = activePieceStartX;
                            activePiece.yPos = activePieceStartY;
                        } else {
                            activePiece.movePiece(activePieceStartX, activePieceStartY);
                        }
                        activePiece = null;
                    }
                }

                uiFrame.getContentPane().invalidate();
                uiFrame.getContentPane().validate();
                uiFrame.getContentPane().repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        try {
            frame.setIconImage(ImageIO.read(new File("src/GUI/Assets/icon2.png")));
        } catch (IOException ignored) {
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setTitle("Chess Application");
        frame.setVisible(true);
        frame.setBounds(screen.width / 2 - 512, screen.height / 2 - 384, 1024, 768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 2, 0, 0));

        frame.add(boardPanel);
        frame.add(pgnPanel);

        return frame;
    }

    private Move validMove(int targetX, int targetY) {
        int startIndex = posToIndex(activePieceStartX, activePieceStartY);
        int targetIndex = posToIndex(targetX, targetY);

        for (Move m : chessGame.getLegalMoves()) {
            if (m.startSquare == startIndex && m.targetSquare == targetIndex) {
                return m;
            }
        }
        return null;
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem flipBoard = generateFlipBoardButton();
        JMenuItem reset = generateResetButton();
        JMenuItem exit = generateExitButton();

        fileMenu.add(flipBoard);
        fileMenu.add(reset);
        fileMenu.add(exit);

        menuBar.add(fileMenu);
        menuBar.setVisible(true);
        return menuBar;
    }

    private JMenuItem generateFlipBoardButton() {
        JMenuItem item = new JMenuItem("Flip Board");
        item.addActionListener(e -> {
            boardFlipped = !boardFlipped;
            pieceList = generatePieceList();

            uiFrame.getContentPane().invalidate();
            uiFrame.getContentPane().validate();
            uiFrame.getContentPane().repaint();
        });

        return item;
    }

    private JMenuItem generateResetButton() {
        JMenuItem item = new JMenuItem("Reset");
        item.addActionListener(e -> {
            chessGame.board = new Board();
            pieceList = generatePieceList();
            chessGame.movesPlayed.clear();

            uiFrame.getContentPane().invalidate();
            uiFrame.getContentPane().validate();
            uiFrame.getContentPane().repaint();
        });

        return item;
    }

    private JMenuItem generateExitButton() {
        JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(e -> System.exit(0));

        return item;
    }

    private JPanel generateBoardPanel() {
        return new JPanel() {
            @Override
            public void paint(Graphics g) {
                boolean whiteSquare = !boardFlipped;
                this.setBounds(0, 0, squareSize * 8, squareSize * 8);

                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        if (whiteSquare) {
                            g.setColor(lightSquares);
                        } else {
                            g.setColor(darkSquares);
                        }

                        g.fillRect(y * squareSize, x * squareSize, squareSize, squareSize);
                        whiteSquare = !whiteSquare;
                    }
                    whiteSquare = !whiteSquare;

                    for (PieceUI piece : pieceList) {
                        g.drawImage(pieceSprites[piece.imgIndex], piece.xPos * squareSize, piece.yPos * squareSize, this);
                    }

                    if (activePiece != null) {
                        g.drawImage(pieceSprites[activePiece.imgIndex], getMousePosition().x - (squareSize / 2), getMousePosition().y - (squareSize / 2), this);
                    }
                }
            }
        };
    }

    private JPanel generatePgnPanel() {
        JPanel pgnPanel = new JPanel();
        pgnField = new JTextArea();

        pgnField.setSize(squareSize * 2, squareSize * 8);
        pgnField.setRows(30);
        pgnField.setColumns(12);
        pgnField.setEditable(false);
        pgnField.setVisible(true);

        JScrollPane pgnScroll = new JScrollPane(pgnField);
        pgnScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        pgnPanel.add(pgnScroll);
        return pgnPanel;
    }

    private void setPgnText(List<Move> movesPlayed) {
        StringBuilder pgn = new StringBuilder();
        int index = 0;
        for (Move m : movesPlayed) {
            if (index % 2 == 0) {
                pgn.append((index) / 2 + 1).append(". ").append(m.move).append("\t");
            } else {
                pgn.append(m.move).append("\n");
            }
            index++;
        }

        pgnField.setText(pgn.toString());
    }

    public Image[] generatePieceSprites() throws IOException {
        BufferedImage spriteSheet = ImageIO.read(new File("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\GUI\\Assets\\sprites.png"));
        Image[] pieceSprites = new Image[12];

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 6; x++) {
                pieceSprites[(y * 6) + x] = spriteSheet.getSubimage(x * 200, y * 200, 200, 200)
                        .getScaledInstance(squareSize, squareSize, BufferedImage.SCALE_SMOOTH);
            }
        }

        return pieceSprites;
    }

    public LinkedList<PieceUI> generatePieceList() {
        LinkedList<PieceUI> pieces = new LinkedList<>();
        String fen = chessGame.saveGameToFEN().split(" ")[0];
        int file = 0, rank = 0;
        boolean isWhite;

        for (int i = 0; i < fen.length(); i++) {
            char symbol = fen.charAt(i);
            if (symbol == '/') {
                file = 0;
                rank++;
            } else {
                if (Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    isWhite = Character.isUpperCase(symbol);
                    int index = -1;
                    char c = Character.toUpperCase(symbol);
                    if (c == 'K') {
                        index = 0;
                    } else if (c == 'Q') {
                        index = 1;
                    } else if (c == 'B') {
                        index = 2;
                    } else if (c == 'N') {
                        index = 3;
                    } else if (c == 'R') {
                        index = 4;
                    } else if (c == 'P') {
                        index = 5;
                    }

                    if (!isWhite) index += 6;
                    PieceUI piece = new PieceUI(file, Math.abs(7 - rank), isWhite, boardFlipped, index, String.valueOf(Character.toUpperCase(symbol)), pieces);
                    file++;
                }
            }
        }

        return pieces;
    }

    public PieceUI getPiece(int xPos, int yPos) {
        for (PieceUI piece : pieceList) {
            if (xPos == piece.xPos && yPos == piece.yPos) {
                return piece;
            }
        }

        return null;
    }

    public int posToIndex(int x, int y) {
        return boardFlipped ? y * 8 + x : Math.abs(y - 7) * 8 + x;
    }
}
