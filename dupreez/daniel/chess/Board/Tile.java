package dupreez.daniel.chess.Board;

import dupreez.daniel.position.Position;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane
{
    public static final int HIGHLIGHT_WIDTH = 8;
    public static final Color HIGHLIGHT_COLOR = Color.YELLOWGREEN;
    public static final Color WARNING_COLOR = Color.RED;
    public static final Color WHITE_COLOR = Color.WHITE;
    public static final Color BLACK_COLOR = Color.DARKGRAY;

    private Position position;
    private boolean isWhite;
    private Board board;

    private Rectangle square;
    private Rectangle highlight;
    private Rectangle warning;
    private Rectangle highlightCover;

    public Tile(int row, int column, Board board)
    {
        this.board = board;
        position = new Position(row, column);

        setTranslateX(column * Board.TILE_WIDTH);
        setTranslateY(row * Board.TILE_WIDTH);

        isWhite = (row + column) % 2 == 0;

        square = new Rectangle(Board.TILE_WIDTH, Board.TILE_WIDTH);
        highlight = new Rectangle(Board.TILE_WIDTH, Board.TILE_WIDTH);
        warning = new Rectangle(Board.TILE_WIDTH, Board.TILE_WIDTH);
        highlightCover = new Rectangle(Board.TILE_WIDTH - HIGHLIGHT_WIDTH, Board.TILE_WIDTH - HIGHLIGHT_WIDTH);

        if(isWhite)
        {
            square.setFill(WHITE_COLOR);
            highlightCover.setFill(WHITE_COLOR);
        }
        else
        {
            square.setFill(BLACK_COLOR);
            highlightCover.setFill(BLACK_COLOR);
        }
        highlight.setFill(HIGHLIGHT_COLOR);
        warning.setFill(WARNING_COLOR);

        clear();

        getChildren().add(square);
        getChildren().add(highlight);
        getChildren().add(warning);
        getChildren().add(highlightCover);

        setOnMouseClicked(e -> board.clickedSquare(position));
    }

    protected void highLight()
    {
        highlight.setVisible(true);
        highlightCover.setVisible(true);
    }

    protected void warn()
    {
        warning.setVisible(true);
        highlightCover.setVisible(true);
    }

    protected void clear()
    {
        highlight.setVisible(false);
        warning.setVisible(false);
        highlightCover.setVisible(false);
    }

}
