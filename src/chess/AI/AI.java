package src.chess.AI;

import src.chess.Board.ActiveBoard;
import src.chess.move.Move;

public abstract class AI {

    protected ActiveBoard activeBoard;

    public void setActiveBoard(ActiveBoard activeBoard) {
        this.activeBoard = activeBoard;
    }

    public abstract Move makeMove();
    public abstract void stop();
}
