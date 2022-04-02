package GUI;

import Board.Move;
import Utils.Utils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class MouseObserver implements MouseListener {
    UI ui;

    public MouseObserver(UI ui) {
        super();
        this.ui = ui;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //If no active piece exists, and one exists at the clicked cell, set it as the active piece
        if (UI.activePiece == null) {
            UI.activePiece = ui.getPiece((e.getX() - ui.xOffset) / UI.squareSize, (e.getY() - ui.yOffset) / UI.squareSize);
            if (UI.activePiece != null) {
                UI.activePieceStartX = UI.activePiece.xPos;
                UI.activePieceStartY = UI.activePiece.yPos;
            }
        } else {//If an active piece exists
            //if right-clicked, null the active piece having returned it to the original square
            if (SwingUtilities.isRightMouseButton(e)) {
                UI.activePiece.movePiece(UI.activePieceStartX, UI.activePieceStartY);
                UI.activePiece = null;
            //if left-clicked
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                Move move;

                int mouseX = (e.getX() - ui.xOffset) / UI.squareSize;
                int mouseY = (e.getY() - ui.yOffset) / UI.squareSize;

                //if the piece can legally move there, do so, and null the active piece
                //play the move and update the pgn and engine data accordingly
                if ((move = validMove(mouseX, mouseY)) != null) {
                    ui.chessGame.board = ui.chessGame.moveGenerator.makeMove(move, ui.chessGame.board);
                    ui.chessGame.movesPlayed.add(move);
                    ui.pieceList = ui.generatePieceList();
                    setPgnText(ui.chessGame.movesPlayed);
                    ui.updateEngineDataField();
                //if  the clicked square is the original square, set it back there
                } else if (UI.activePieceStartX == mouseX && UI.activePieceStartY == mouseY) {
                    UI.activePiece.xPos = UI.activePieceStartX;
                    UI.activePiece.yPos = UI.activePieceStartY;
                //otherwise, reset it to its original position
                } else {
                    UI.activePiece.movePiece(UI.activePieceStartX, UI.activePieceStartY);
                }
                UI.activePiece = null;
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

    //use the start and target squares clicked to match moves against the legal move list
    private Move validMove(int targetX, int targetY) {
        int startIndex = Utils.posToIndex(UI.activePieceStartX, UI.activePieceStartY, ui.boardFlipped);
        int targetIndex = Utils.posToIndex(targetX, targetY, ui.boardFlipped);

        for (Move m : ui.chessGame.getLegalMoves()) {
            if (m.startSquare == startIndex && m.targetSquare == targetIndex) {
                return m;
            }
        }

        return null;
    }

    //update the pgn field to accurately display moves played
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
