package src.controller;

import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.Board.Status;
import src.chess.Player.Player;
import src.chess.move.Move;
import src.chess.piece.Piece;
import src.position.Position;

import javafx.scene.Scene;
import javafx.stage.Stage;
import src.view.board.ActiveBoardView;
import src.view.board.BoardView;
import src.view.board.PlayView;
import src.view.board.Spot;
import src.view.piece.PieceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class BoardController {
    public static final String TITLE = "Resources";

    private static final int ROWS = 8;
    private static final int COLUMNS = 8;
    private static Stack<Move> pastMoves;

    private ActiveBoard activeBoard;
    private ActiveBoardView activeBoardView;

    private Status status;
    private boolean isWhiteTurn;

    private Player black;
    private Player white;

    public boolean isDown = false;

    /**
     * construct a {@code BoardController} object with board's information taken from a file
     * @param filePath path to file contains the information of board
     * @param white white player
     * @param black black player
     */
    public BoardController(String filePath, Player white, Player black) {
        setUpBoardFromFile(new File(filePath));
        pastMoves = new Stack<>();

        black.setBoardController(this);
        white.setBoardController(this);

        this.black = black;
        this.white = white;
        this.isWhiteTurn = false;
    }

    public void setUpBoardFromFile(File file){
        try {
            Scanner in = new Scanner(file);

            int numberOfWhitePieces = Integer.parseInt(in.nextLine().replaceAll("\\D", ""));
            String[] whitePieceString = new String[numberOfWhitePieces];
            Piece[] whitePieces = new Piece[numberOfWhitePieces];
            PieceView[] whitePieceViews = new PieceView[numberOfWhitePieces];
            for (int i = 0; i < numberOfWhitePieces; i++) {
                int finalI= i;
                whitePieceString[i] = in.nextLine();
                whitePieces[i] = Piece.parsePiece(whitePieceString[i]);
                whitePieceViews[i]=whitePieces[i].getPieceView();
                whitePieceViews[i].setOnMouseClicked(e-> this.clickedSquare(whitePieces[finalI].getPosition()));
            }

            int numberOfBlackPieces = Integer.parseInt(in.nextLine().replaceAll("\\D", ""));
            String[] blackPieceString = new String[numberOfWhitePieces];
            Piece[] blackPieces = new Piece[numberOfBlackPieces];
            PieceView[] blackPieceViews = new PieceView[numberOfBlackPieces];
            for (int i = 0; i < numberOfBlackPieces; i++) {
                int finalI = i;
                blackPieceString[i] = in.nextLine();
                blackPieces[i] = Piece.parsePiece(blackPieceString[i]);
                blackPieceViews[i] = blackPieces[i].getPieceView();
                blackPieceViews[i].setOnMouseClicked(e-> this.clickedSquare(blackPieces[finalI].getPosition()));
            }

            activeBoard = new Board(whitePieces,blackPieces,this);
            activeBoardView = new BoardView(whitePieceViews, blackPieceViews,createSpot());

        } catch (FileNotFoundException e) {
            System.err.print("CANNOT READ BOARD FROM FILE:");
            System.err.println(file.getAbsolutePath());
        }

    }

    private Spot[][] createSpot(){
        Spot[][] grid = new Spot[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            int finalR = r;
            for (int c = 0; c < COLUMNS; c++) {
                int finalC = c;
                Spot spot = new Spot(r, c);
                spot.setOnMouseClicked(e-> this.clickedSquare(new Position(finalR,finalC)));
                grid[r][c] = spot;
            }
        }
        return grid;
    }

    public ActiveBoardView getActiveBoardView() {
        return activeBoardView;
    }
    public ActiveBoard getActiveBoard(){return activeBoard;}

    /**
     * start displaying the game
     */
    public void startDisplay() {
        System.out.println("Setting up GUI");

        Scene scene = new Scene(activeBoardView.getBoardGUI(this));
        Stage window = PlayView.getPlayView(TITLE, scene);
        window.setOnCloseRequest(e -> {
            System.out.println("WINDOW CLOSED!!");
            isDown = true;
            white.stop();
            black.stop();
        });
        window.show();
        System.out.println("TEST");

        changeTurn();
    }

    /**
     * executing the next move
     * @param move the move which is executed
     * change turn after executing this move
     */
    public void executeNextMove(Move move) {
        if(isDown) {
            return;
        }
        pastMoves.push(move);
        move.doMove(true);
        System.out.println(move.toString());
        activeBoardView.clearHighlights();
        if(activeBoard.checkForCheck(!isWhiteTurn)) {
            activeBoardView.showWarning(activeBoard.getKing(!isWhiteTurn).getPosition());
            if(activeBoard.checkForCheckMate(!isWhiteTurn)) {
                //TODO setup restart game when someone wins
                System.out.printf("%s has won the game!%n", isWhiteTurn ? "White" : "Black");
                status = Status.FREEZE;
                return;
            }
        } else {
            if(activeBoard.checkForStaleMate(!isWhiteTurn)) {
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

    public void clickedSquare(Position position) {
        if(isWhiteTurn) {
            white.forwardBoardInput(position);
        } else {
            black.forwardBoardInput(position);
        }
    }

    public static Move getLastMove(){
        if (!pastMoves.empty()) {
            return pastMoves.peek();
        }
        return null;
    }

    public static void pushMove(Move move){
        pastMoves.push(move);
    }
    public static void undoMove(boolean isVisual) {
        pastMoves.pop().undoMove(isVisual);
    }
    public static Move popMove(){
        return pastMoves.pop();
    }
}
