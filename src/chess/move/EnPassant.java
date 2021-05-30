package src.chess.move;

import src.chess.Board.ActiveBoard;
import src.chess.piece.Piece;
import src.position.Position;

public class EnPassant extends Move{
    /**
     * Constructor for the move class
     * Constructor automatically gets the captured piece if there is one
     *
     * @param movingPiece  The piece that is going to be moved
     * @param boardActions The board to be changed
     * @param endPos       The end Position of the moving piece
     */
    public EnPassant(Piece movingPiece,Piece capturedPiece, ActiveBoard boardActions, Position endPos) {
        super(movingPiece,boardActions,endPos);
        this.setCapturedPiece(capturedPiece);
    }
}
