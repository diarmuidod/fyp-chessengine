package GUI;

import Board.Board;
import Board.Move;
import Engine.Engine;
import GameManager.Game;
import Utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class UserInterface {
    private static final int squareSize = 64;
    private static PieceUI activePiece = null;
    private static int activePieceStartX = 0;
    private static int activePieceStartY = 0;
    private final int xOffset;
    private final int yOffset;
    private final JFrame uiFrame;
    private final Game chessGame;
    private final Image[] pieceSprites;
    private final Color darkSquares = Color.decode("#769656");
    private final Color lightSquares = Color.decode("#EEEED2");
    private JPanel boardPanel;
    private JTextArea pgnField;
    private Engine chessEngine;
    private LinkedList<PieceUI> pieceList;
    private boolean boardFlipped = false;

    public UserInterface() throws IOException {
        chessEngine = loadEngine();
        chessGame = new Game("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", chessEngine);
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
        JButton copyFenButton = generateCopyFEN();
        JButton exportPgnButton = generateExportPgn();

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

        frame.setTitle("Chess Application");
        frame.setVisible(true);
        frame.setSize(new Dimension(692, 750));
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setLayout(gbl);

        gbc.ipadx = 16;
        gbc.ipady = 18;
        gbc.weightx = 0.1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(boardPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        frame.add(pgnPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 25);
        gbc.anchor = GridBagConstraints.EAST;
        frame.add(copyFenButton, gbc);

        return frame;
    }

    private Move validMove(int targetX, int targetY) {
        int startIndex = Utils.posToIndex(activePieceStartX, activePieceStartY, boardFlipped);
        int targetIndex = Utils.posToIndex(targetX, targetY, boardFlipped);

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

    private JButton generateCopyFEN() {
        JButton button = new JButton("Copy FEN String");
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(chessGame.saveGameToFEN()), null));
        return button;
    }

    private JButton generateExportPgn() {
        JButton button = new JButton("Export Pgn");
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
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
                        Point mousePosition = getMousePosition();
                        if (mousePosition != null) {
                            g.drawImage(pieceSprites[activePiece.imgIndex], mousePosition.x - (squareSize / 2), mousePosition.y - (squareSize / 2), this);
                        }
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
        BufferedImage spriteSheet = ImageIO.read(new File("src\\GUI\\Assets\\sprites.png"));
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


    public void saveEngine() {
        // Serialization
        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream("engine.ser");
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(chessEngine);

            out.close();
            file.close();

            System.out.println("Object has been serialized");

        } catch (IOException ignored) {
        }
    }

    public Engine loadEngine() {
        try {
            Engine engine;
            // Reading the object from a file
            FileInputStream file = new FileInputStream("engine.ser");
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            engine = (Engine) in.readObject();

            in.close();
            file.close();

            return engine;
        } catch (Exception ignored) {
        }

        return new Engine();
    }
}
