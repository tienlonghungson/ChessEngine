package src.chess.Board;

import src.chess.move.Move;
import src.chess.piece.Piece;
import src.position.Position;
import src.view.board.ActiveBoardView;

import java.util.LinkedList;

public interface ActiveBoard {

    /**
     * get all possible moves of a player
     *
     * @param isWhite whether the player plays black or white
     * @return list of possible moves of a player
     */
    LinkedList<Move> getAllMoves(boolean isWhite);

    /**
     * get all valid moves of one player
     *
     * @param isWhite whether the player plays black or white
     * @return list of valid moves of one player
     */
    LinkedList<Move> getAllValidMoves(boolean isWhite);

    /**
     * Checks to make sure that there is no piece at the specified position and that no opponent piece is attacking the position
     *
     * @param position the position which is checked
     * @param isWhite whether the piece is black or white
     * @return true if the position is not under attack and contains no piece
     */
    boolean isCleanMove(Position position, Boolean isWhite);

    /**
     * Checks to see if any piece of the opposite color is attacking the specified position
     *
     * @param position the position which is checked
     * @param isWhite whether the piece is black or white
     * @return true if the position is not under attack
     */
    boolean isSafeMove(Position position, Boolean isWhite);

    /**
     * Checks to see if the position is within the bounds of the size of the board
     *
     * @param position the position which is checked
     * @return whether the position is in the Board
     */
    boolean isInBounds(Position position);

    /**
     * Checks to see if there is a piece at the specified position that is the same color
     *
     * @param position the position which is checked
     * @param isWhite whether the piece at {@code position} is white or black
     * @return Returns true if the position in the board array is not null and the piece is the same color
     */
    boolean hasFriendlyPieceAtPosition(Position position, boolean isWhite);

    /**
     * Checks to see if there is a piece at the specified position that is the opposing color
     *
     * @param position the position which is checked
     * @param isWhite whether the piece at {@code position} is white or black
     * @return Returns true if the position in the board array is not null and the piece is the opposite color
     */
    boolean hasHostilePieceAtPosition(Position position, boolean isWhite);

    /**
     * Checks to see if there is a piece at the specified position
     *
     * @param position the position which is checked
     * @return Returns true if the position in the board array is not null
     */
    boolean hasPieceAtPosition(Position position);

    /**
     * Adds the piece to the board at its last position and sets its isDead value to false
     *  @param piece which is revived
     * @param isVisual whether the revival is visible
     */
    void revive(Piece piece, boolean isVisual);

    /**
     * Removes the piece from the board array and sets the piece's isDead value to true
     * @param piece which is dead
     * @param isVisual whether this move is visible
     *
     */
    void kill(Piece piece, boolean isVisual);

    /**
     * Moves the piece to its new position and updates the pieces' internal position
     *  @param piece piece which is moved
     * @param newPosition new position of piece
     * @param isVisual whether this is update visibly
     */
    void updatePosition(Piece piece, Position newPosition, boolean isVisual);

    /**
     * Returns the Piece at the specified position
     *
     * @param position the specified position
     * @return the Piece at {@code position}
     */
    Piece getPiece(Position position);

    /**
     * calculate score in this board
     * @return total score in board
     */
    int score();

    /**
     * check if the file( column) is open
     *
     * @param col the file which is checked
     * @return {@code true} if the file is open, {@code false} otherwise
     */

    boolean openFile(int col);

    /**
     * check if a player is checkmate
     *
     * @param isWhite whether the player plays black or white
     * @return {@code true} if this player is checkmate, {@code false} otherwise
     */
    boolean checkForCheckMate(boolean isWhite);

    /**
     * check if a player came to a stalemate
     *
     * @param isWhite whether the player plays black or white
     * @return {@code true} if this player can no longer make any moves, {@code false} otherwise
     */
    boolean checkForStaleMate(boolean isWhite);

    /**
     * get the king
     *
     * @param isWhite whether the King is black or white
     * @return King {@code Piece}
     */
    Piece getKing(boolean isWhite);

    /**
     * check if the king of one player is under attack
     * @param isWhite whether the player play white or black
     * @return {@code true} if the king is under attack, {@code false} otherwise
     */
    boolean checkForCheck(boolean isWhite);


    // TODO recode giveBestMove

//    void giveBestMove(Move move);

}
