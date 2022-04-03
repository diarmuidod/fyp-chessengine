package GUI;

import Engine.Engine;
import GameManager.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class UI {
    public static final int squareSize = 64;
    public static PieceUI activePiece = null;
    public static int activePieceStartX = 0;
    public static int activePieceStartY = 0;
    public static Engine engine;
    public final JFrame uiFrame;
    public final int xOffset;
    public final int yOffset;
    public final Game chessGame;
    private final Image[] pieceSprites;
    private final Color darkSquares = Color.decode("#769656");
    private final Color lightSquares = Color.decode("#EEEED2");
    public JPanel mainPanel;
    public JPanel dataPanel;
    public JPanel boardPanel;
    public JPanel buttonsPanel;
    public JTextArea pgnField;
    public JTextArea engineDataField;
    public LinkedList<PieceUI> pieceList;
    public boolean boardFlipped = false;

    public boolean toggleEngine = false;

    public UI() throws IOException {
        engine = new Engine();
        chessGame = new Game();
        pieceList = generatePieceList();
        pieceSprites = generatePieceSprites();

        uiFrame = generateFrame();
        updateEngineDataField();

        //offset for making piece interaction pixel accurate
        xOffset = SwingUtilities.convertPoint(boardPanel, boardPanel.getX(), boardPanel.getY(), uiFrame).x;
        yOffset = SwingUtilities.convertPoint(boardPanel, boardPanel.getX(), boardPanel.getY(), uiFrame).y;
    }

    private JFrame generateFrame() {
        JFrame frame = new JFrame();
        JMenuBar menuBar = new MenuBar(this);

        boardPanel = generateBoardPanel();
        JPanel pgnPanel = generatePgnPanel();
        JPanel engineDataPanel = generateEngineDataPanel();

        buttonsPanel = new JPanel(new BorderLayout(5, 5));

        JButton copyFenButton = generateCopyFEN();
        JButton startEngineTrainingButton = startEngineTraining();
        JButton stopEngineTrainingButton = generateUpdateEngineVariations();

        buttonsPanel.add(copyFenButton, BorderLayout.NORTH);
        buttonsPanel.add(startEngineTrainingButton, BorderLayout.CENTER);
        buttonsPanel.add(stopEngineTrainingButton, BorderLayout.SOUTH);

        dataPanel = new JPanel();
        dataPanel.setLayout(new BorderLayout(0, 5));
        dataPanel.add(engineDataPanel, BorderLayout.NORTH);
        dataPanel.add(pgnPanel, BorderLayout.CENTER);
        dataPanel.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setJMenuBar(menuBar);

        frame.addMouseMotionListener(new MouseMotionObserver(this));
        frame.addMouseListener(new MouseObserver(this));

        try {
            //set application icon
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon2.png"))));
        } catch (IOException ignored) {}

        frame.setTitle("Chess Application");
        frame.setVisible(true);
        frame.setSize(new Dimension(720, 576));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(boardPanel, BorderLayout.WEST);
        mainPanel.add(dataPanel, BorderLayout.EAST);

        frame.add(mainPanel);
        return frame;
    }

    private JButton generateCopyFEN() {
        JButton button = new JButton("Copy FEN String");
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(chessGame.saveGameToFEN()), null));
        return button;
    }

    private JButton startEngineTraining() {
        JButton button = new JButton("Train Engine (60s)");
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(e -> {
            try {
                engine.trainEngine(60, engine.findMoveNode(chessGame.movesPlayed));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return button;
    }

    private JButton generateUpdateEngineVariations() {
        JButton button = new JButton("Update Variations");
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(e -> updateEngineDataField());

        return button;
    }

    //draw board and pieces
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

    private JPanel generateEngineDataPanel() {
        JPanel engineDataPanel = new JPanel();
        engineDataField = new JTextArea();

        engineDataField.setSize(squareSize * 2, squareSize * 8);
        engineDataField.setRows(6);
        engineDataField.setColumns(12);
        engineDataField.setEditable(false);
        engineDataField.setVisible(true);

        engineDataPanel.add(engineDataField);
        return engineDataPanel;
    }

    //split piece sprite sheet and load into array for later
    public Image[] generatePieceSprites() throws IOException {
        BufferedImage spriteSheet = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResource("sprites.png")));
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

    public void updateEngineDataField() {
        List<LinkedList<String>> variations = engine.getBestVariations(chessGame, 5, 5);

        engineDataField.setText("");
        for (LinkedList<String> l : variations) {
            StringBuilder moveList = new StringBuilder();
            for (int i = 0; i < l.size(); i++) {
                String m = l.get(i);

                if (i % 2 == 0) {
                    moveList.append(chessGame.movesPlayed.size() + (i / 2) + 1).append(". ");
                    moveList.append(m).append(" ");
                } else {
                    moveList.append(m).append(" ");
                }
            }
            engineDataField.append(moveList + "\n");

        }
    }
}
