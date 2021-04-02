package src.chess.move;

import src.chess.Board.Board;
import src.chess.piece.Pawn;
import src.chess.piece.Piece;
import src.position.Position;

/**
 * Promotion extends the Move object and stores a promotion move
 * Promotion moves involves the Pawn when it comes to the end of board and upgrades
 */
public class Promotion extends Move {
    private Piece upgradePiece;
    private Pawn movingPiece;

    public Promotion(Piece movingPiece, Board board, Position endPos, Piece upgradePiece) {
        super(movingPiece, board, endPos);
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

    @Override
    public String toString() {
        return upgradePiece.getName();
    }
}
