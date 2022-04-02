package GUI;

import Board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class MenuBar extends JMenuBar {
    private MenuBar() {
        throw new UnsupportedOperationException("Stop that you.");
    }

    public MenuBar(UI ui) {
        super();
        JMenu fileMenu = new JMenu("File");

        JMenuItem flipBoard = generateFlipBoardButton(ui);
        JMenuItem copyPGN = generateCopyPGNButton(ui);
        JMenuItem reset = generateResetButton(ui);
        JMenuItem exit = generateExitButton();

        fileMenu.add(flipBoard);
        fileMenu.add(copyPGN);
        fileMenu.add(reset);
        fileMenu.add(exit);

        this.add(fileMenu);
        this.setVisible(true);
    }

    //Button to flip board orientation between black and white
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

    //Button to copy PGN to clipboard
    private JMenuItem generateCopyPGNButton(UI ui) {
        JMenuItem item = new JMenuItem("Copy PGN");
        item.addActionListener(e -> {
            ui.boardFlipped = !ui.boardFlipped;
            ui.pieceList = ui.generatePieceList();

            ui.uiFrame.getContentPane().invalidate();
            ui.uiFrame.getContentPane().validate();
            ui.uiFrame.getContentPane().repaint();
        });

        return item;
    }

    //Button to reset game, board, pgn and engine fields
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
