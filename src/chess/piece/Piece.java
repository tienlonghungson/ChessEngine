package src.chess.piece;

import src.chess.Board.Board;
import src.chess.Board.ActiveBoard;
import src.chess.move.Move;
import src.position.Position;
import src.position.PositionMap;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public abstract class Piece extends ImageView {
    public static final long TRANSITION_TIME = 400; //400

    protected boolean isWhite;
    protected boolean isDead;
    protected Position position;
    protected ActiveBoard activeBoard;

    public Piece(Position position, boolean isWhite, ActiveBoard boardActions) {
        this.position = position;
        this.isWhite = isWhite;
        this.activeBoard = boardActions;
        this.isDead = false;
    }

    /**
     *
     * @return the list of possible moves (including valid moves and moves can cause the king to be unsafe)
     */
    public LinkedList<Move> getMoves(){
        LinkedList<Move> moves = new LinkedList<>();
        final int[][] moveDirections = moveDirections();
        for (int[] direction:
             moveDirections) {
            getMovesHelper(direction[0],direction[1],moves);
        }
        return moves;
    }

    /**
     * check if the moves in direction determine by {@code (rowInc,colInc)} can be executed
     * @param rowInc x coordinate of direction
     * @param colInc y coordinate of direction
     * @param moves contains the moves if they are valid
     */
    protected abstract void getMovesHelper(int rowInc, int colInc, LinkedList<Move> moves);

    /**
     *
     * @return the array identifies direction of possible moves
     */
    protected abstract int[][] moveDirections();

    /**
     * valid moves are list of possible moves excluding moves cause the king to be unsafe
     * @return list of valid move
     */
    public LinkedList<Move> getValidMoves() {
        LinkedList<Move> validMoves = new LinkedList<>();
        LinkedList<Move> allMoves = getMoves();

        Piece king = activeBoard.getKing(isWhite);

        for (Move move: allMoves) {
            move.doMove(false);
            // check if this move cause the king to be unsafe
            if(activeBoard.isSafeMove(king.getPosition(), isWhite)) {
                validMoves.add(move);
            }
            move.undoMove(false);
        }

        return validMoves;
    }

    public abstract int getScore();

    public abstract String getID();

    public abstract String getName();

    public boolean isDead() {
        return isDead;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Position getNewPosition() {
        return position.getPositionWithOffset();
    }

    public Position getPosition() {
        return this.position;
    }

    // Image View
    public void updatePosition(Position position, boolean isVisual) {
        this.position = position;
        if(isVisual) {
            TranslateTransition tf = new TranslateTransition(Duration.millis(TRANSITION_TIME), this);
            tf.setToX(position.getCol() * Board.TILE_WIDTH);
            tf.setToY(position.getRow() * Board.TILE_WIDTH);
            tf.play();
        }
    }

    // Image View
    public void kill(boolean isVisual) {
        this.isDead = true;
        if(isVisual) {
            setVisible(false);
        }
    }

    // Image View
    public void revive(boolean isVisual) {
        this.isDead = false;
        if(isVisual) {
            setVisible(true);
        }
    }

    // Image View
    public void setupIcon(String filePath) {
        filePath += isWhite() ? "/White_" : "/Black_";
        filePath += getName() + ".png";

        try {
            Image image = new Image(new FileInputStream(filePath));
            setImage(image);
            setFitHeight(Board.TILE_WIDTH);
            setFitWidth(Board.TILE_WIDTH);
            setPreserveRatio(true);

            setTranslateX(position.getCol() * Board.TILE_WIDTH);
            setTranslateY(position.getRow() * Board.TILE_WIDTH);
            setOnMouseClicked(e -> activeBoard.clickedSquare(position));
        } catch(FileNotFoundException e) {
            System.err.printf("NO SUCH FILE \"%s\"%n", filePath);
        }
    }

    public static int distanceFromMiddle(Position position) {
        //TODO Dynamic Middle
        return (int) Math.hypot(3.5 - position.getRow(), 3.5 - position.getCol());
    }

    public static Piece parsePiece(String rawData, Board board) {
        rawData = rawData.substring(1);
        String[] data = rawData.split("[\\W]+");

        return switch (data[0]) {
            case "P" -> Pawn.parsePawn(data, board);
            case "R" -> Rook.parseRook(data, board);
            case "Kn" -> Knight.parseKnight(data, board);
            case "B" -> Bishop.parseBishop(data, board);
            case "Q" -> Queen.parseQueen(data, board);
            case "K" -> King.parseKing(data, board);
            default -> null;
        };

    }

    public static PositionMap<Move> getMoveMap(LinkedList<Move> moves) {
        PositionMap<Move> pMap = new PositionMap<>(moves.size());
        for (Move m: moves) {
            pMap.add(m.getEndPosition(), m);
        }
        return pMap;
    }
}
