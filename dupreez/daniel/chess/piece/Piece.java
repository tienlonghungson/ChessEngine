package dupreez.daniel.chess.piece;

import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.move.Move;
import dupreez.daniel.position.Position;
import dupreez.daniel.position.PositionMap;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public abstract class Piece extends ImageView
{
    public static final long TRANSITION_TIME = 400; //400

    protected boolean isWhite;
    protected boolean isDead;
    protected Position position;
    protected Board board;

    public Piece(Position position, boolean isWhite, Board board)
    {
        this.position = position;
        this.isWhite = isWhite;
        this.board = board;
        this.isDead = false;
    }

    public abstract LinkedList<Move> getMoves();

    public LinkedList<Move> getValidMoves()
    {
        LinkedList<Move> validMoves = new LinkedList<>();
        LinkedList<Move> allMoves = getMoves();

        Piece king = board.getKing(isWhite);

        for (Move move: allMoves)
        {
            move.doMove(false);
            if(board.isSafeMove(king.getPosition(), isWhite))
                validMoves.add(move);
            move.undoMove(false);
        }

        return validMoves;
    }

    public abstract int getScore();

    public abstract String getID();

    public abstract String getName();

    public boolean isDead()
    {
        return isDead;
    }

    public boolean isWhite()
    {
        return isWhite;
    }

    public Position getNewPosition()
    {
        return position.getPosition();
    }

    public Position getPosition()
    {
        return this.position;
    }

    public void updatePosition(Position position, boolean isVisual)
    {
        this.position = position;
        if(isVisual)
        {
            TranslateTransition tf = new TranslateTransition(Duration.millis(TRANSITION_TIME), this);
            tf.setToX(position.getCol() * Board.TILE_WIDTH);
            tf.setToY(position.getRow() * Board.TILE_WIDTH);
            tf.play();
        }
    }

    public void kill(boolean isVisual)
    {
        this.isDead = true;
        if(isVisual)
            setVisible(false);
    }

    public void revive(boolean isVisual)
    {
        this.isDead = false;
        if(isVisual)
            setVisible(true);
    }

    public void setupIcon(String filePath)
    {
        filePath += isWhite() ? "/White_" : "/Blue_";
        filePath += getName() + ".png";

        try
        {
            Image image = new Image(new FileInputStream(filePath));
            setImage(image);
            setFitHeight(Board.TILE_WIDTH);
            setFitWidth(Board.TILE_WIDTH);
            setPreserveRatio(true);

            setTranslateX(position.getCol() * Board.TILE_WIDTH);
            setTranslateY(position.getRow() * Board.TILE_WIDTH);
            setOnMouseClicked(e -> {
                board.clickedSquare(position);
            });
        }
        catch(FileNotFoundException e)
        {
            System.err.printf("NO SUCH FILE \"%s\"%n", filePath);
        }
    }

    public static int distanceFromMiddle(Position position)
    {
        //TODO Dynamic Middle
        return (int) Math.hypot(3.5 - position.getRow(), 3.5 - position.getCol());
    }

    public static Piece parsePiece(String rawData, Board board)
    {
        rawData = rawData.substring(1);
        String[] data = rawData.split("[\\W]+");

        switch (data[0])
        {
            case "P":
                return Pawn.parsePawn(data, board);
            case "R":
                return Rook.parseRook(data, board);
            case "Kn":
                return Knight.parseKnight(data, board);
            case "B":
                return Bishop.parseBishop(data, board);
            case "Q":
                return Queen.parseQueen(data, board);
            case "K":
                return King.parseKing(data, board);
        }

        return null;
    }

    public static PositionMap<Move> getMoveMap(LinkedList<Move> moves)
    {
        PositionMap<Move> pMap = new PositionMap<>(moves.size());
        for (Move m: moves)
            pMap.add(m.getEndPosition(), m);
        return pMap;
    }
}
