package GUI;

import Board.Board;
import GameManager.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class UserInterface {
    private static final int squareSize = 64;
    private final int xOffset;
    private final int yOffset;

    private JFrame uiFrame;
    private JPanel boardPanel;

    private final Game chessGame;

    LinkedList<PieceUI> pieceList;
    private final Image[] pieceSprites;
    private static PieceUI activePiece = null;
    private static int activePieceX = 0;
    private static int activePieceY = 0;

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

        frame.setJMenuBar(menuBar);
        frame.add(boardPanel);

        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                if(activePiece != null) {
                    activePiece.xPos = e.getX();
                    activePiece.yPos = e.getY();
                }
                frame.repaint();
            }
        });

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(activePiece == null) {
                    activePiece = getPiece((e.getX() - xOffset) / squareSize, (e.getY() - yOffset) / squareSize);
                    if(activePiece != null) {
                        activePieceX = activePiece.xPos;
                        activePieceY = activePiece.yPos;
                    }
                } else {
                    int mouseX = (e.getX() - xOffset) / squareSize;
                    int mouseY = (e.getY() - yOffset) / squareSize;

                    if (mouseX >= 0 && mouseX <= 7 && mouseY >= 0 && mouseY <= 7) {
                        activePiece.movePiece(mouseX, mouseY);
                    } else {
                        activePiece.movePiece(activePieceX, activePieceY);
                    }
                    activePiece = null;
                }
                frame.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setTitle("Chess Application");
        try {
            frame.setIconImage(ImageIO.read(new File("src/GUI/Assets/sprites.png"))
                                      .getSubimage(620, 220, 180, 180));
        } catch (IOException ignored) {}
        frame.setBounds((screenSize.width / 2) - 264, (screenSize.height / 2) - 264, 528, 574);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem reset = generateResetButton();
        JMenuItem exit = generateExitButton();

        fileMenu.add(reset);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        menuBar.setVisible(true);
        return menuBar;
    }

    private JMenuItem generateResetButton() {
        JMenuItem item = new JMenuItem("Reset");
        item.addActionListener(e -> {
            chessGame.board = new Board();
            pieceList = generatePieceList();

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
                boolean whiteSquare = true;
                this.setBounds(0, 0, squareSize * 8, squareSize * 8);

                for(int y = 0; y < 8; y++) {
                    for(int x = 0; x < 8; x++) {
                        if (whiteSquare) {
                            g.setColor(lightSquares);
                        } else {
                            g.setColor(darkSquares);
                        }

                        g.fillRect(y * squareSize, x * squareSize, squareSize, squareSize);
                        whiteSquare = !whiteSquare;
                    }
                    whiteSquare = !whiteSquare;

                    for(PieceUI piece : pieceList) {
                        g.drawImage(pieceSprites[piece.imgIndex], piece.xPos * squareSize, piece.yPos * squareSize, this);
                    }

                    if(activePiece != null) {
                        g.drawImage(pieceSprites[activePiece.imgIndex], getMousePosition().x - (squareSize / 2), getMousePosition().y - (squareSize / 2), this);
                    }
                }
            }
        };
    }

    public Image[] generatePieceSprites() throws IOException {
        BufferedImage spriteSheet = ImageIO.read(new File("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\GUI\\Assets\\sprites.png"));
        Image[] pieceSprites = new Image[12];

        for(int y = 0; y < 2; y++) {
            for(int x = 0; x < 6; x++) {
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
                    switch(Character.toUpperCase(symbol)) {
                        case 'K':
                            index = 0;
                            break;

                        case 'Q':
                            index = 1;
                            break;

                        case 'B':
                            index = 2;
                            break;

                        case 'N':
                            index = 3;
                            break;

                        case 'R':
                            index = 4;
                            break;

                        case 'P':
                            index = 5;
                            break;
                    }

                    if(!isWhite) index += 6;
                    PieceUI piece = new PieceUI(file, rank, isWhite, index, String.valueOf(Character.toUpperCase(symbol)), pieces);
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
        return Math.abs(7 - y) * 8 + x;
    }
}
