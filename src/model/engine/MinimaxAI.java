package src.model.engine;

import src.model.move.Move;
import src.controller.BoardController;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MinimaxAI extends AI {
    private static final boolean VISUAL_CALCULATIONS = false;
    private final boolean isWhite;
    private final AISettings settings;
    private int stateCounter = 0;
    private boolean checkmate;

    private boolean stopCalculating;
    private int iterativeDeepeningDepth;

    int nullEffect=0;

    ScheduledExecutorService timer;

    private final int TABLE_SIZE= (int)1e8; // Transposition Table Size
    private HashEntry[] table = new HashEntry[TABLE_SIZE];  // transposition table

    public MinimaxAI(boolean isWhite, AISettings settings) {
        this.isWhite = isWhite;
        this.settings = settings;
        stopCalculating = false;
    }

    public LinkedList<Move> calculateBestMove(int depth) {
        LinkedList<Move> moves = activeBoard.getAllValidMoves(isWhite);

        LinkedList<Move> bestMoves = new LinkedList<>();
        int bestScore;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Collections.sort(moves);

        bestScore = Integer.MIN_VALUE;
        for (Move move: moves) {
            move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(move);
//                System.out.println("Move "+move.toString()+"\tZobristKey: "+activeBoard.getZobristKey());
            int tempScore;
            if(settings.checkForStalemateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForStaleMate(false)) {
                tempScore = 0;
            } else if (settings.checkForCheckmateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForCheckMate(false)) {
                tempScore = Integer.MAX_VALUE;
            } else {
                tempScore = -getBestMoveHelper(!isWhite, alpha, beta, depth, activeBoard.hasNullMove(!isWhite));
            }
            BoardController.undoMove(VISUAL_CALCULATIONS);
//                System.out.println("UndoMove "+move.toString()+"\tZobristKey: "+activeBoard.getZobristKey());
            if(tempScore > bestScore) {
                bestScore = tempScore;
                bestMoves = new LinkedList<>();
                bestMoves.add(move);
            } else if (settings.addRandomness && tempScore == bestScore) {
                bestMoves.add(move);
            }

            if(!settings.addRandomness) {
                alpha = Integer.max(alpha, bestScore);
                if (beta <= alpha && settings.alphaBetaPruning) {
                    break;
                }
            }
        }
        if(bestScore == (isWhite ? Integer.MAX_VALUE : Integer.MIN_VALUE)) {
            checkmate = true;
        }

        return bestMoves;
    }

    private int getBestMoveHelper(boolean isWhiteTurn, int alpha, int beta, int depth,boolean doNull) {
        if(stopCalculating) {
            return isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        if(depth <= 0) {
            stateCounter++;
            return isWhiteTurn?(int)this.score():(int)-this.score();
        }
        LinkedList<Move> moves = activeBoard.getAllMoves(isWhiteTurn);
//        Collections.sort(moves);
        int i=0;
        int bestScore;
        bestScore = Integer.MIN_VALUE;

        // null move
        if (doNull){
            bestScore = Integer.max(bestScore,-getBestMoveHelper(!isWhiteTurn,beta,beta+1,depth-3,false));
            if (bestScore>beta){
                nullEffect++;
                return bestScore;
            }
        }
        for (Move move: moves) {
            move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(move);
            if(settings.checkForStalemateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForStaleMate(!isWhite)) {
                bestScore = Integer.max(bestScore, 0);
            } else if (settings.checkForCheckmateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForCheckMate(!isWhite)) {
                bestScore = Integer.MAX_VALUE;
            } else {
                if (i++==0) {
                    bestScore = Integer.max(bestScore, -getBestMoveHelper(!isWhiteTurn, -beta, -alpha, depth - 1,doNull));
                } else {
                    bestScore = Integer.max(bestScore, -getBestMoveHelper(!isWhiteTurn,-alpha-1,-alpha,depth-1,doNull));
                    if (alpha<bestScore && bestScore<beta){
                        bestScore = -getBestMoveHelper(!isWhiteTurn,-beta,-bestScore,depth-1,doNull);
                    }
                }
            }
            BoardController.undoMove(VISUAL_CALCULATIONS);
            alpha = Integer.max(alpha, bestScore);
            if (beta <= alpha && settings.alphaBetaPruning) {
                break;
            }
        }
        return alpha;
    }

    public void stop() {
        if(settings.useTimeLimit) {
            timer.shutdownNow();
        }
        stopCalculating = true;
    }

    @Override
    public Move makeMove() {
        stopCalculating = false;
        checkmate = false;
        if(settings.useTimeLimit) {
            System.out.println("Starting Timer");
            timer = Executors.newSingleThreadScheduledExecutor();
            timer.schedule(new AIManager(this), settings.timeLimit, TimeUnit.SECONDS);
        }

//        System.out.println("Start Searching with Zobrist Key: "+activeBoard.getZobristKey(isWhite));
        LinkedList<Move> bestMoves = new LinkedList<>();
        if(settings.iterativeDeepening) {
            iterativeDeepeningDepth = settings.iterativeDeepeningStartDepth;
            while(!stopCalculating && iterativeDeepeningDepth <= settings.depth && !checkmate) {
                stateCounter=0;
                LinkedList<Move> moves = calculateBestMove(iterativeDeepeningDepth++);
                if(!stopCalculating) {
                    bestMoves = moves;
                }
            }
        } else {
            bestMoves = calculateBestMove(settings.depth);
        }

        stop();
        printReport(bestMoves.size());
//        System.out.println("Done Searching with Zobrist Key: "+activeBoard.getZobristKey(isWhite));
        if(settings.moveOrdering) {
            Collections.sort(bestMoves);
            System.out.println(getName() + "'s best moves: " + bestMoves);
            return bestMoves.getFirst();
        } else {
            return bestMoves.get((int) (Math.random() * bestMoves.size()));
        }
    }

    @Override
    protected double score() {
        return activeBoard.score();
    }

    public String getName() {
        return settings.name;
    }

    public void printReport(int size) {
        System.out.printf("Reached a depth of %d, chose one out of %d equal moves, and evaluated %,d moves%n", iterativeDeepeningDepth, size, stateCounter);
        stateCounter = 0;
    }
}

