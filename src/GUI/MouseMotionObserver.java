package GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionObserver implements MouseMotionListener {
    UI ui;

    public MouseMotionObserver(UI ui) {
        super();
        this.ui = ui;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (UI.activePiece != null) {
            if (ui.boardPanel.contains(e.getPoint())) {
                UI.activePiece.xPos = e.getX();
                UI.activePiece.yPos = e.getY();
            }
        }
        ui.uiFrame.getContentPane().repaint();
    }
}
