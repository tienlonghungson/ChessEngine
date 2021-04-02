package src.chess.piece;

import src.chess.Board.Board;
import src.chess.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Knight extends Piece
{
    public static final int SCORE = 3;
    public static final String ID = "Kn";
    public static final String NAME = "Knight";

    public Knight (Position position, boolean isWhite, Board board)
    {
        super(position, isWhite, board);
    }

    @Override
    public LinkedList<Move> getMoves()
    {
        LinkedList<Move> moves = new LinkedList<>();

        getMovesHelper(2, 1, moves, board);
        getMovesHelper(2, -1, moves, board);
        getMovesHelper(-2, 1, moves, board);
        getMovesHelper(-2, -1, moves, board);
        getMovesHelper(1, 2, moves, board);
        getMovesHelper(-1, 2, moves, board);
        getMovesHelper(1, -2, moves, board);
        getMovesHelper(-1, -2, moves, board);

        return moves;
    }

    private void getMovesHelper(int colOffset, int rowOffset, LinkedList<Move> moves, Board board)
    {
        Position temp = this.position.getPositionWithOffset(colOffset, rowOffset);
        if(board.inBounds(temp) && !board.hasFriendlyPieceAtPosition(temp, isWhite))
            moves.add(new Move(this, board, temp));
    }

    @Override
    public int getScore()
    {
        return SCORE;
    }

    @Override
    public String getID()
    {
        return ID;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    public static Knight parseKnight(String[] data, Board board)
    {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);

        return new Knight(position, isWhite, board);
    }
}
