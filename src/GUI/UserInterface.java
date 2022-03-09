package GUI;

import Engine.Engine;
import GameManager.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class UserInterface {
    public static final int squareSize = 64;
    public static PieceUI activePiece = null;
    public static int activePieceStartX = 0;
    public static int activePieceStartY = 0;
    public final int xOffset;
    public final int yOffset;
    public final JFrame uiFrame;
    public final Game chessGame;
    private final Image[] pieceSprites;
    private final Color darkSquares = Color.decode("#769656");
    private final Color lightSquares = Color.decode("#EEEED2");
    public JPanel boardPanel;
    public JTextArea pgnField;
    private Engine chessEngine;
    public LinkedList<PieceUI> pieceList;
    public boolean boardFlipped = false;

    public UserInterface() throws IOException {
        chessEngine = loadEngine();
        chessGame = new Game();
        pieceList = generatePieceList();
        pieceSprites = generatePieceSprites();

        uiFrame = generateFrame();
        xOffset = SwingUtilities.convertPoint(boardPanel, boardPanel.getX(), boardPanel.getY(), uiFrame).x;
        yOffset = SwingUtilities.convertPoint(boardPanel, boardPanel.getX(), boardPanel.getY(), uiFrame).y;
    }

    private JFrame generateFrame() {
        JFrame frame = new JFrame();
        JMenuBar menuBar = new MenuBar(this);

        boardPanel = generateBoardPanel();
        JPanel pgnPanel = generatePgnPanel();
        JButton copyFenButton = generateCopyFEN();
        JButton exportPgnButton = generateExportPgn();

        frame.setJMenuBar(menuBar);

        frame.addMouseMotionListener(new MouseMotionObserver(this));
        frame.addMouseListener(new MouseObserver(this));

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
            Engine engine = null;


            return null;
        } catch (Exception ignored) {
        }

        return new Engine();
    }
}
