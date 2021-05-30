package src.view.board;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Spot extends StackPane {
    public final int HIGHLIGHT_WIDTH = 8;
    public final Color HIGHLIGHT_COLOR = Color.YELLOWGREEN;
    public final Color WARNING_COLOR = Color.RED;
    public final Color WHITE_COLOR = Color.WHITE;
    public final Color BLACK_COLOR = Color.DARKGRAY;

    private final Rectangle highlight;
    private final Rectangle warning;
    private final Rectangle highlightCover;

    public Spot(int row, int column) {

        setTranslateX(column * ActiveBoardView.SPOT_WIDTH);
        setTranslateY(row * ActiveBoardView.SPOT_WIDTH);

        boolean isWhite = (row + column) % 2 == 0;

        Rectangle square = new Rectangle(ActiveBoardView.SPOT_WIDTH, ActiveBoardView.SPOT_WIDTH);
        highlight = new Rectangle(ActiveBoardView.SPOT_WIDTH, ActiveBoardView.SPOT_WIDTH);
        warning = new Rectangle(ActiveBoardView.SPOT_WIDTH, ActiveBoardView.SPOT_WIDTH);
        highlightCover = new Rectangle(ActiveBoardView.SPOT_WIDTH - HIGHLIGHT_WIDTH, ActiveBoardView.SPOT_WIDTH - HIGHLIGHT_WIDTH);

        if(isWhite) {
            square.setFill(WHITE_COLOR);
            highlightCover.setFill(WHITE_COLOR);
        } else {
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
    }

    /**
     * allow highlight
     */
    protected void highLight() {
        highlight.setVisible(true);
        highlightCover.setVisible(true);
    }

    /**
     * allow warning
     */
    protected void warn() {
        warning.setVisible(true);
        highlightCover.setVisible(true);
    }

    /**
     * make highlight, warning, highlightCover become invisible
     */
    protected void clear() {
        highlight.setVisible(false);
        warning.setVisible(false);
        highlightCover.setVisible(false);
    }

}
