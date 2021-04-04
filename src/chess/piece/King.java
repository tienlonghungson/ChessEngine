package src.chess.piece;

import src.chess.Board.Board;
import src.chess.move.Castling;
import src.chess.move.FirstMove;
import src.chess.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class King extends Piece implements FirstMoveMatters {
    public static final int SCORE = 400;
    public static final String ID = "K";
    public static final String NAME = "King";
    private static final int[][] moveDirections = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

    public boolean hasMoved;

    public King (Position position, boolean isWhite, Board board) {
        this(position, isWhite, false, board);
    }

    public King(Position position, boolean isWhite, boolean hasMoved, Board board) {
        super(position, isWhite, board);
        this.hasMoved = hasMoved;
    }

    /**
     * @return list of {@code moves} which is valid
     */
    @Override
    public LinkedList<Move> getMoves() {
        LinkedList<Move> moves = new LinkedList<>();

        moves.addAll(getMovesNoCastle());
        moves.addAll(getMovesCastle());

        return moves;
    }

    public LinkedList<Move> getMovesNoCastle() {
        LinkedList<Move> moves = new LinkedList<>();
        final int[][] moveDirections = moveDirections();

        for (int[] direction:
             moveDirections) {
            getMovesHelper(direction[0],direction[1],moves);
        }

        return moves;
    }

    protected void getMovesHelper(int rowOffset, int colOffset, LinkedList<Move> moves) {
        Position temp = this.position.getPositionWithOffset(colOffset, rowOffset);
        if(activeBoard.isInBounds(temp) && !activeBoard.hasFriendlyPieceAtPosition(temp, isWhite)) {
            moves.add(setupMove(temp));
        }
    }

    @Override
    protected int[][] moveDirections() {
        return moveDirections;
    }

    public LinkedList<Move> getMovesCastle() {
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

    private Move setupMove(Position position) {
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

    public static King parseKing(String[] data, Board board) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);

        return new King(position, isWhite, hasMoved, board);
    }
}
