package src.chess.AI;

public class AIManager implements Runnable {
    private MinimaxAI ai;

    public AIManager(MinimaxAI ai) {
        this.ai = ai;
    }

    public void run() {
        ai.stop();
    }
}
