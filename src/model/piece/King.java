package src.model.piece;

import src.model.board.ActiveBoard;
import src.model.board.Board;
import src.model.move.Castling;
import src.model.move.FirstMove;
import src.model.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class King extends Piece implements FirstMoveMatters {
    public static final int POSITIONAL_SCORE[][]={
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            { 20,  20,   0,   0,   0,   0,  20,  20},
            { 20,  30,   10,  0,   0,  10,  30,  20}
    };
    public static final int SCORE = 400; //400/30000
    public static final String ID = "K";
    public static final String NAME = "King";
    private static final int[][] moveDirections = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

    public boolean hasMoved;

//    public King (Position position, boolean isWhite, Board board) {
//        this(position, isWhite, false, board);
//    }

//    public King(Position position, boolean isWhite, boolean hasMoved, Board board) {
//        super(position, isWhite);
//        this.hasMoved = hasMoved;
//    }

    public King(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite);
        this.hasMoved = hasMoved;
    }

    /**
     * @return list of {@code moves} which is valid
     * @param activeBoard where all the moves has been(can be ) executed
     */
    @Override
    public LinkedList<Move> getMoves(ActiveBoard activeBoard) {
        LinkedList<Move> moves = new LinkedList<>();

        moves.addAll(getMovesNoCastle(activeBoard));
        moves.addAll(getMovesCastle(activeBoard));

        return moves;
    }

    public LinkedList<Move> getMovesNoCastle(ActiveBoard activeBoard) {
        LinkedList<Move> moves = new LinkedList<>();
        final int[][] moveDirections = moveDirections();

        for (int[] direction:
             moveDirections) {
            getMovesHelper(direction[0],direction[1],moves,activeBoard );
        }

        return moves;
    }

    protected void getMovesHelper(int rowOffset, int colOffset, LinkedList<Move> moves, ActiveBoard activeBoard) {
        Position temp = this.position.getPositionWithOffset(colOffset, rowOffset);
        if(activeBoard.isInBounds(temp) && !activeBoard.hasFriendlyPieceAtPosition(temp, isWhite)) {
            moves.add(setupMove(temp, activeBoard));
        }
    }

    @Override
    protected int[][] moveDirections() {
        return moveDirections;
    }

    public LinkedList<Move> getMovesCastle(ActiveBoard activeBoard) {
        LinkedList<Move> moves = new LinkedList<>();

        if(!hasMoved && activeBoard.isSafeMove(this.position, isWhite)) {
            Piece leftPiece = activeBoard.getPiece(new Position(position.getRow(), 0));
            if(leftPiece instanceof Rook) {
                if (!((Rook) leftPiece).hasMoved) {
                    boolean canCastle = true;
                    for (int i = position.getCol() - 1; i > 0 && canCastle; i--) {
                        if (!activeBoard.isCleanMove(new Position(position.getRow(), i), isWhite)) {
                            canCastle = false;
                        }
                    }
                    if(canCastle) {
                        moves.add(new Castling(this, leftPiece, position.getPositionWithOffset(0, -2), position.getPositionWithOffset(0, -1), activeBoard));
                    }
                }
            }

            Piece rightPiece = activeBoard.getPiece(new Position(position.getRow(), Board.COLUMNS - 1));
            if(rightPiece instanceof Rook) {
                if (!((Rook) rightPiece).hasMoved) {
                    boolean canCastle = true;
                    for (int i = position.getCol() + 1; i < Board.COLUMNS - 1 && canCastle; i++) {
                        if (!activeBoard.isCleanMove(new Position(position.getRow(), i), isWhite)) {
                            canCastle = false;
                        }
                    }
                    if(canCastle) {
                        moves.add(new Castling(this, rightPiece, position.getPositionWithOffset(0, 2), position.getPositionWithOffset(0, 1), activeBoard));
                    }
                }
            }
        }

        return moves;
    }

    private Move setupMove(Position position, ActiveBoard activeBoard) {
        if(hasMoved) {
            return new Move(this, activeBoard, position);
        } else {
            return new FirstMove(this, activeBoard, position);
        }
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
        return isWhite?(byte)0:6;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public float[] getOneHotEncoding() {
        return isWhite? new float[]{0,0,0,0,0,0,0,0,0,0,0,1}:new float[]{0,0,0,0,0,1,0,0,0,0,0,0};
    }

    @Override
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

//    public static King parseKing(String[] data, Board board) {
//        Position position = Position.parsePosition(data[1] + data[2]);
//        boolean isWhite = Boolean.parseBoolean(data[3]);
//        boolean hasMoved = Boolean.parseBoolean(data[4]);
//
//        return new King(position, isWhite, hasMoved, board);
//    }

    public static King parseKing(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);

        return new King(position, isWhite, hasMoved);
    }
}
