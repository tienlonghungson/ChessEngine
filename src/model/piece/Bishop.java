package src.model.piece;

import src.model.board.ActiveBoard;
import src.model.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Bishop extends Piece {
    public static final int POSITIONAL_SCORE[][]={{-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };
    public static final int SCORE = 3;//330/3
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
//    @Override
//    public int getScore() {
//        return SCORE+(isWhite?POSITIONAL_SCORE[7-position.getRow()][position.getCol()]:POSITIONAL_SCORE[position.getRow()][position.getCol()]);
//    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public byte getIndex() {
        return isWhite?(byte)3:9;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public float[] getOneHotEncoding() {
        return isWhite?new float[]{0,0,0,0,0,0,0,0,1,0,0,0}:new float[]{0,0,1,0,0,0,0,0,0,0,0,0};
    }

    public static Bishop parseBishop(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);

        return new Bishop(position, isWhite);
    }
}
