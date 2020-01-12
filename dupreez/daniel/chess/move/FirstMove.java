package dupreez.daniel.chess.move;

import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.piece.FirstMoveMatters;
import dupreez.daniel.chess.piece.Piece;
import dupreez.daniel.position.Position;

public class FirstMove extends Move
{
    FirstMoveMatters piece;

    public FirstMove(Piece movingPiece, Board board, Position endPos)
    {
        super(movingPiece, board, endPos);
        piece = (FirstMoveMatters)movingPiece;
    }

    @Override
    public void doMove(boolean isVisual)
    {
        piece.setHasMoved(true);
        super.doMove(isVisual);
    }

    @Override
    public void undoMove(boolean isVisual)
    {
        piece.setHasMoved(false);
        super.undoMove(isVisual);
    }
}
