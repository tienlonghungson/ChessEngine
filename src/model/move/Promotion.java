package src.model.move;

import src.model.board.ActiveBoard;
import src.model.piece.Pawn;
import src.model.piece.Piece;
import src.position.Position;

/**
 * Promotion extends the Move object and stores a promotion move
 * Promotion moves involves the Pawn when it comes to the end of board and upgrades
 */
public class Promotion extends Move {
    private final Piece upgradePiece;
    private final Pawn movingPiece;

    public Promotion(Piece movingPiece, ActiveBoard activeBoard, Position endPos, Piece upgradePiece) {
        super(movingPiece, activeBoard, endPos);
        this.upgradePiece = upgradePiece;
        this.movingPiece = (Pawn)movingPiece;
    }

    public void doMove(boolean isVisual) {
        movingPiece.upgrade(upgradePiece, isVisual);
        super.doMove(isVisual);
    }

    public void undoMove(boolean isVisual) {
        movingPiece.downgrade(isVisual);
        super.undoMove(isVisual);
    }

    public Piece getUpgradePiece() {
        return upgradePiece;
    }

    @Override
    public String toString() {
        return upgradePiece.getName();
    }
}
