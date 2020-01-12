package dupreez.daniel.chess.move;

import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.piece.Piece;
import dupreez.daniel.position.Position;

/**
 * CastleMove extends the Move object and stores a castle move
 * Castle moves involves the King and Rook moving past each other in the same move
 */
public class CastleMove extends FirstMove
{
    private Position startRook;
    private Piece rook;
    private Position endRook;

    /**
     * Constructor for the CastleMove object
     *
     * @param king
     * @param rook
     * @param endKing
     * @param endRook
     * @param board
     */
    public CastleMove(Piece king, Piece rook, Position endKing, Position endRook, Board board)
    {
        super(king, board, endKing);
        this.startRook = rook.getNewPosition();
        this.rook = rook;
        this.endRook = endRook;
    }

    @Override
    public void doMove(boolean isVisual)
    {
        super.doMove(isVisual);
        super.board.updatePosition(rook, endRook, isVisual);
    }

    @Override
    public void undoMove(boolean isVisual)
    {
        super.undoMove(isVisual);
        super.board.updatePosition(rook, startRook, isVisual);
    }
}
