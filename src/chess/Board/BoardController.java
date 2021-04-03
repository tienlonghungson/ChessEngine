package src.chess.Board;

import src.chess.Player.Player;
import src.chess.move.Move;
import src.position.Position;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Stack;

public class BoardController {
    public static final String TITLE = "Chess";

    private Board board;
    private Stack<Move> pastMoves;

    private Status status;
    private boolean isWhiteTurn;

    private Player black;
    private Player white;

    private Stage window;

    public boolean isDown = false;

    /**
     * construct a {@code BoardController} object with board's information taken from a file
     * @param filePath path to file contains the information of board
     * @param white white player
     * @param black black player
     */
    public BoardController(String filePath, Player white, Player black) {
        board = Board.setupFromFile(new File(filePath), this);
        pastMoves = new Stack<>();

        black.setBoardController(this);
        white.setBoardController(this);
        black.setBoard(board);
        white.setBoard(board);

        this.black = black;
        this.white = white;
        this.isWhiteTurn = false;
    }

    /**
     * start displaying the game
     */
    public void startDisplay() {
        System.out.println("Setting up GUI");
        window = new Stage();
        window.setTitle(TITLE);
        window.setResizable(false);
        window.setOnCloseRequest(e -> {
            System.out.println("WINDOW CLOSED!!");
            isDown = true;
            white.stop();
            black.stop();
        });

        Scene scene = new Scene(board.getGUI());


        window.setScene(scene);
        window.show();
        System.out.println("TEST");

        changeTurn();
    }

    /**
     * executing the next move
     * @param move the move which is executed
     * change turn after executing this move
     */
    public void giveNextMove(Move move) {
        if(isDown) {
            return;
        }
        pastMoves.push(move);
        move.doMove(true);
        board.clearHighlights();
        if(board.checkForCheck(!isWhiteTurn)) {
            if(board.checkForCheckMate(!isWhiteTurn)) {
                //TODO setup restart game when someone wins
                System.out.printf("%s has won the game!%n", isWhiteTurn ? "White" : "Black");
                status = Status.FREEZE;
                return;
            }
        } else {
            if(board.checkForStaleMate(!isWhiteTurn)) {
                System.out.printf("Stalemate!!%n");
                status = Status.FREEZE;
                return;
            }
        }

        changeTurn();
    }

    public void changeTurn() {
        isWhiteTurn = !isWhiteTurn;
        if(isWhiteTurn) {
            white.calculateNextMove();
        } else {
            black.calculateNextMove();
        }
    }



    //TODO
    public void undoMove() {
        pastMoves.pop().undoMove(true);
    }

    public void clickedSquare(Position position) {
        if(isWhiteTurn) {
            white.forwardBoardInput(position);
        } else {
            black.forwardBoardInput(position);
        }
    }
}
