package src.chess.Player;

import src.chess.AI.MinimaxAI;
import src.chess.AI.AISettings;
import src.controller.BoardController;
import src.position.Position;

public class ComputerPlayer extends Player {
    private MinimaxAI ai;

    public ComputerPlayer(boolean isWhite) {
        super(isWhite);
        this.ai = new MinimaxAI(isWhite, AISettings.chooseAISettings());
    }


    @Override
    public void setBoardController(BoardController boardController) {
        super.setBoardController(boardController);
        ai.setActiveBoard(boardController.getActiveBoard());
    }

    public void calculateNextMove() {
        System.out.println("Starting AI Thread");
        new Thread(() -> boardController.executeNextMove(ai.makeMove())).start();
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
