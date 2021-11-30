package GUI;

import java.util.Iterator;
import java.util.LinkedList;

public class PieceUI {
    int xPos;
    int yPos;
    boolean isWhite;
    String id;

    LinkedList<PieceUI> pieceList;

    public PieceUI(int xPos, int yPos, boolean isWhite, String id, LinkedList<PieceUI> pieceList) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.isWhite = isWhite;
        this.id = id;
        this.pieceList = pieceList;

        pieceList.add(this);
    }

    public void movePiece(int xPos, int yPos) {
        PieceUI pieceToRemove = null;
        for (PieceUI piece : pieceList) {
            if (piece.xPos == xPos && piece.yPos == yPos) {
                pieceToRemove = piece;
                break;
            }
        }

        pieceList.remove(pieceToRemove);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void remove() {
        pieceList.remove(this);
    }
}
