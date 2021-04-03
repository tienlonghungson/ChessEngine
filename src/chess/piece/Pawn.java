package src.chess.piece;

//import com.sun.org.apache.xpath.internal.operations.Bool;
import src.chess.Board.Board;
import src.chess.Launcher;
import src.chess.move.FirstMove;
import src.chess.move.Move;
import src.chess.move.Promotion;
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

    public Pawn(Position position, boolean isWhite, Board board) {
        this(position, isWhite, false, board);
    }

    public Pawn(Position position, boolean isWhite, boolean hasMoved, Board board) {
        super(position, isWhite, board);
        this.hasMoved = hasMoved;
    }

    @Override
    public LinkedList<Move> getMoves() {
        LinkedList<Move> moves = new LinkedList<>();

        // if this pawn has been upgraded
        if(upgradePiece != null) {
            moves.addAll(upgradePiece.getMoves());
            for (Move m: moves) {
                m.setMovingPiece(this);
            }
        } else {
            Position temp;

            //Forward one
            temp = this.position.getPositionWithOffset(forward(1), 0);
            if (board.isInBounds(temp) && !board.hasPieceAtPosition(temp)) {
                moves.addAll(setupMove(temp.getPositionWithOffset()));
                //Forward two
                if (!hasMoved) {
                    temp = this.position.getPositionWithOffset(forward(2), 0);
                    if (board.isInBounds(temp) && !board.hasPieceAtPosition(temp)) {
                        moves.addAll(setupMove(temp.getPositionWithOffset()));
                    }
                }
            }
            //Capture right
            temp = this.position.getPositionWithOffset(forward(1), 1);
            if (board.isInBounds(temp) && board.hasHostilePieceAtPosition(temp, isWhite)) {
                moves.addAll(setupMove(temp.getPositionWithOffset()));
            }

            //Capture left
            temp = this.position.getPositionWithOffset(forward(1), -1);
            if (board.isInBounds(temp) && board.hasHostilePieceAtPosition(temp, isWhite)) {
                moves.addAll(setupMove(temp.getPositionWithOffset()));
            }

            //TODO Setup en passant
        }

        return moves;
    }

    private LinkedList<Move> setupMove(Position position) {
        LinkedList<Move> moves = new LinkedList<>();
        if(!hasMoved) { // if the first move has been executed
            moves.add(new FirstMove(this, board, position));
        } else if (position.getRow() == (isWhite ? 0: Board.ROWS - 1)) { // if PawnUpgradeMove has been executed
            moves.add(new Promotion(this, board, position, new Queen(position, isWhite, board)));
            moves.add(new Promotion(this, board, position, new Knight(position, isWhite, board)));
        } else {
            moves.add(new Move(this, board, position));
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
            oldImage = getImage();
            piece.setupIcon(Launcher.filePath.getAbsolutePath() + "/Resources/Chess_Pieces");
            setImage(piece.getImage());
            piece.setVisible(false);
        }
    }

    public void downgrade(boolean isVisual) {
        upgradePiece = null;
        if(isVisual) {
            setImage(oldImage);
            oldImage = null;
        }
    }

    public static Pawn parsePawn(String[] data, Board board) {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);
        //boolean hasJustMoved = Boolean.parseBoolean(data[5]);

        return new Pawn(position, isWhite, hasMoved, board);
    }
}
