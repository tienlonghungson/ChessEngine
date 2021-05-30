package src.model.move;

import src.model.board.ActiveBoard;
import src.model.piece.FirstMoveMatters;
import src.model.piece.Pawn;
import src.model.piece.Piece;
import src.position.Position;

public class FirstMove extends Move {
    FirstMoveMatters piece;

    public FirstMove(Piece movingPiece, ActiveBoard board, Position endPos) {
        super(movingPiece, board, endPos);
        piece = (FirstMoveMatters)movingPiece;
    }

    @Override
    public void doMove(boolean isVisual) {
        piece.setHasMoved(true);
        super.doMove(isVisual);
    }

    @Override
    public void undoMove(boolean isVisual) {
        piece.setHasMoved(false);
        super.undoMove(isVisual);
    }

    public boolean isPawn(){
        return (piece instanceof Pawn);
    }
}
