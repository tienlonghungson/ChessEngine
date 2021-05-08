package src.chess.piece;

//import com.sun.org.apache.xpath.internal.operations.Bool;
import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.Launcher;
import src.chess.move.EnPassant;
import src.chess.move.FirstMove;
import src.chess.move.Move;
import src.chess.move.Promotion;
import src.controller.BoardController;
import src.position.Position;
import javafx.scene.image.Image;

import java.util.LinkedList;

public class Pawn extends Piece implements FirstMoveMatters {
    public static final int SCORE = 1;
    public static final String ID = "P";
    public static final String NAME = "Pawn";

    public boolean hasMoved;

    private Piece upgradePiece;
    private Image oldImage;

//    public Pawn(Position position, boolean isWhite, Board board) {
//        this(position, isWhite, false, board);
//    }

//    public Pawn(Position position, boolean isWhite, boolean hasMoved, Board board) {
//        super(position, isWhite);
//        this.hasMoved = hasMoved;
//    }

    public Pawn(Position position, boolean isWhite, boolean hasMoved) {
        super(position, isWhite);
        this.hasMoved = hasMoved;
    }

    /**
     * @return list of {@code moves} which is valid
     * @param activeBoard the board where the moves is considered
     */
    @Override
    public LinkedList<Move> getMoves(ActiveBoard activeBoard) {
        LinkedList<Move> moves = new LinkedList<>();

        // if this pawn has been upgraded
        if(upgradePiece != null) {
            moves.addAll(upgradePiece.getMoves(activeBoard));
            for (Move m: moves) {
                m.setMovingPiece(this);
            }
        } else {
            Position temp;

            //Forward one
            temp = this.position.getPositionWithOffset(forward(1), 0);
            if (activeBoard.isInBounds(temp) && !activeBoard.hasPieceAtPosition(temp)) {
                moves.addAll(setupMove(temp.getPositionWithOffset(),activeBoard));
                //Forward two
                if (!hasMoved) {
                    temp = this.position.getPositionWithOffset(forward(2), 0);
                    if (activeBoard.isInBounds(temp) && !activeBoard.hasPieceAtPosition(temp)) {
                        moves.addAll(setupMove(temp.getPositionWithOffset(),activeBoard));
                    }
                }
            }
            //Capture right
            temp = this.position.getPositionWithOffset(forward(1), 1);
            if (activeBoard.isInBounds(temp) && activeBoard.hasHostilePieceAtPosition(temp, isWhite)) {
                moves.addAll(setupMove(temp.getPositionWithOffset(),activeBoard));
            }

            //Capture left
            temp = this.position.getPositionWithOffset(forward(1), -1);
            if (activeBoard.isInBounds(temp) && activeBoard.hasHostilePieceAtPosition(temp, isWhite)) {
                moves.addAll(setupMove(temp.getPositionWithOffset(),activeBoard));
            }

            int row;
            row = isWhite ? 3:4;
            if (position.getRow()==row) {
                int catchableRow, endRow ;
                catchableRow = isWhite?1:6; endRow = (row+catchableRow)>>1;
                Move lastMove = BoardController.getLastMove();
                if (lastMove instanceof FirstMove){ // no need to check null before instance of
                    if (((FirstMove) lastMove).isPawn()) {
                        Position endPosition = lastMove.getEndPosition(), startPosition = lastMove.getStartPos();
                        if (endPosition.getRow()==row && startPosition.getRow()==catchableRow && Math.abs(position.getCol()- endPosition.getCol())==1){
                            moves.add(new EnPassant(this, activeBoard.getPiece(endPosition),activeBoard,new Position(endRow,startPosition.getCol())));
                        }
                    }
                }
            }
        }

        return moves;
    }

    @Override
    protected void getMovesHelper(int rowInc, int colInc, LinkedList<Move> moves, ActiveBoard activeBoard) {}

    @Override
    protected int[][] moveDirections() {
        return null;
    }

    private LinkedList<Move> setupMove(Position position, ActiveBoard activeBoard) {
        LinkedList<Move> moves = new LinkedList<>();
        if(!hasMoved) { // if the first move has been executed
            moves.add(new FirstMove(this, activeBoard, position));
        } else if (position.getRow() == (isWhite ? 0: Board.ROWS - 1)) { // if PawnUpgradeMove has been executed
//            moves.add(new Promotion(this, activeBoard, position, new Queen(position, isWhite, activeBoard)));
            moves.add(new Promotion(this, activeBoard, position, new Queen(position, isWhite)));
//            moves.add(new Promotion(this, activeBoard, position, new Knight(position, isWhite, activeBoard)));
            moves.add(new Promotion(this, activeBoard, position, new Knight(position, isWhite)));
            moves.add(new Promotion(this,activeBoard,position,new Rook(position,isWhite,true)));
            moves.add(new Promotion(this,activeBoard,position, new Bishop(position,isWhite)));
        } else {
            moves.add(new Move(this, activeBoard, position));
        }
        return moves;
    }

    @Override
    public int getScore() {
        if(upgradePiece != null) {
            return upgradePiece.getScore();
        } else {
            return SCORE;
        }
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

    @Override
    public void updatePosition(Position position, boolean isVisual) {
        super.updatePosition(position, isVisual);
        if(upgradePiece != null) {
            upgradePiece.updatePosition(position, false);
        }
    }

    public int forward(int steps) {
        steps = Math.abs(steps);
        if(isWhite) {
            return -1 * steps;
        } else {
            return steps;
        }
    }

    public void upgrade(Piece piece, boolean isVisual) {
        this.upgradePiece = piece;
        if(isVisual) {
            oldImage = pieceView.getImage();
            pieceView.setupIcon(Launcher.filePath.getAbsolutePath() + "/ChessPieceImages",this);
        }
    }

    public void downgrade(boolean isVisual) {
        upgradePiece = null;
        if(isVisual) {
            pieceView.setImage(oldImage);
            oldImage = null;
        }
    }

//    public static Pawn parsePawn(String[] data, Board board) {
//        Position position = Position.parsePosition(data[1] + data[2]);
//        boolean isWhite = Boolean.parseBoolean(data[3]);
//        boolean hasMoved = Boolean.parseBoolean(data[4]);
//        //boolean hasJustMoved = Boolean.parseBoolean(data[5]);
//
//        return new Pawn(position, isWhite, hasMoved, board);
//    }

    public static Pawn parsePawn(String[] data) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);
        //boolean hasJustMoved = Boolean.parseBoolean(data[5]);

        return new Pawn(position, isWhite, hasMoved);
    }
}
