package GUI;

import java.util.LinkedList;

public class PieceUI {
    int xPos;
    int yPos;
    boolean isWhite;
    int imgIndex;
    String id;

    LinkedList<PieceUI> pieceList;

    public PieceUI(int xPos, int yPos, boolean isWhite, boolean boardFlipped, int imgIndex, String id, LinkedList<PieceUI> pieceList) {
        this.xPos = xPos;
        this.yPos = boardFlipped ? yPos : Math.abs(yPos - 7);
        this.isWhite = isWhite;
        this.imgIndex = imgIndex;
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

        if (pieceToRemove != null) {
            pieceToRemove.remove();
        }

        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void remove() {
        pieceList.remove(this);
    }
}
