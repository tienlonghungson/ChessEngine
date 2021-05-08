package src.chess.Board;

import src.chess.move.Move;
import src.controller.BoardController;
import src.position.Position;

import src.chess.piece.King;
import src.chess.piece.Pawn;
import src.chess.piece.Piece;

import java.util.LinkedList;
import java.util.Stack;

public class Board implements ActiveBoard {
    public static final int ROWS = 8;
    public static final int COLUMNS = 8;

    private BoardController controller;


    private Piece[] whitePieces;
    private Piece[] blackPieces;

    private Piece[][] board;

    public Board(Piece[] whitePieces, Piece[] blackPieces, BoardController boardController){
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;

        board = new Piece[ROWS][COLUMNS];
        for (Piece whitePiece:
             whitePieces) {
            this.put(whitePiece.getPosition(), whitePiece);
        }

        for (Piece blackPiece:
             blackPieces) {
            this.put(blackPiece.getPosition(), blackPiece);
        }

        this.controller = boardController;
    }

//    public Board(String[] whitePieces, String[] blackPieces, BoardController controller) {
//        this.whitePieces = new Piece[whitePieces.length];
//        this.blackPieces = new Piece[blackPieces.length];
//        board = new Piece[ROWS][COLUMNS];
//
//        int i=0;
//        for (String whitePiece:
//             whitePieces) {
//            Piece piece = Piece.parsePiece(whitePiece,this);
//            this.whitePieces[i++]= piece;
//            this.put(piece.getPosition(), piece);
//        }
//
//        i=0;
//        for (String blackPiece:
//             blackPieces) {
//            Piece piece = Piece.parsePiece(blackPiece,this);
//            this.blackPieces[i++] = piece;
//            this.put(piece.getPosition(),piece);
//        }
//
//        this.controller = controller;
//    }


//    public void giveBestMove(Move move) {
//        controller.executeNextMove(move);
//    }

    /**
     * get all possible moves of a player
     *
     * @param isWhite whether the player plays black or white
     * @return list of possible moves of a player
     */
    public LinkedList<Move> getAllMoves(boolean isWhite) {
        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        LinkedList<Move> moves = new LinkedList<>();
        for (Piece piece: pieces) {
            if (!piece.isDead()) {
                moves.addAll(piece.getMoves(this));
            }
        }
        return moves;
    }

    /**
     * get all possible moves excluding castling of a player
     *
     * @param isWhite whether the player plays black or white
     * @return list of possible moves excluding castling of a player
     */
    private LinkedList<Move> getAllMovesNoCastle(boolean isWhite) {
        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        LinkedList<Move> moves = new LinkedList<>();
        for (Piece piece: pieces) {
            if(!piece.isDead()) {
                if (piece instanceof King) {
                    moves.addAll(((King) piece).getMovesNoCastle(this));
                } else {
                    moves.addAll(piece.getMoves(this));
                }
            }
        }
        return moves;
    }

    /**
     * get all valid moves of one player
     *
     * @param isWhite whether the player plays black or white
     * @return list of valid moves of one player
     */
    public LinkedList<Move> getAllValidMoves(boolean isWhite) {
        LinkedList<Move> validMoves = new LinkedList<>();

        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        for (Piece piece: pieces) {
            if (!piece.isDead()) {
                validMoves.addAll(piece.getValidMoves(this));
            }
        }
        return validMoves;
    }

    /**
     * get the king
     *
     * @param isWhite whether the King is black or white
     * @return King {@code Piece}
     */
    public Piece getKing(boolean isWhite) {
        Piece[] pieces = isWhite ? whitePieces : blackPieces;
        for (Piece piece: pieces) {
            if (piece instanceof King) {
                return piece;
            }
        }
        return null;
    }

