package src.chess.piece;

import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.move.FirstMove;
import src.chess.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Rook extends Piece implements FirstMoveMatters {
    public static final int SCORE = 5;
    public static final String ID = "R";
    public static final String NAME = "Rook";
    private static final int[][] moveDirections =  {{1,0},{-1,0},{0,1},{0,-1}};

    public boolean hasMoved;

    public Rook (Position position, boolean isWhite, ActiveBoard activeBoard) {
        this(position, isWhite, false, activeBoard);
    }

    public Rook(Position position, boolean isWhite, boolean hasMoved, ActiveBoard activeBoard) {
        super(position, isWhite);
        this.hasMoved = hasMoved;
    }

    public Rook(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite);
        this.hasMoved = hasMoved;
    }

    protected void getMovesHelper(int rowInc, int colInc, LinkedList<Move> moves, ActiveBoard activeBoard) {
        Position temp = this.position.getPositionWithOffset(rowInc, colInc);
        while(activeBoard.isInBounds(temp)) {
            if(activeBoard.hasFriendlyPieceAtPosition(temp, isWhite)) {
                break;
            } else if (activeBoard.hasHostilePieceAtPosition(temp, isWhite)) {
                moves.add(setupMove(temp.getPositionWithOffset(),activeBoard));
                break;
            }
            moves.add(setupMove(temp.getPositionWithOffset(), activeBoard));
            temp = temp.getPositionWithOffset(rowInc, colInc);
        }
    }

    /**
     * set up the move to the position
     * @param position end position of the move
     * @param activeBoard
     * @return FirstMove object if this is the first move, Move otherwise
     */
    private Move setupMove(Position position, ActiveBoard activeBoard) {
        if(hasMoved) {
            return new Move(this, activeBoard, position);
        } else {
            return new FirstMove(this, activeBoard, position);
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

    @Override
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public static Rook parseRook(String[] data, Board board) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);

        return new Rook(position, isWhite, hasMoved, board);
    }

    public static Rook parseRook(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);

        return new Rook(position, isWhite, hasMoved);
    }
}
