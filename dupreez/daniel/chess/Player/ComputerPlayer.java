package dupreez.daniel.chess.Player;

import dupreez.daniel.chess.AI.AI;
import dupreez.daniel.chess.AI.AIManager;
import dupreez.daniel.chess.AI.AISettings;
import dupreez.daniel.chess.AI.Settings;
import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.Launcher;
import dupreez.daniel.chess.move.Move;
import dupreez.daniel.chess.move.MoveConformationWindow;
import dupreez.daniel.chess.piece.Piece;
import dupreez.daniel.position.Position;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComputerPlayer extends Player
{
    private AI ai;

    public ComputerPlayer(boolean isWhite)
    {
        super(isWhite);
        this.ai = new AI(isWhite, AISettings.chooseAISettings());
    }

    @Override
    public void setBoard(Board board)
    {
        super.setBoard(board);
        ai.setBoard(board);
    }

    public void calculateNextMove()
    {
        System.out.println("Starting AI Thread");
        (new Thread(ai)).start();
    }

    public void forwardBoardInput(Position position)
    {
        System.out.println(ai.getName() + " is thinking");
    }

    public void stop()
    {
        ai.stop();
    }

    public String toString()
    {
        return "Computer";
    }
}
