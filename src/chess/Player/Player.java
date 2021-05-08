package src.chess.Player;

import src.controller.BoardController;
import src.chess.move.Move;
import src.position.Position;

public abstract class Player {
    public static final String[] PLAYER_TYPES = {"Human", "Computer"};

    protected boolean isWhite;

    protected BoardController boardController;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setBoardController(BoardController boardController) {
        this.boardController = boardController;
    }

    public void returnMove(Move move) {
        boardController.executeNextMove(move);
    }

    public abstract void calculateNextMove();
    public abstract void forwardBoardInput(Position position);
    public abstract void stop();
    public abstract String toString();
    public static Player parsePlayer(String playerType, boolean isWhite) {
        return switch (playerType) {
            case "Human" -> new HumanPlayer(isWhite);
            case "Computer" -> new ComputerPlayer(isWhite);
            default -> null;
        };
    }
}
