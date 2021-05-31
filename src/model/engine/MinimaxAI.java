package src.model.engine;

import javafx.util.Pair;
import src.model.board.Board;
import src.model.move.Castling;
import src.model.move.Move;
import src.controller.BoardController;
import src.model.move.Promotion;
import src.model.piece.King;
import src.model.piece.Queen;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MinimaxAI extends AI {
    private static final int QUIESCENCE_THRESHOLD = 1400;
    private static final boolean VISUAL_CALCULATIONS = false;
    private final int INF=99999999, MINUS_INF = -INF;
    private final boolean isWhite;
    private final AISettings settings;
    private int stateCounter = 0;
    private boolean checkmate;

    private boolean stopCalculating;
    private int iterativeDeepeningDepth;

//    int nullEffect=0;

    ScheduledExecutorService timer;

    private final int TABLE_SIZE= (int)1e8; // Transposition Table Size
    private HashMap<Long,HashEntry> table = new HashMap<>(1<<10);  // transposition table

    public MinimaxAI(boolean isWhite, AISettings settings) {
        this.isWhite = isWhite;
        this.settings = settings;
        stopCalculating = false;
    }

    public LinkedList<Move> calculateBestMove(int depth) {
        LinkedList<Move> moves = activeBoard.getAllValidMoves(isWhite);
        LinkedList<Move> bestMoves = new LinkedList<>();
        int bestScore;

        int alpha = MINUS_INF;
        int beta = INF;
        Collections.sort(moves);

        bestScore = MINUS_INF;
        for (Move move: moves) {
            move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(move);
//                System.out.println("Move "+move.toString()+"\tZobristKey: "+activeBoard.getZobristKey());
            int tempScore;
            tempScore = -getBestMoveHelper(!isWhite, alpha, beta, depth, activeBoard.hasNullMove(!isWhite));
            BoardController.undoMove(VISUAL_CALCULATIONS);
//                System.out.println("UndoMove "+move.toString()+"\tZobristKey: "+activeBoard.getZobristKey());
            if(tempScore > bestScore) {
                bestScore = tempScore;
                bestMoves = new LinkedList<>();
                bestMoves.add(move);
            } else if (tempScore == bestScore) {
                bestMoves.add(move);
            }

            if(!settings.addRandomness) {
                alpha = Integer.max(alpha, bestScore);
                if (beta <= alpha && settings.alphaBetaPruning) {
                    break;
                }
            }
        }
        if(bestScore == (isWhite ? INF : MINUS_INF)) {
            checkmate = true;
        }

        return bestMoves;
    }

    private int getBestMoveHelperTT(boolean isWhiteTurn, int alpha, int beta, int depth,boolean doNull) {
        if(stopCalculating) {
            return isWhiteTurn ? MINUS_INF : INF;
        }

        HashEntry entry = table.get(activeBoard.getZobristKey(isWhiteTurn));
        if (entry!=null && entry.depth>depth){
            if (entry.flag==FLAG.EXACT){
                stateCounter++;
                return entry.eval;
            }
            if (entry.flag==FLAG.LOWER_BOUND && entry.eval>alpha){
                alpha = entry.eval;
            } else if (entry.flag==FLAG.UPPER_BOUND && entry.eval<beta){
                beta = entry.eval;
            }
            if (alpha>=beta){
                stateCounter++;
                return entry.eval;
            }
        }
        int score;
        depth = Math.max(depth, 0);
        if(depth == 0) {
            stateCounter++;
            score = isWhiteTurn?(int)this.score():(int)-this.score();
            if (score<=alpha){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,score,0,null));
            } else if (score>=beta){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,score,0,null ));
            } else {
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,score,0,null ));
            }
//            System.out.println("Put "+activeBoard.getZobristKey(isWhiteTurn));
            return score;
        }
        LinkedList<Move> moves = activeBoard.getAllMoves(isWhiteTurn);
//        Collections.sort(moves);
        int i=0;
        int bestScore;
        bestScore = MINUS_INF;

        // null move
        if (doNull){
            bestScore = Integer.max(bestScore,-getBestMoveHelper(!isWhiteTurn,beta,beta+1,depth-3,false));
            if (bestScore>beta){
                stateCounter++;
                return bestScore;
            }
        }

