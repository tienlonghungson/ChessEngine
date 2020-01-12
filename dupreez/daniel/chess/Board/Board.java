package dupreez.daniel.chess.Board;

import dupreez.daniel.chess.AI.Settings;
import dupreez.daniel.chess.Launcher;
import dupreez.daniel.chess.move.Move;
import dupreez.daniel.chess.piece.*;
import dupreez.daniel.position.Position;
import dupreez.daniel.position.PositionMap;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Board
{
    public static final int TILE_WIDTH = 64;
    public static final int ROWS = 8;
    public static final int COLUMNS = 8;

    private BoardController controller;

    private Stage window;
    private Tile[][] grid;

    private Piece[] whitePieces;
    private Piece[] blackPieces;

    private Piece[][] board;

    public Board(String[] whitePieces, String[] blackPieces, BoardController controller)
    {
        this.whitePieces = new Piece[whitePieces.length];
        this.blackPieces = new Piece[blackPieces.length];
        board = new Piece[ROWS][COLUMNS];

        for (int i = 0; i < whitePieces.length; i++)
        {
            Piece piece = Piece.parsePiece(whitePieces[i], this);
            this.whitePieces[i] = piece;
            this.put(piece.getPosition(), piece);
        }

        for (int i = 0; i < blackPieces.length; i++)
        {
            Piece piece = Piece.parsePiece(blackPieces[i], this);
            this.blackPieces[i] = piece;
            this.put(piece.getPosition(), piece);
        }

        this.controller = controller;
    }

    public Parent getGUI()
    {
        //Board
        Pane root = new Pane();
        root.setPrefSize(ROWS * TILE_WIDTH, COLUMNS * TILE_WIDTH);
        grid = new Tile[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++)
        {
            for (int c = 0; c < COLUMNS; c++)
            {
                Tile tile = new Tile(r, c, this);
                grid[r][c] = tile;
                root.getChildren().add(tile);
            }
        }

        //Pieces
        for (Piece whitePiece: whitePieces)
        {
            whitePiece.setupIcon(Launcher.filePath.getAbsolutePath() + "/Resources/Chess_Pieces");
            root.getChildren().add(whitePiece);
        }

        for (Piece blackPiece: blackPieces)
        {
            blackPiece.setupIcon(Launcher.filePath.getAbsolutePath() + "/Resources/Chess_Pieces");
            root.getChildren().add(blackPiece);
        }
        return root;
    }

    public void clickedSquare(Position position)
    {
        controller.clickedSquare(position);
    }

    public void giveBestMove(Move move)
    {
        controller.giveNextMove(move);
    }

    public void highlight(Position position)
    {
        grid[position.getRow()][position.getCol()].highLight();
    }

    public void warn(Position position)
    {
        grid[position.getRow()][position.getCol()].warn();
    }

    public void clearHighlights()
    {
        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[r].length; c++)
                grid[r][c].clear();
    }

    public LinkedList<Move> getAllMoves(boolean isWhite)
    {
        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        LinkedList<Move> moves = new LinkedList<>();
        for (Piece piece: pieces)
            if(!piece.isDead())
                moves.addAll(piece.getMoves());
        return moves;
    }

    private LinkedList<Move> getAllMovesNoCastle(boolean isWhite)
    {
        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        LinkedList<Move> moves = new LinkedList<>();
        for (Piece piece: pieces)
        {
            if(!piece.isDead())
            {
                if (piece instanceof King)
                    moves.addAll(((King) piece).getMovesNoCastle());
                else
                    moves.addAll(piece.getMoves());
            }
        }
        return moves;
    }

    public LinkedList<Move> getAllValidMoves(boolean isWhite)
    {
        LinkedList<Move> validMoves = new LinkedList<>();

        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        for (Piece piece: pieces)
            if(!piece.isDead())
                validMoves.addAll(piece.getValidMoves());

        return validMoves;
    }

    public Piece getKing(boolean isWhite)
    {
        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        for (Piece piece: pieces)
            if(piece instanceof King)
                return piece;
        return null;
    }

    public boolean checkForCheck(boolean isWhite)
    {
        Piece king = getKing(isWhite);
        if(!isSafeMove(king.getPosition(), isWhite))
        {
            warn(king.getPosition());
            return true;
        }
        return false;
    }

    public boolean checkIfKingCanMove(boolean isWhite)
    {
        return getKing(isWhite).getValidMoves().size() != 0;
    }

    public boolean checkForStaleMate(boolean isWhite)
    {
        return !checkIfKingCanMove(isWhite) && getAllValidMoves(isWhite).size() == 0;

    }

    public boolean checkForCheckMate(boolean isWhite)
    {
        return !isSafeMove(getKing(isWhite).getPosition(), isWhite) && getAllValidMoves(isWhite).size() == 0;
    }

    public boolean openFile(Position position)
    {
        for (int i = 0; i < Board.ROWS; i++)
        {
            Piece p = getPiece(new Position(i, position.getCol()));
            if (p != null && p instanceof Pawn)
                return false;
        }
        return true;
    }

    public int score()
    {
        int score = 0;
        for (Piece piece: whitePieces)
            if(!piece.isDead())
                score += piece.getScore();
        for (Piece piece: blackPieces)
            if(!piece.isDead())
                score -= piece.getScore();
        return score;
    }

    /**
     * Returns the Piece at the specified position
     *
     * @param position
     * @return
     */
    public Piece getPiece(Position position)
    {
        if(inBounds(position))
            return board[position.getRow()][position.getCol()];
        else
            return null;
    }

    /**
     * Sets the value in the board array at the position to the specified piece
     *
     * @param position
     * @param piece
     */
    public void put(Position position, Piece piece)
    {
        board[position.getRow()][position.getCol()] = piece;
    }

    /**
     * Sets the value in the board array to null
     *
     * @param position
     */
    public void remove(Position position)
    {
        board[position.getRow()][position.getCol()] = null;
    }

    /**
     * Moves the piece to its new position and updates the pieces' internal position
     *
     * @param piece
     * @param newPosition
     * @param isVisual
     */
    public void updatePosition(Piece piece, Position newPosition, boolean isVisual)
    {
        this.remove(piece.getPosition());
        this.put(newPosition, piece);
        piece.updatePosition(newPosition, isVisual);
    }

    /**
     * Removes the piece from the board array and sets the pieces' isDead value to true
     *
     * @param piece
     * @param isVisual
     */
    public void kill(Piece piece, boolean isVisual)
    {
        this.remove(piece.getPosition());
        piece.kill(isVisual);
    }

    /**
     * Adds the piece to the board at its last position and sets its isDead value to false
     *
     * @param piece
     * @param isVisual
     */
    public void revive(Piece piece, boolean isVisual)
    {
        this.put(piece.getPosition(), piece);
        piece.revive(isVisual);
    }

    /**
     * Checks to see if there is a piece at the specified position
     *
     * @param position
     * @return Returns true if the position in the board array is not null
     */
    public boolean hasPieceAtPosition(Position position)
    {
        Piece piece = this.getPiece(position);
        return piece != null;
    }

    /**
     * Checks to see if there is a piece at the specified position that is the opposing color
     *
     * @param position
     * @param isWhite
     * @return Returns true if the position in the board array is not null and the piece is the opposite color
     */
    public boolean hasHostilePieceAtPosition(Position position, boolean isWhite)
    {
        Piece piece = this.getPiece(position);
        return piece != null && piece.isWhite() != isWhite;
    }

    /**
     * Checks to see if there is a piece at the specified position that is the same color
     *
     * @param position
     * @param isWhite
     * @return Returns true if the position in the board array is not null and the piece is the same color
     */
    public boolean hasFriendlyPieceAtPosition(Position position, boolean isWhite)
    {
        Piece piece = this.getPiece(position);
        return piece != null && piece.isWhite() == isWhite;
    }

    /**
     * Checks to see if the position is within the bounds of the size of the board
     *
     * @param position
     * @return
     */
    public boolean inBounds(Position position)
    {
        int column = position.getCol();
        int row = position.getRow();

        return !(column < 0 || column >= COLUMNS || row < 0 || row >= ROWS);
    }

    /**
     * Checks to make sure that there is no piece at the specified position and that no opponent piece is attacking the position
     *
     * @param position
     * @param isWhite
     * @return
     */
    public boolean isCleanMove(Position position, Boolean isWhite)
    {
        return !hasPieceAtPosition(position) && isSafeMove(position, isWhite);
    }

    /**
     * Checks to see if any piece of the opposite color is attacking the specified position
     *
     * @param position
     * @param isWhite
     * @return
     */
    public boolean isSafeMove(Position position, Boolean isWhite)
    {
        LinkedList<Move> opponentMoves = getAllMovesNoCastle(!isWhite);
        for (Move move: opponentMoves)
            if(move.getEndPosition().equals(position))
                return false;
        return true;
    }

    /**
     * Sets up a new Board object from a specified file
     *
     * @param file
     * @return
     */
    public static Board setupFromFile(File file, BoardController controller)
    {
        try
        {
            Scanner in = new Scanner(file);

            int numberOfWhitePieces = Integer.parseInt(in.nextLine().replaceAll("\\D", ""));
            String[] whitePieces = new String[numberOfWhitePieces];
            for (int i = 0; i < numberOfWhitePieces; i++)
                whitePieces[i] = in.nextLine();

            int numberOfBlackPieces = Integer.parseInt(in.nextLine().replaceAll("\\D", ""));
            String[] blackPieces = new String[numberOfWhitePieces];
            for (int i = 0; i < numberOfBlackPieces; i++)
                blackPieces[i] = in.nextLine();

            return new Board(whitePieces, blackPieces, controller);
        }
        catch (FileNotFoundException e)
        {
            System.err.print("CANNOT READ BOARD FROM FILE:");
            System.err.println(file.getAbsolutePath());
        }

        return null;
    }

    public void print()
    {
        for (int i = 0; i < 8; i++)
        {
            System.out.print(8 - i + " |");
            for (int j = 0; j < 8; j++)
            {
                Position position = new Position(i, j);
                Piece piece = this.getPiece(position);
                if (piece != null)
                    System.out.printf("%2s|", piece.isWhite() ? piece.getID().toUpperCase() : piece.getID().toLowerCase());
                else
                    System.out.print("  |");
            }
            System.out.println();
        }
        System.out.print("   ");

        for (int i = 65; i < 65 + 8; i++)
        {
            System.out.printf(" %c ", i);
        }
        System.out.println();
    }
}