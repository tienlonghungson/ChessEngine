package src.model.move;

import src.model.board.ActiveBoard;
import src.position.Position;
import src.model.piece.Knight;
import src.model.piece.Pawn;
import src.model.piece.Piece;
import src.model.piece.Rook;

/**
 * Move object used to store the a chess move. Stores and moving piece, the start and end position,
 * and the captured piece if there is one.
 *
 */
public class Move implements Comparable<Move> {
    private Piece movingPiece;
    private final Position startPos;
    private final Position endPos;
    private Piece capturedPiece;
    protected ActiveBoard activeBoard;

    /**
     * Constructor for the move class
     * Constructor automatically gets the captured piece if there is one
     *
     * @param movingPiece The piece that is going to be moved
     * @param boardActions The board to be changed
     * @param endPos The end Position of the moving piece
     */
    public Move(Piece movingPiece, ActiveBoard boardActions, Position endPos) {
        this.movingPiece = movingPiece;
//        this.startPos = movingPiece.getNewPosition();
        this.startPos = movingPiece.getPosition();
        this.endPos = endPos;
        this.activeBoard = boardActions;
        this.capturedPiece = boardActions.getPiece(endPos);
    }

    /**
     * Causes the move to happen on the board
     *
     * @param isVisual Whether or not the change should happen graphically
     */
    public void doMove(boolean isVisual) {
        if(capturedPiece != null) {
            activeBoard.kill(capturedPiece, isVisual);
        }
        activeBoard.updatePosition(movingPiece, endPos, isVisual);
        activeBoard.updateZobristKey(this);
    }

    /**
     * Undoes the move on the board
     *
     * @param isVisual Whether or not the change should happen graphically
     */
    public void undoMove(boolean isVisual) {
        activeBoard.updatePosition(movingPiece, startPos, isVisual);
        activeBoard.updateZobristKey(this);
        if(capturedPiece != null) {
            activeBoard.revive(capturedPiece, isVisual);
        }
    }

    public Position getEndPos() {
        return endPos;
    }

    public Position getStartPos() {
        return startPos;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setMovingPiece(Piece movingPiece) {
        this.movingPiece = movingPiece;
    }
    void setCapturedPiece(Piece capturedPiece){
        this.capturedPiece = capturedPiece;
    }


    /**
     * @return the score of the captured piece
     */
    public int getCaptureScore() {
        if(capturedPiece != null) {
            return capturedPiece.getScore();
        }
        return 0;
    }

    /**
     * @return the score of the moving piece
     */
    public int getMovingScore() {
        return movingPiece.getMovingScore();
    }

    /**
     * Returns a String version of the move with the moving piece's id and its starting position followed by the
     * captured piece if there is one and the finishing position
     *
     * @return A String representation of this Move object
     */
    @Override
    public String toString() {
        StringBuilder moveDescription = new StringBuilder(movingPiece.getID() + " " + startPos.toString() + " -> ");
        if(capturedPiece != null) {
            moveDescription.append(capturedPiece.getID()).append(" ");
        }
        moveDescription.append(endPos.toString());
        return moveDescription.toString();
    }

    /**
     * compare the benefit of this move with other
     * @param move the compared move
     * @return the different in benefit between this move and the other
     */
    public int compareTo(Move move) {
        //1
        if(this instanceof Promotion) {
            return -1;
        } else if(move instanceof Promotion) {
            return 1;
        }

        //2
        if(this instanceof Castling) {
            return -1;
        } else if(move instanceof Castling) {
            return 1;
        }

        //3
        int captureScore = this.getCaptureScore() - move.getCaptureScore();
        if(captureScore != 0) {
            return captureScore * -1;
        }

        //4
        if(this.getMovingScore() == Rook.SCORE && activeBoard.openFile(this.getEndPos().getCol())) {
            return -1;
        } else if(move.getMovingScore() == Rook.SCORE && activeBoard.openFile(move.getEndPos().getCol())) {
            return 1;
        }

        //5
        if(this.getMovingScore() == Knight.SCORE && (Position.distanceFromMiddle(this.getEndPos()) - Position.distanceFromMiddle(this.getStartPos())) < 0) {
            return -1;
        } else if(move.getMovingScore() == Knight.SCORE && (Position.distanceFromMiddle(move.getEndPos()) - Position.distanceFromMiddle(move.getStartPos())) < 0) {
            return 1;
        }

        //6
        if(this.getMovingScore() == Pawn.SCORE) {
            return 1;
        } else if(move.getMovingScore() == Pawn.SCORE) {
            return -1;
        }

        return 0;
    }
}
