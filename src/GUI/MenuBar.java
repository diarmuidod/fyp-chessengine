package GUI;

import Board.Board;

import javax.swing.*;

public class MenuBar extends JMenuBar {
    private MenuBar(){
        throw new UnsupportedOperationException("Stop that you.");
    }

    public MenuBar(UI ui) {
        super();
        JMenu fileMenu = new JMenu("File");

        JMenuItem flipBoard = generateFlipBoardButton(ui);
        JMenuItem reset = generateResetButton(ui);
        JMenuItem exit = generateExitButton();

        fileMenu.add(flipBoard);
        fileMenu.add(reset);
        fileMenu.add(exit);

        this.add(fileMenu);
        this.setVisible(true);
    }

    private JMenuItem generateFlipBoardButton(UI ui) {
        JMenuItem item = new JMenuItem("Flip Board");
        item.addActionListener(e -> {
            ui.boardFlipped = !ui.boardFlipped;
            ui.pieceList = ui.generatePieceList();

            ui.uiFrame.getContentPane().invalidate();
            ui.uiFrame.getContentPane().validate();
            ui.uiFrame.getContentPane().repaint();
        });

        return item;
    }

    private JMenuItem generateResetButton(UI ui) {
        JMenuItem item = new JMenuItem("Reset");
        item.addActionListener(e -> {
            ui.chessGame.board = new Board();
            ui.pieceList = ui.generatePieceList();
            ui.chessGame.movesPlayed.clear();
            ui.pgnField.setText("");
            ui.engineDataField.setText("");

            ui.uiFrame.getContentPane().invalidate();
            ui.uiFrame.getContentPane().validate();
            ui.uiFrame.getContentPane().repaint();
        });

        return item;
    }

    private JMenuItem generateExitButton() {
        JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(e -> System.exit(0));

        return item;
    }
}
