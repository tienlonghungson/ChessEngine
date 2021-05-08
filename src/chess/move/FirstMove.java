package src.chess.move;

import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.piece.FirstMoveMatters;
import src.chess.piece.Pawn;
import src.chess.piece.Piece;
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
