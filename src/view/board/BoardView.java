package src.view.board;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import src.controller.BoardController;
import src.position.Position;
import src.view.piece.PieceView;


public class BoardView implements ActiveBoardView {
    private PieceView[] whitePieces;
    private PieceView[] blackPieces;
    private Spot[][] grid;

    public BoardView(PieceView[] whitePieces,PieceView[] blackPieces, Spot[][] grid){
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.grid = grid;
    }

    @Override
    public Parent getBoardGUI(BoardController boardController) {
        //Board
        Pane root = new Pane();
        root.setPrefSize(ROWS * SPOT_WIDTH, COLUMNS * SPOT_WIDTH);
        for (Spot[] row: grid) {
            for (Spot spot: row) {
                root.getChildren().add(spot);
            }
        }

        //Pieces
        for (PieceView whitePiece: whitePieces) {
            root.getChildren().add(whitePiece);
        }

        for (PieceView blackPiece: blackPieces) {
            root.getChildren().add(blackPiece);
        }
        return root;
    }

    /**
     * disable highlight, warning, and highlight cover of every spot
     */
    @Override
    public void clearHighlights() {
        for (Spot[] spots : grid) {
            for (Spot spot : spots) {
                spot.clear();
            }
        }
    }

    @Override
    public void highlight(Position position) {
        grid[position.getRow()][position.getCol()].highLight();
    }

    @Override
    public void showWarning(Position position) {
        grid[position.getRow()][position.getCol()].warn();
    }

}
