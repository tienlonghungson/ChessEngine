package src.chess.piece;

import src.chess.Board.ActiveBoard;
import src.chess.Board.Board;
import src.chess.Launcher;
import src.chess.move.Move;
import src.position.Position;
import src.position.PositionMap;
import src.view.piece.PieceView;

import java.util.LinkedList;

public abstract class Piece{
    public static final long TRANSITION_TIME = 400; //400

    protected boolean isWhite;
    protected boolean isDead;
    protected Position position;
//    protected ActiveBoard activeBoard;
    protected PieceView pieceView;

    public Piece(Position position, boolean isWhite) {
        this.position = position;
        this.isWhite = isWhite;
//        this.activeBoard = boardActions;
        this.isDead = false;
        pieceView = new PieceView();
        pieceView.setupIcon(Launcher.filePath.getAbsolutePath() + "/ChessPieceImages",this);
    }

    public PieceView getPieceView() {
        return pieceView;
    }

    /**
     *
     * @return the list of possible moves (including valid moves and moves can cause the king to be unsafe)
     * @param activeBoard
     */
    public LinkedList<Move> getMoves(ActiveBoard activeBoard){
        LinkedList<Move> moves = new LinkedList<>();
        final int[][] moveDirections = moveDirections();
        for (int[] direction:
             moveDirections) {
            getMovesHelper(direction[0],direction[1],moves,activeBoard );
        }
        return moves;
    }

    /**
     * check if the moves in direction determine by {@code (rowInc,colInc)} can be executed
     * @param rowInc x coordinate of direction
     * @param colInc y coordinate of direction
     * @param moves contains the moves if they are valid
     * @param activeBoard
     */
    protected abstract void getMovesHelper(int rowInc, int colInc, LinkedList<Move> moves, ActiveBoard activeBoard);

    /**
     *
     * @return the array identifies direction of possible moves
     */
    protected abstract int[][] moveDirections();

    /**
     * valid moves are list of possible moves excluding moves cause the king to be unsafe
     * @return list of valid move
     * @param activeBoard
     */
    public LinkedList<Move> getValidMoves(ActiveBoard activeBoard) {
        LinkedList<Move> validMoves = new LinkedList<>();
        LinkedList<Move> allMoves = getMoves(activeBoard);

        Piece king = activeBoard.getKing(isWhite);

        for (Move move: allMoves) {
            move.doMove(false);
            // check if this move cause the king to be unsafe
            if(activeBoard.isSafeMove(king.getPosition(), isWhite)) {
                validMoves.add(move);
            }
            move.undoMove(false);
        }

        return validMoves;
    }

    public abstract int getScore();

    public abstract String getID();

    public abstract String getName();

    public boolean isDead() {
        return isDead;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Position getNewPosition() {
        return position.getPositionWithOffset();
    }

    public Position getPosition() {
        return this.position;
    }

    // GUI evolve
    public void updatePosition(Position position, boolean isVisual) {
        this.position = position;
        if(isVisual) {
            pieceView.updatePosition(position);
        }
    }

    // GUI evolve
    public void kill(boolean isVisual) {
        this.isDead = true;
        if(isVisual) {
            pieceView.kill();
        }
    }

    // GUI evolve
    public void revive(boolean isVisual) {
        this.isDead = false;
        if(isVisual) {
            pieceView.revive();
        }
    }

    public static int distanceFromMiddle(Position position) {
        //TODO Dynamic Middle
        return (int) Math.hypot(3.5 - position.getRow(), 3.5 - position.getCol());
    }

    public static Piece parsePiece(String rawData, Board board) {
        rawData = rawData.substring(1);
        String[] data = rawData.split("[\\W]+");

        return switch (data[0]) {
            case "P" -> Pawn.parsePawn(data, board);
            case "R" -> Rook.parseRook(data, board);
            case "Kn" -> Knight.parseKnight(data, board);
            case "B" -> Bishop.parseBishop(data, board);
            case "Q" -> Queen.parseQueen(data, board);
            case "K" -> King.parseKing(data, board);
            default -> null;
        };

    }

    public static Piece parsePiece(String rawData) {
        rawData = rawData.substring(1);
        String[] data = rawData.split("[\\W]+");

        return switch (data[0]) {
            case "P" -> Pawn.parsePawn(data);
            case "R" -> Rook.parseRook(data);
            case "Kn" -> Knight.parseKnight(data);
            case "B" -> Bishop.parseBishop(data);
            case "Q" -> Queen.parseQueen(data);
            case "K" -> King.parseKing(data);
            default -> null;
        };

    }

    public static PositionMap<Move> getMoveMap(LinkedList<Move> moves) {
        PositionMap<Move> pMap = new PositionMap<>(moves.size());
        for (Move m: moves) {
            pMap.add(m.getEndPosition(), m);
        }
        return pMap;
    }
}
