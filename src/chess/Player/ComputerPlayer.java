package src.chess.Player;

import src.chess.AI.AI;
import src.chess.AI.AISettings;
import src.chess.Board.Board;
import src.position.Position;

public class ComputerPlayer extends Player {
    private AI ai;

    public ComputerPlayer(boolean isWhite) {
        super(isWhite);
        this.ai = new AI(isWhite, AISettings.chooseAISettings());
    }

    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        ai.setBoard(board);
    }

    public void calculateNextMove() {
        System.out.println("Starting AI Thread");
        (new Thread(ai)).start();
    }

    public void forwardBoardInput(Position position) {
        System.out.println(ai.getName() + " is thinking");
    }

    public void stop() {
        ai.stop();
    }

    public String toString() {
        return "Computer";
    }
}
