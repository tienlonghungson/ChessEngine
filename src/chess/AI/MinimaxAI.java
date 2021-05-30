package src.chess.AI;

import src.chess.move.Move;
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

    ScheduledExecutorService timer;

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

        if(isWhite) {
            bestScore = Integer.MIN_VALUE;
            for (Move move: moves) {
                move.doMove(VISUAL_CALCULATIONS);
                BoardController.pushMove(move);
                int tempScore;
                if(settings.checkForStalemateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForStaleMate(false)) {
                    tempScore = 0;
                } else if (settings.checkForCheckmateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForCheckMate(false)) {
                    tempScore = Integer.MAX_VALUE;
                } else {
                    tempScore = getBestMoveHelper(false, alpha, beta, depth);
                }
                BoardController.undoMove(VISUAL_CALCULATIONS);
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
        } else {
            bestScore = Integer.MAX_VALUE;
            for (Move move: moves) {
                move.doMove(VISUAL_CALCULATIONS);
                BoardController.pushMove(move);
                int tempScore;
                if(settings.checkForStalemateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForStaleMate(true)) {
                    tempScore = 0;
                } else if (settings.checkForCheckmateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForCheckMate(true)) {
                    tempScore = Integer.MIN_VALUE;
                } else {
                    tempScore = getBestMoveHelper(true, alpha, beta, depth);
                }
                BoardController.undoMove(VISUAL_CALCULATIONS);
                if(tempScore < bestScore) {
                    bestScore = tempScore;
                    bestMoves = new LinkedList<>();
                    bestMoves.add(move);
                } else if (settings.addRandomness && tempScore == bestScore) {
                    bestMoves.add(move);
                }

                if(!settings.addRandomness) {
                    beta = Integer.min(beta, bestScore);
                    if (beta <= alpha && settings.alphaBetaPruning) {
                        break;
                    }
                }
            }
        }
        if(bestScore == (isWhite ? Integer.MAX_VALUE : Integer.MIN_VALUE)) {
            checkmate = true;
        }

        return bestMoves;
    }

    private int getBestMoveHelper(boolean isWhiteTurn, int alpha, int beta, int depth) {
        if(stopCalculating) {
            return isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        if(depth == 0) {
            stateCounter++;
            return activeBoard.score();
        }
        LinkedList<Move> moves = activeBoard.getAllMoves(isWhiteTurn);
        int bestScore;
        if(isWhiteTurn) {
            bestScore = Integer.MIN_VALUE;
            for (Move move: moves) {
                move.doMove(VISUAL_CALCULATIONS);
                BoardController.pushMove(move);
                if(settings.checkForStalemateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForStaleMate(!isWhite)) {
                    bestScore = Integer.max(bestScore, 0);
                } else if (settings.checkForCheckmateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForCheckMate(!isWhite)) {
                    bestScore = Integer.MAX_VALUE;
                } else {
                    bestScore = Integer.max(bestScore, getBestMoveHelper(false, alpha, beta, depth - 1));
                }
                BoardController.undoMove(VISUAL_CALCULATIONS);
                alpha = Integer.max(alpha, bestScore);
                if (beta <= alpha && settings.alphaBetaPruning) {
                    break;
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (Move move: moves) {
                move.doMove(VISUAL_CALCULATIONS);
                BoardController.pushMove(move);
                if(settings.checkForStalemateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForStaleMate(!isWhite)) {
                    bestScore = Integer.min(bestScore, 0);
                } else if (settings.checkForCheckmateDepth > (iterativeDeepeningDepth - depth) && activeBoard.checkForCheckMate(!isWhite)) {
                    bestScore = Integer.MIN_VALUE;
                } else {
                    bestScore = Integer.min(bestScore, getBestMoveHelper(true, alpha, beta, depth - 1));
                }
                BoardController.undoMove(VISUAL_CALCULATIONS);

                beta = Integer.min(beta, bestScore);
                if (beta <= alpha && settings.alphaBetaPruning) {
                    break;
                }
            }
        }
        return bestScore;
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

        LinkedList<Move> bestMoves = new LinkedList<>();
        if(settings.iterativeDeepening) {
            iterativeDeepeningDepth = settings.iterativeDeepeningStartDepth;
            while(!stopCalculating && iterativeDeepeningDepth <= settings.depth && !checkmate) {
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

        if(settings.moveOrdering) {
            Collections.sort(bestMoves);
            System.out.println(getName() + "'s best moves: " + bestMoves);
            return bestMoves.getFirst();
        } else {
            return bestMoves.get((int) (Math.random() * bestMoves.size()));
        }
    }

    public String getName() {
        return settings.name;
    }

    public void printReport(int size) {
        System.out.printf("Reached a depth of %d, chose one out of %d equal moves, and evaluated %,d moves%n", iterativeDeepeningDepth, size, stateCounter);
        stateCounter = 0;
    }
}
