package src.model.piece;

import src.model.board.ActiveBoard;
import src.model.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Knight extends Piece {
    public static final int[][] POSITIONAL_SCORE ={
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  0, 15, 20, 20, 15,  0,-30},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  5,  5,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };
    public static final int SCORE = 320; //320//3
    public static final String ID = "Kn";
    public static final String NAME = "Knight";
    private static final int[][] moveDirections= {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};

    public Knight (Position position, boolean isWhite) {
        super(position, isWhite);
    }

    protected void getMovesHelper(int colOffset, int rowOffset, LinkedList<Move> moves, ActiveBoard activeBoard) {
        Position temp = this.position.getPositionWithOffset(colOffset, rowOffset);
        if(activeBoard.isInBounds(temp) && !activeBoard.hasFriendlyPieceAtPosition(temp, isWhite)) {
            moves.add(new Move(this, activeBoard, temp));
        }
    }

    @Override
    protected int[][] moveDirections() {
        return moveDirections ;
    }

    @Override
    public int getScore() {
        return SCORE;
    }
    @Override
    public int getMovingScore() {
        return SCORE+(isWhite?POSITIONAL_SCORE[7-position.getRow()][position.getCol()]:POSITIONAL_SCORE[position.getRow()][position.getCol()]);
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public byte getIndex() {
        return isWhite?(byte)4:10;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public float[] getOneHotEncoding() {
        return isWhite?new float[]{0,0,0,0,0,0,0,1,0,0,0,0}:new float[]{0,1,0,0,0,0,0,0,0,0,0,0};
    }

    public static Knight parseKnight(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);

        return new Knight(position, isWhite);
    }
}
