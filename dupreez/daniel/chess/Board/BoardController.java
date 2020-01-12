package dupreez.daniel.chess.Board;

import dupreez.daniel.chess.AI.AI;
import dupreez.daniel.chess.AI.Settings;
import dupreez.daniel.chess.Player.Player;
import dupreez.daniel.chess.move.Move;
import dupreez.daniel.chess.move.MoveConformationWindow;
import dupreez.daniel.chess.piece.King;
import dupreez.daniel.chess.piece.Piece;
import dupreez.daniel.position.Position;
import dupreez.daniel.position.PositionMap;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BoardController
{
    public static final String TITLE = "Chess";

    private Board board;
    private Stack<Move> pastMoves;

    private Status status;
    private boolean isWhiteTurn;

    private Player black;
    private Player white;

    private Stage window;

    public boolean isDown = false;

    public BoardController(String filePath, Player white, Player black)
    {
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

    public void startDisplay()
    {
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

    public void giveNextMove(Move move)
    {
        if(isDown)
            return;
        pastMoves.push(move);
        move.doMove(true);
        board.clearHighlights();
        if(board.checkForCheck(!isWhiteTurn))
        {
            if(board.checkForCheckMate(!isWhiteTurn))
            {
                //TODO setup restart game when someone wins
                System.out.printf("%s has won the game!%n", isWhiteTurn ? "White" : "Black");
                status = Status.FREEZE;
                return;
            }
        }
        else
        {
            if(board.checkForStaleMate(!isWhiteTurn))
            {
                System.out.printf("Stalemate!!%n");
                status = Status.FREEZE;
                return;
            }
        }

        changeTurn();
    }

    public void changeTurn()
    {
        isWhiteTurn = !isWhiteTurn;
        if(isWhiteTurn)
            white.calculateNextMove();
        else
            black.calculateNextMove();
    }



    //TODO
    public void undoMove()
    {
        pastMoves.pop().undoMove(true);
    }

    public void clickedSquare(Position position)
    {
        if(isWhiteTurn)
            white.forwardBoardInput(position);
        else
            black.forwardBoardInput(position);
    }
}
