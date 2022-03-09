package GUI;

import Board.Move;
import Utils.Utils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class MouseObserver implements MouseListener {
    UserInterface ui;
    public MouseObserver(UserInterface ui) {
        super();
        this.ui = ui;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (ui.activePiece == null) {
            ui.activePiece = ui.getPiece((e.getX() - ui.xOffset) / ui.squareSize, (e.getY() - ui.yOffset) / ui.squareSize);
            if (ui.activePiece != null) {
                ui.activePieceStartX = ui.activePiece.xPos;
                ui.activePieceStartY = ui.activePiece.yPos;
            }
        } else {
            if (SwingUtilities.isRightMouseButton(e)) {
                ui.activePiece.movePiece(ui.activePieceStartX, ui.activePieceStartY);
                ui.activePiece = null;
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                Move move;

                int mouseX = (e.getX() - ui.xOffset) / ui.squareSize;
                int mouseY = (e.getY() - ui.yOffset) / ui.squareSize;

                if ((move = validMove(mouseX, mouseY)) != null) {
                    ui.chessGame.board = ui.chessGame.moveGenerator.makeMove(move, ui.chessGame.board);
                    ui.chessGame.movesPlayed.add(move);
                    ui.pieceList = ui.generatePieceList();
                    setPgnText(ui.chessGame.movesPlayed);
                } else if (ui.activePieceStartX == mouseX && ui.activePieceStartY == mouseY) {
                    ui.activePiece.xPos = ui.activePieceStartX;
                    ui.activePiece.yPos = ui.activePieceStartY;
                } else {
                    ui.activePiece.movePiece(ui.activePieceStartX, ui.activePieceStartY);
                }
                ui.activePiece = null;
            }
        }

        ui.uiFrame.getContentPane().invalidate();
        ui.uiFrame.getContentPane().validate();
        ui.uiFrame.getContentPane().repaint();
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

    private Move validMove(int targetX, int targetY) {
        int startIndex = Utils.posToIndex(ui.activePieceStartX, ui.activePieceStartY, ui.boardFlipped);
        int targetIndex = Utils.posToIndex(targetX, targetY, ui.boardFlipped);

        for (Move m : ui.chessGame.getLegalMoves()) {
            if (m.startSquare == startIndex && m.targetSquare == targetIndex) {
                return m;
            }
        }
        return null;
    }

    public void setPgnText(List<Move> movesPlayed) {
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

        ui.pgnField.setText(pgn.toString());
    }
}
