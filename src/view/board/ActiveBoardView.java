package src.view.board;

import javafx.scene.Parent;
import src.controller.BoardController;
import src.chess.move.Move;
import src.position.Position;

public interface ActiveBoardView {
    int SPOT_WIDTH = 64;
    int ROWS = 8;
    int COLUMNS = 8;

    Parent getBoardGUI(BoardController boardController);

    /**
     * disable highlight, warning, and highlight cover
     */
    void clearHighlights();

    /**
     * turn on highlight at a specific position
     * @param position where highlight is turned on
     */
    void highlight(Position position);

    /**
     * show warning at a specific position
     * @param position where the warning is shown
     */
    void showWarning(Position position);
}
