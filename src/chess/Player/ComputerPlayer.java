package src.chess.Player;

import src.chess.AI.AI;
import src.chess.AI.AISettings;
import src.chess.Board.ActiveBoard;
import src.chess.move.Move;
import src.controller.BoardController;
import src.position.Position;

public class ComputerPlayer extends Player {
    private AI ai;

    public ComputerPlayer(boolean isWhite) {
        super(isWhite);
        this.ai = new AI(isWhite, AISettings.chooseAISettings());
    }

    @Override
    public void setActiveBoard(ActiveBoard activeBoard) {
        super.setActiveBoard(activeBoard);
        ai.setActiveBoard(activeBoard);
//        beingCalculatedMove.setActiveBoard(activeBoard);
    }

    public void calculateNextMove(BoardController boardController) {
        System.out.println("Starting AI Thread");
        (new Thread(ai)).start();
//        boardController.executeNextMove(beingCalculatedMove);
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
