package dupreez.daniel.chess.move;

import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.piece.Pawn;
import dupreez.daniel.chess.piece.Piece;
import dupreez.daniel.position.Position;

public class PawnUpgradeMove extends Move
{
    private Piece upgradePiece;
    private Pawn movingPiece;

    public PawnUpgradeMove(Piece movingPiece, Board board, Position endPos, Piece upgradePiece)
    {
        super(movingPiece, board, endPos);
        this.upgradePiece = upgradePiece;
        this.movingPiece = (Pawn)movingPiece;
    }

    public void doMove(boolean isVisual)
    {
        movingPiece.upgrade(upgradePiece, isVisual);
        super.doMove(isVisual);
    }

    public void undoMove(boolean isVisual)
    {
        movingPiece.downgrade(isVisual);
        super.undoMove(isVisual);
    }

    @Override
    public String toString()
    {
        return upgradePiece.getName();
    }
}