//        // killer move
//        if (entry!=null && entry.move!=null && (moveScore(entry.move)>=QUIESCENCE_THRESHOLD)){
//            entry.move.doMove(VISUAL_CALCULATIONS);
//            BoardController.pushMove(entry.move);
//
//            score = -getBestMoveHelperTT(!isWhiteTurn,-beta,-alpha,depth-1,doNull);
//            if (score>bestScore){
//                bestScore=score;
//            }
//            if (bestScore<=alpha){
//                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,bestScore,0,entry.move));
//            } else if (bestScore>=beta){
//                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,bestScore,0,entry.move ));
//            } else {
//                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,bestScore,0,entry.move ));
//            }
//
//            BoardController.undoMove(VISUAL_CALCULATIONS);
//        }

        // all move
        for (Move move: moves) {
            move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(move);
            if (i++==0) {
                score = -getBestMoveHelperTT(!isWhiteTurn, -beta, -alpha, depth - 1,doNull);
                if (score > bestScore){
                    bestScore = score;
                }

            } else {
                score =  -getBestMoveHelperTT(!isWhiteTurn,-alpha-1,-alpha,depth-1,doNull);
                if (score>bestScore){
                    bestScore = score;
                }
                if (alpha<bestScore && bestScore<beta){
                    bestScore = -getBestMoveHelperTT(!isWhiteTurn,-beta,-bestScore,depth-1,doNull);
                }

            }

            BoardController.undoMove(VISUAL_CALCULATIONS);

            if (bestScore<=alpha){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,bestScore,0,move));
            } else if (bestScore>=beta){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,bestScore,0,move ));
            } else {
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,bestScore,0,move ));
            }
            alpha = Integer.max(alpha, bestScore);
            if (beta <= alpha && settings.alphaBetaPruning) {
                break;
            }
        }
        return alpha;
    }

    private int getBestMoveHelperHeuristic(boolean isWhiteTurn, int alpha, int beta, int depth,boolean doNull) {
        if(stopCalculating) {
            return isWhiteTurn ? MINUS_INF : INF;
        }

        HashEntry entry = table.get(activeBoard.getZobristKey(isWhiteTurn));
        if (entry!=null && entry.depth>depth){
            if (entry.flag==FLAG.EXACT){
                stateCounter++;
                return entry.eval;
            }
            if (entry.flag==FLAG.LOWER_BOUND && entry.eval>alpha){
                alpha = entry.eval;
            } else if (entry.flag==FLAG.UPPER_BOUND && entry.eval<beta){
                beta = entry.eval;
            }
            if (alpha>=beta){
                stateCounter++;
                return entry.eval;
            }
        }
        int score;
        depth = Math.max(depth, 0);
        if(depth == 0) {
            stateCounter++;
            score = isWhiteTurn?(int)this.score():(int)-this.score();
            if (score<=alpha){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,score,0,null));
            } else if (score>=beta){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,score,0,null ));
            } else {
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,score,0,null ));
                return score;
            }
//            System.out.println("Put "+activeBoard.getZobristKey(isWhiteTurn));
        }
        LinkedList<Move> moves = activeBoard.getAllMoves(isWhiteTurn);
//        Collections.sort(moves);
        int i=0;
        int bestScore;
        bestScore = MINUS_INF;

        // null move
        if (doNull){
            bestScore = Integer.max(bestScore,-getBestMoveHelper(!isWhiteTurn,beta,beta+1,depth-3,false));
            if (bestScore>beta){
                stateCounter++;
                return bestScore;
            }
        }

        // killer move
        if (entry!=null && entry.move!=null && (moveScore(entry.move)>=QUIESCENCE_THRESHOLD)){
            entry.move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(entry.move);

            score = -getBestMoveHelperTT(!isWhiteTurn,-beta,-alpha,depth-1,doNull);
            if (score>bestScore){
                bestScore=score;
            }
            if (bestScore<=alpha){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,bestScore,0,entry.move));
            } else if (bestScore>=beta){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,bestScore,0,entry.move ));
            } else {
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,bestScore,0,entry.move ));
            }

            BoardController.undoMove(VISUAL_CALCULATIONS);
        }

        // all move
        for (Move move: moves) {
            move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(move);
            if (i++==0) {
                score = -getBestMoveHelperTT(!isWhiteTurn, -beta, -alpha, depth - 1,doNull);
                if (score > bestScore){
                    bestScore = score;
                }

            } else {
                score =  -getBestMoveHelperTT(!isWhiteTurn,-alpha-1,-alpha,depth-1,doNull);
                if (score>bestScore){
                    bestScore = score;
                }
                if (alpha<bestScore && bestScore<beta){
                    bestScore = -getBestMoveHelperTT(!isWhiteTurn,-beta,-bestScore,depth-1,doNull);
                }

            }

            BoardController.undoMove(VISUAL_CALCULATIONS);

            if (bestScore<=alpha){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,bestScore,0,move));
            } else if (bestScore>=beta){
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,bestScore,0,move ));
            } else {
                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,bestScore,0,move ));
            }
            alpha = Integer.max(alpha, bestScore);
            if (beta <= alpha && settings.alphaBetaPruning) {
                break;
            }
        }
        return alpha;
    }

    private int getBestMoveHelper(boolean isWhiteTurn, int alpha, int beta, int depth,boolean doNull) {
        if(stopCalculating) {
            return isWhiteTurn ? MINUS_INF : INF;
        }
        if(depth <= 0) {
            stateCounter++;
            return isWhiteTurn?(int)this.score():(int)-this.score();
        }
        LinkedList<Move> moves = activeBoard.getAllMoves(isWhiteTurn);
//        Collections.sort(moves);
        int i=0;
        int bestScore;
        bestScore = MINUS_INF;

        // null move
        if (doNull){
            bestScore = Integer.max(bestScore,-getBestMoveHelper(!isWhiteTurn,-beta-1,-beta,depth-3,false));
            if (bestScore>beta){
                stateCounter++;
                return bestScore;
            }
        }
        for (Move move: moves) {
            move.doMove(VISUAL_CALCULATIONS);
            BoardController.pushMove(move);
            if (i++==0) {
                bestScore = Integer.max(bestScore, -getBestMoveHelper(!isWhiteTurn, -beta, -alpha, depth - 1,doNull));
            } else {
                bestScore = Integer.max(bestScore, -getBestMoveHelper(!isWhiteTurn,-alpha-1,-alpha,depth-1,doNull));
                if (alpha<bestScore && bestScore<beta){
                    bestScore = -getBestMoveHelper(!isWhiteTurn,-beta,-bestScore,depth-1,doNull);
                }
            }

            BoardController.undoMove(VISUAL_CALCULATIONS);
            alpha = Integer.max(alpha, bestScore);
            if (beta <= alpha && settings.alphaBetaPruning) {
                break;
            }
        }
        stateCounter++;
        return alpha;
    }

