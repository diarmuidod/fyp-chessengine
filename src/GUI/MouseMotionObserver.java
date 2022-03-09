package GUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionObserver implements MouseMotionListener {
    UserInterface ui;

    public MouseMotionObserver(UserInterface ui) {
        super();
        this.ui = ui;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (ui.activePiece != null) {
            if (ui.boardPanel.contains(e.getPoint())) {
                ui.activePiece.xPos = e.getX();
                ui.activePiece.yPos = e.getY();
            }
        }
        ui.uiFrame.getContentPane().repaint();
    }
}
