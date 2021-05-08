package src.chess.piece;

import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Bishop extends Piece {
    public static final int SCORE = 3;
    public static final String ID = "B";
    public static final String NAME = "Bishop";
    private static final int[][] moveDirections = {{1,1},{-1,1},{-1,-1},{1,-1}};

    public Bishop (Position position, boolean isWhite) {
        super(position, isWhite);
    }

    protected void getMovesHelper(int rowInc, int colInc, LinkedList<Move> moves, ActiveBoard activeBoard) {
        Position temp = this.position.getPositionWithOffset(rowInc, colInc);
        while(activeBoard.isInBounds(temp)) {
            if(activeBoard.hasFriendlyPieceAtPosition(temp, isWhite)) {
                break;
            } else if(activeBoard.hasHostilePieceAtPosition(temp, isWhite)) {
                moves.add(new Move(this, activeBoard, temp.getPositionWithOffset()));
                break;
            }
            moves.add(new Move(this, activeBoard, temp.getPositionWithOffset()));
            temp = temp.getPositionWithOffset(rowInc, colInc);
        }
    }

    @Override
    protected int[][] moveDirections() {
        return moveDirections;
    }

    @Override
    public int getScore() {
        return SCORE;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static Bishop parseBishop(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);

        return new Bishop(position, isWhite);
    }
}