    /**
     * check if the king of one player is under attack
     * @param isWhite whether the player play white or black
     * @return {@code true} if the king is under attack, {@code false} otherwise
     */
    public boolean checkForCheck(boolean isWhite) {
        Piece king = getKing(isWhite);
        //            activeBoardView.showWarning(king.getPosition());
//        if(!isSafeMove(king.getPosition(), isWhite)) {
////            activeBoardView.showWarning(king.getPosition());
//            return true;
//        }
//        return false;
        return !isSafeMove(king.getPosition(), isWhite);
    }

    /**
     * check if the King can make any moves
     *
     * @param isWhite whether the king is black or white
     * @return {@code true} if the king can move, {@code false} otherwise
     */
    public boolean checkIfKingCanMove(boolean isWhite) {
        return getKing(isWhite).getValidMoves(this).size() != 0;
    }

    /**
     * check if a player came to a stalemate
     *
     * @param isWhite whether the player plays black or white
     * @return {@code true} if this player can no longer make any moves, {@code false} otherwise
     */
    public boolean checkForStaleMate(boolean isWhite) {
        return !checkIfKingCanMove(isWhite) && getAllValidMoves(isWhite).size() == 0;
    }

    /**
     * check if a player is checkmate
     *
     * @param isWhite whether the player plays black or white
     * @return {@code true} if this player is checkmate, {@code false} otherwise
     */
    public boolean checkForCheckMate(boolean isWhite) {
        return !isSafeMove(getKing(isWhite).getPosition(), isWhite) && getAllValidMoves(isWhite).size() == 0;
    }

    /**
     * check if the file is open
     *
     * @param col the file which is checked
     * @return {@code true} if the file is open, {@code false} otherwise
     */
    public boolean openFile(int col) {
        for (int i = 0; i < Board.ROWS; i++) {
            Piece p = getPiece(new Position(i, col));
            if (p instanceof Pawn) {
                return false;
            }
        }
        return true;
    }

    /**
     * calculate score in this board
     * @return total score in board
     */
    public int score() {
        int score = 0;
        for (Piece piece: whitePieces) {
            if (!piece.isDead()) {
                score += piece.getScore();
            }
        }
        for (Piece piece: blackPieces) {
            if (!piece.isDead()) {
                score -= piece.getScore();
            }
        }
        return score;
    }

    /**
     * Returns the Piece at the specified position
     *
     * @param position the specified position
     * @return the Piece at {@code position}
     */
    public Piece getPiece(Position position) {
        if(isInBounds(position)) {
            return board[position.getRow()][position.getCol()];
        }
        else
            return null;
    }

    /**
     * Sets the value in the board array at the position to the specified piece
     *
     * @param position the position which is got the piece
     * @param piece which is placed at position
     */
    public void put(Position position, Piece piece) {
        board[position.getRow()][position.getCol()] = piece;
    }

    /**
     * Sets the value in the board array to null
     *
     * @param position whose the piece is removed
     */
    public void remove(Position position) {
        board[position.getRow()][position.getCol()] = null;
    }

    /**
     * Moves the piece to its new position and updates the pieces' internal position
     *
     * @param piece piece which is moved
     * @param newPosition new position of piece
     * @param isVisual whether the move is visible
     */
    public void updatePosition(Piece piece, Position newPosition, boolean isVisual) {
        this.remove(piece.getPosition());
        this.put(newPosition, piece);
        piece.updatePosition(newPosition, isVisual);
    }

    /**
     * Removes the piece from the board array and sets the piece's isDead value to true
     *
     * @param piece which is dead
     * @param isVisual whether the move is visible
     */
    public void kill(Piece piece, boolean isVisual) {
        this.remove(piece.getPosition());
        piece.kill(isVisual);
    }

    /**
     * Adds the piece to the board at its last position and sets its isDead value to false
     *
     * @param piece which is revived
     * @param isVisual whether the revival is visible
     */
    public void revive(Piece piece, boolean isVisual) {
        this.put(piece.getPosition(), piece);
        piece.revive(isVisual);
    }

    /**
     * Checks to see if there is a piece at the specified position
     *
     * @param position the position which is checked
     * @return Returns true if the position in the board array is not null
     */
    public boolean hasPieceAtPosition(Position position) {
        Piece piece = this.getPiece(position);
        return piece != null;
    }

