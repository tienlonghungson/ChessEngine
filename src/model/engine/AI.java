package src.model.engine;

import src.model.board.ActiveBoard;
import src.model.move.Move;

public abstract class AI {

    protected ActiveBoard activeBoard;

    public void setActiveBoard(ActiveBoard activeBoard) {
        this.activeBoard = activeBoard;
    }

    public abstract Move makeMove();
    public abstract void stop();
    protected abstract double score();

    protected class HashEntry {
        public long zobrist;
        public int depth;
        public int flag;
        public int eval;
        public int ancient;
        public Move move;

        public HashEntry(long zobrist, int depth, int flag, int eval, int ancient, Move move) {
            this.zobrist = zobrist;
            this.depth = depth;
            this.flag = flag;
            this.eval = eval;
            this.ancient = ancient;
            this.move = move;
        }
    }
}