//    TODO: the code below hasn't been completed
//
//    public Pair<Integer,Move> getMove(boolean isWhiteTurn,int depth){
//        if (stopCalculating){
//            return (isWhiteTurn?new Pair<>(INF,null):new Pair<>(-INF,null));
//        }
//        if (depth==0){
//            stateCounter++;
//            return (isWhiteTurn?new Pair<>(activeBoard.score(),null):new Pair<>(-activeBoard.score(), null));
//        }
//        int bestScore = MINUS_INF, curScore;
//        Move bestMove=null;
//        LinkedList<Move> possibleMoves = activeBoard.getAllValidMoves(isWhite);
//        for (Move move:possibleMoves) {
//            move.doMove(VISUAL_CALCULATIONS);
//            BoardController.pushMove(move);
//            curScore = -(getMove(!isWhiteTurn,depth-1).getKey());
//            BoardController.undoMove(VISUAL_CALCULATIONS);
//
//            if (curScore>bestScore){
//                bestScore=curScore;
//                bestMove=move;
//            }
//        }
//        return new Pair<>(bestScore,bestMove);
//    }
//
//    public Pair<Integer,Move> alphaBeta(boolean isWhiteTurn, int depth, int alpha, int beta){
//        if (stopCalculating){
//            return (isWhiteTurn?new Pair<>(INF,null):new Pair<>(MINUS_INF,null));
//        }
//
//        LinkedList<Move> possibleMoves;
//        if (depth==0 || (possibleMoves=activeBoard.getAllValidMoves(isWhiteTurn)).size()==0){
////            return new Pair<>(activeBoard.score(), null);
//            stateCounter++;
//            return (isWhiteTurn?new Pair<>(activeBoard.score(),null):new Pair<>(-activeBoard.score(), null));
//        }
//        int bestScore = MINUS_INF, curScore;
//        Move bestMove = null;
//        for (Move move:possibleMoves) {
//            move.doMove(VISUAL_CALCULATIONS);
//            BoardController.pushMove(move);
//            curScore = -(alphaBeta(!isWhiteTurn,depth-1,-beta,-alpha).getKey());
//            BoardController.undoMove(VISUAL_CALCULATIONS);
//            if (curScore>bestScore){
//                bestScore = curScore;
//                bestMove = move;
//            }
//            if (bestScore>alpha){
//                alpha = bestScore;
//            }
//            if (bestScore >= beta){
//                break;
//            }
//        }
//        return new Pair<>(bestScore,bestMove);
//    }
//
//    public Pair<Integer,Move> alphaBetaTT(boolean isWhiteTurn,int depth, int alpha, int beta){
//        if (stopCalculating){
//            return (isWhiteTurn?new Pair<>(INF,null):new Pair<>(MINUS_INF,null));
//        }
//        HashEntry entry = table.get(activeBoard.getZobristKey(isWhite));
//        if (entry!=null){
//            System.out.println(entry.zobrist+" "+entry.depth+" "+entry.eval+" "+entry.flag);
//        }
//        if (entry!=null && entry.depth>=depth){
//            if (entry.flag==FLAG.EXACT){
//                stateCounter++;
//                return new Pair<>(entry.eval,null);
//            }
//            if (entry.flag==FLAG.LOWER_BOUND && entry.eval>alpha){
//                alpha = entry.eval;
//            } else if (entry.flag==FLAG.UPPER_BOUND && entry.eval<beta){
//                beta = entry.eval;
//            }
//            if (alpha>=beta){
//                stateCounter++;
//                return new Pair<>(entry.eval,null);
//            }
//        }
//
//        int score;
//        LinkedList<Move> possibleMove = activeBoard.getAllValidMoves(isWhiteTurn);
//        if (depth==0 || (possibleMove.size()==0)){
//            score = isWhiteTurn?activeBoard.score():-activeBoard.score();
//            if (score<=alpha){
//                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,score,0,null));
//            } else if (score>=beta){
//                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,score,0,null ));
//            } else {
//                table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,score,0,null ));
//            }
//            System.out.println("Put "+activeBoard.getZobristKey(isWhiteTurn));
//            stateCounter++;
//            return new Pair<>(score,null);
//        }
//        int bestScore= MINUS_INF;
//        Move bestMove = null;
//        for (Move move: possibleMove) {
//            move.doMove(VISUAL_CALCULATIONS);
//            BoardController.pushMove(move);
//            score = -alphaBetaTT(!isWhiteTurn,depth-1,-beta,-alpha).getKey();
//            BoardController.undoMove(VISUAL_CALCULATIONS);
//
//            if (score>bestScore){
//                bestScore=score;
//                bestMove=move;
//            }
//            if (bestScore>alpha){
//                alpha=bestScore;
//            }
//            if (bestScore>=beta){
//                break;
//            }
//        }
//        if (bestScore<=alpha){
//            table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.LOWER_BOUND,bestScore,0,null ));
//        } else if (bestScore>=beta){
//            table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.UPPER_BOUND,bestScore,0,null ));
//        } else {
//            table.put(activeBoard.getZobristKey(isWhiteTurn),new HashEntry(activeBoard.getZobristKey(isWhiteTurn),depth,FLAG.EXACT,bestScore,0,null ) );
//        }
//        stateCounter++;
//        return new Pair<>(bestScore,bestMove);
//    }

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
//        LinkedList<Move> bestMoves = new LinkedList<>();
        Move bestMove=null;
        Pair<Integer,Move> best = null, cur;
        if(settings.iterativeDeepening) {
            iterativeDeepeningDepth = settings.iterativeDeepeningStartDepth;
            while(!stopCalculating && iterativeDeepeningDepth <= settings.depth && !checkmate) {
                stateCounter=0;
                LinkedList<Move> moves = calculateBestMove(iterativeDeepeningDepth++);
//                bestMove = getMove(isWhite,iterativeDeepeningDepth++).getValue();
//                cur= alphaBeta(isWhite,iterativeDeepeningDepth++,MINUS_INF,INF);

                if(!stopCalculating) {
//                    bestMoves = moves;
//                    best=cur;
                }
            }
        } else {
//            bestMoves = calculateBestMove(settings.depth);
        }