    /**
     * Checks to see if there is a piece at the specified position that is the opposing color
     *
     * @param position the position which is checked
     * @param isWhite whether the piece at {@code position} is white or black
     * @return Returns true if the position in the board array is not null and the piece is the opposite color
     */
    public boolean hasHostilePieceAtPosition(Position position, boolean isWhite) {
        Piece piece = this.getPiece(position);
        return piece != null && piece.isWhite() != isWhite;
    }

    /**
     * Checks to see if there is a piece at the specified position that is the same color
     *
     * @param position the position which is checked
     * @param isWhite whether the piece at {@code position} is white or black
     * @return Returns true if the position in the board array is not null and the piece is the same color
     */
    public boolean hasFriendlyPieceAtPosition(Position position, boolean isWhite) {
        Piece piece = this.getPiece(position);
        return piece != null && piece.isWhite() == isWhite;
    }

    /**
     * Checks to see if the position is within the bounds of the size of the board
     *
     * @param position the position which is checked
     * @return whether the position is in the Board
     */
    public boolean isInBounds(Position position) {
        int column = position.getCol();
        int row = position.getRow();

        return !(column < 0 || column >= COLUMNS || row < 0 || row >= ROWS);
    }

    /**
     * Checks to make sure that there is no piece at the specified position and that no opponent piece is attacking the position
     *
     * @param position the position which is checked
     * @param isWhite whether the piece is black or white
     * @return true if the position is not under attack and contains no piece
     */
    public boolean isCleanMove(Position position, Boolean isWhite) {
        return !hasPieceAtPosition(position) && isSafeMove(position, isWhite);
    }

    /**
     * Checks to see if any piece of the opposite color is attacking the specified position
     *
     * @param position the position which is checked
     * @param isWhite whether the piece is black or white
     * @return true if the position is not under attack
     */
    public boolean isSafeMove(Position position, Boolean isWhite) {
        LinkedList<Move> opponentMoves = getAllMovesNoCastle(!isWhite);
        for (Move move: opponentMoves) {
            if (move.getEndPosition().equals(position)) {
                return false;
            }
        }
        return true;
    }


    //    /**
//     * Sets up a new Board object from a specified file
//     *
//     * @param file contains the Board information
//     * @return a {@code Board} object representing the Board from file
//     */
//    public static Board setupFromFile(File file, BoardController controller) {
//        try {
//            Scanner in = new Scanner(file);
//
//            int numberOfWhitePieces = Integer.parseInt(in.nextLine().replaceAll("\\D", ""));
//            String[] whitePieces = new String[numberOfWhitePieces];
//            for (int i = 0; i < numberOfWhitePieces; i++) {
//                whitePieces[i] = in.nextLine();
//            }
//
//            int numberOfBlackPieces = Integer.parseInt(in.nextLine().replaceAll("\\D", ""));
//            String[] blackPieces = new String[numberOfWhitePieces];
//            for (int i = 0; i < numberOfBlackPieces; i++) {
//                blackPieces[i] = in.nextLine();
//            }
//            return new Board(whitePieces, blackPieces, controller);
//        } catch (FileNotFoundException e) {
//            System.err.print("CANNOT READ BOARD FROM FILE:");
//            System.err.println(file.getAbsolutePath());
//        }
//
//        return null;
//    }

    public void print() {
        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + " |");
            for (int j = 0; j < 8; j++) {
                Position position = new Position(i, j);
                Piece piece = this.getPiece(position);
                if (piece != null) {
                    System.out.printf("%2s|", piece.isWhite() ? piece.getID().toUpperCase() : piece.getID().toLowerCase());
                } else {
                    System.out.print("  |");
                }
            }
            System.out.println();
        }
        System.out.print("   ");

        for (int i = 65; i < 65 + 8; i++) {
            System.out.printf(" %c ", i);
        }
        System.out.println();
    }
}