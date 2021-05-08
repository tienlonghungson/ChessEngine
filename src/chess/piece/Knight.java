package src.chess.piece;

import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Knight extends Piece {
    public static final int SCORE = 3;
    public static final String ID = "Kn";
    public static final String NAME = "Knight";
    private static final int[][] moveDirections= {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};

//    public Knight (Position position, boolean isWhite, ActiveBoard activeBoard) {
//        super(position, isWhite);
//    }

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
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

//    public static Knight parseKnight(String[] data, Board board) {
//        Position position = Position.parsePosition(data[1] + data[2]);
//        boolean isWhite = Boolean.parseBoolean(data[3]);
//
//        return new Knight(position, isWhite, board);
//    }

    public static Knight parseKnight(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);

        return new Knight(position, isWhite);
    }
}
