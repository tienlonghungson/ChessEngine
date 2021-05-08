package src.chess.Player;

import src.chess.AI.Settings;
import src.chess.move.Move;
import src.chess.move.MoveConformationWindow;
import src.chess.piece.Piece;
import src.position.Position;
import src.position.PositionMap;

import java.util.LinkedList;

public class HumanPlayer extends Player {

    private Piece highlightedPiece;
    private PositionMap<Move> positionMap;

    public HumanPlayer(boolean isWhite) {
        super(isWhite);
    }

    public void calculateNextMove() {}

    public void forwardBoardInput(Position position) {
        // everytime we clicked a piece, highlight turns on
        // so if we clicked a another piece, we need to turn off the highlight of the last piece
        boardController.getActiveBoardView().clearHighlights();
        if(highlightedPiece == null) {
            Piece piece = boardController.getActiveBoard().getPiece(position);
            if(piece != null && piece.isWhite() == isWhite) {
                boardController.getActiveBoardView().highlight(position);
                highlightedPiece = piece;
                LinkedList<Move> moves = piece.getValidMoves(boardController.getActiveBoard());
                positionMap = Piece.getMoveMap(moves);
                if(Settings.showMoves) {
                    for (Move move : moves) {
                        boardController.getActiveBoardView().highlight(move.getEndPosition());
                    }
                }
            }
        } else {
            if (positionMap != null) {
                LinkedList<Move> moves = new LinkedList<>();
                Move move = positionMap.remove(position);
                while (move != null) {
                    moves.add(move);
                    move = positionMap.remove(position);
                }
                highlightedPiece = null;
                positionMap = null;
                if (moves.size() == 1) {
                    returnMove(moves.get(0));
                } else if (moves.size() > 1) {
                    returnMove(MoveConformationWindow.display("Confirm Move", "Which move would you like to take?", moves));
                } else {
                    forwardBoardInput(position);
                }
            } else {
                boardController.getActiveBoardView().highlight(position);
            }
        }
    }

    public void stop() {}

    public String toString() {
        return "Human";
    }
}