//        System.out.println("Best move is "+bestMove.toString());

        stop();
        bestMove = best.getValue();
//        System.out.println("bestScore "+ best.getKey());
//        printReport(bestMoves.size());
        printReport(1);
//        System.out.println("Done Searching with Zobrist Key: "+activeBoard.getZobristKey(isWhite));
        if(settings.moveOrdering) {
//            Collections.sort(bestMoves);
//            System.out.println(getName() + "'s best moves: " + bestMove);
//            return bestMoves.getFirst();
            return bestMove;
        } else {
//            return bestMoves.get((int) (Math.random() * bestMoves.size()));
            return bestMove;
        }
    }

    @Override
    protected double score() {
        return activeBoard.score();
    }

    protected int moveScore(Move move){
        int moveScore = 0;
        if (move instanceof Promotion){
            moveScore+= Queen.SCORE;
        } else if (move instanceof Castling){
            moveScore+= King.SCORE;
        }
        if (move.getCapturedPiece()!=null){
            moveScore+=move.getCaptureScore();
        }
        moveScore+= move.getMovingScore();
        return moveScore;
    }

    public String getName() {
        return settings.name;
    }

    public void printReport(int size) {
        System.out.printf("Reached a depth of %d and evaluated %,d moves%n", iterativeDeepeningDepth, stateCounter);
//        System.out.printf("Reached a depth of %d", iterativeDeepeningDepth);//, size, stateCounter);
        stateCounter = 0;
    }
}

