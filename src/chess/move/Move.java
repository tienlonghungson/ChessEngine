package src.chess.move;

import src.chess.Board.ActiveBoard;
import src.position.Position;
import src.chess.piece.Knight;
import src.chess.piece.Pawn;
import src.chess.piece.Piece;
import src.chess.piece.Rook;

/**
 * Move object used to store the a chess move. Stores and moving piece, the start and end position,
 * and the captured piece if there is one.
 *
 */
public class Move implements Comparable<Move> {
    private Piece movingPiece;
    private Position startPos;
    private Position endPos;
    private Piece capturedPiece;
    protected ActiveBoard activeBoard;

    public Move(){
//        this.activeBoard = boardActions;
    }

    public void setActiveBoard(ActiveBoard activeBoard) {
        this.activeBoard = activeBoard;
    }

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
        this.startPos = movingPiece.getNewPosition();
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
    }

    /**
     * Undoes the move on the board
     *
     * @param isVisual Whether or not the change should happen graphically
     */
    public void undoMove(boolean isVisual) {
        activeBoard.updatePosition(movingPiece, startPos, isVisual);
        if(capturedPiece != null) {
            activeBoard.revive(capturedPiece, isVisual);
        }
    }

    public Position getEndPosition() {
        return endPos;
    }

    public Position getStartPos() {
        return startPos;
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
        return movingPiece.getScore();
    }

    /**
     * Returns a String version of the move with the moving piece's id and its starting position followed by the
     * captured piece if there is one and the finishing position
     *
     * @return A String representation of this Move object
     */
    public String toString() {
        StringBuilder moveDescription = new StringBuilder(movingPiece.getID() + " " + startPos.toString() + " -> ");
        if(capturedPiece != null) {
            moveDescription.append(capturedPiece.getID() + " ");
        }
        moveDescription.append(endPos.toString());
        return moveDescription.toString();
    }

    public void setMovingPiece(Piece piece) {
        movingPiece = piece;
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
        if(this.getMovingScore() == Rook.SCORE && activeBoard.openFile(this.getEndPosition().getCol())) {
            return -1;
        } else if(move.getMovingScore() == Rook.SCORE && activeBoard.openFile(move.getEndPosition().getCol())) {
            return 1;
        }

        //5
        if(this.getMovingScore() == Knight.SCORE && (Position.distanceFromMiddle(this.getEndPosition()) - Position.distanceFromMiddle(this.getStartPos())) < 0) {
            return -1;
        } else if(move.getMovingScore() == Knight.SCORE && (Position.distanceFromMiddle(move.getEndPosition()) - Position.distanceFromMiddle(move.getStartPos())) < 0) {
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
