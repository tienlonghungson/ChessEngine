package src.model.move;

import src.model.board.ActiveBoard;
import src.model.piece.Piece;
import src.position.Position;

/**
 * CastleMove extends the Move object and stores a castle move
 * Castle moves involves the King and Rook moving past each other in the same move
 */
public class Castling extends FirstMove {
    private final Position startRook;
    private final Piece rook;
    private final Position endRook;

    /**
     * Constructor for the CastleMove object
     *
     * @param king in the castle move
     * @param rook in the castle move
     * @param endKing position of King after castling
     * @param endRook position of Rook after castling
     * @param activeBoard presentation board
     */
    public Castling(Piece king, Piece rook, Position endKing, Position endRook, ActiveBoard activeBoard) {
        super(king, activeBoard, endKing);
//        this.startRook = rook.getNewPosition();
        this.startRook = rook.getPosition();
        this.rook = rook;
        this.endRook = endRook;
    }

    @Override
    public void doMove(boolean isVisual) {
        super.doMove(isVisual);
        super.activeBoard.updatePosition(rook, endRook, isVisual);
    }

    @Override
    public void undoMove(boolean isVisual) {
        super.undoMove(isVisual);
        super.activeBoard.updatePosition(rook, startRook, isVisual);
    }

    public Piece getRook() {
        return rook;
    }

    public Position getStartRook() {
        return startRook;
    }

    public Position getEndRook() {
        return endRook;
    }
}
