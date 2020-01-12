package dupreez.daniel.chess.move;

import dupreez.daniel.chess.AI.Settings;
import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.piece.*;
import dupreez.daniel.position.Position;

/**
 * Move object used to store the a chess move. Stores and moving piece, the start and end position,
 * and the captured piece if there is one.
 *
 * @author Daniël du Preez
 */
public class Move implements Comparable<Move>
{
    private Piece movingPiece;
    private Position startPos;
    private Position endPos;
    private Piece capturedPiece;
    protected Board board;

    /**
     * Constructor for the move class
     * Constructor automatically gets the captured piece if there is one
     *
     * @param movingPiece The piece that is going to be moved
     * @param board The board to be changed
     * @param endPos The end Position of the moving piece
     */
    public Move(Piece movingPiece, Board board, Position endPos)
    {
        this.movingPiece = movingPiece;
        this.startPos = movingPiece.getNewPosition();
        this.endPos = endPos;
        this.board = board;
        this.capturedPiece = board.getPiece(endPos);
    }

    /**
     * Causes the move to happen on the board
     *
     * @param isVisual Whether or not the change should happen graphically
     */
    public void doMove(boolean isVisual)
    {
        if(capturedPiece != null)
            board.kill(capturedPiece, isVisual);
        board.updatePosition(movingPiece, endPos, isVisual);
    }

    /**
     * Undoes the move on the board
     *
     * @param isVisual Whether or not the change should happen graphically
     */
    public void undoMove(boolean isVisual)
    {
        board.updatePosition(movingPiece, startPos, isVisual);
        if(capturedPiece != null)
            board.revive(capturedPiece, isVisual);
    }

    /**
     * Returns the Position object corresponding to place the moving piece will end up
     *
     * @return The end Position object
     */
    public Position getEndPosition()
    {
        return endPos;
    }

    public Position getStartPos() {
        return startPos;
    }

    public int getCaptureScore()
    {
        if(capturedPiece != null)
            return capturedPiece.getScore();
        return 0;
    }

    public int getMovingScore()
    {
        return movingPiece.getScore();
    }

    /**
     * Returns a String version of the move with the moving piece's id and its starting position followed by the
     * captured piece if there is one and the finishing position
     *
     * @return A String representation of this Move object
     */
    public String toString()
    {
        String ret = movingPiece.getID() + " " + startPos.toString() + " -> ";
        if(capturedPiece != null)
            ret += capturedPiece.getID() + " ";
        ret += endPos.toString();
        return ret;

    }

    public void setMovingPiece(Piece piece)
    {
        movingPiece = piece;
    }

    public int compareTo(Move move)
    {
        //1
        if(this instanceof PawnUpgradeMove)
            return -1;
        else if(move instanceof PawnUpgradeMove)
            return 1;

        //2
        if(this instanceof CastleMove)
            return -1;
        else if(move instanceof CastleMove)
            return 1;

        //3
        int captureScore = this.getCaptureScore() - move.getCaptureScore();
        if(captureScore != 0)
            return captureScore * -1;

        //4
        if(this.getMovingScore() == Rook.SCORE && board.openFile(this.getEndPosition()))
            return -1;
        else if(move.getMovingScore() == Rook.SCORE && board.openFile(move.getEndPosition()))
            return 1;

        //5
        if(this.getMovingScore() == Knight.SCORE && (Position.distanceFromMiddle(this.getEndPosition()) - Position.distanceFromMiddle(this.getStartPos())) < 0)
            return -1;
        else if(move.getMovingScore() == Knight.SCORE && (Position.distanceFromMiddle(move.getEndPosition()) - Position.distanceFromMiddle(move.getStartPos())) < 0)
            return 1;

        //6
        if(this.getMovingScore() == Pawn.SCORE)
            return 1;
        else if(move.getMovingScore() == Pawn.SCORE)
            return -1;

        return 0;

    }
}
