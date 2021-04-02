package src.chess.piece;

import src.chess.Board.Board;
import src.chess.move.FirstMove;
import src.chess.move.Move;
import src.position.Position;

import java.util.LinkedList;

public class Rook extends Piece implements FirstMoveMatters
{
    public static final int SCORE = 5;
    public static final String ID = "R";
    public static final String NAME = "Rook";

    public boolean hasMoved;

    public Rook (Position position, boolean isWhite, Board board)
    {
        this(position, isWhite, false, board);
    }

    public Rook(Position position, boolean isWhite, boolean hasMoved, Board board)
    {
        super(position, isWhite, board);
        this.hasMoved = hasMoved;
    }

    @Override
    public LinkedList<Move> getMoves()
    {
        LinkedList<Move> moves = new LinkedList<>();

        getMovesHelper(1, 0, moves);
        getMovesHelper(-1, 0, moves);
        getMovesHelper(0, 1, moves);
        getMovesHelper(0, -1, moves);

        return moves;
    }

    private void getMovesHelper(int rowInc, int colInc, LinkedList<Move> moves)
    {
        Position temp = this.position.getPositionWithOffset(rowInc, colInc);
        while(board.inBounds(temp))
        {
            if(board.hasFriendlyPieceAtPosition(temp, isWhite))
                break;
            else if(board.hasHostilePieceAtPosition(temp, isWhite))
            {
                moves.add(setupMove(temp.getPositionWithOffset()));
                break;
            }
            moves.add(setupMove(temp.getPositionWithOffset()));
            temp = temp.getPositionWithOffset(rowInc, colInc);
        }
    }

    private Move setupMove(Position position)
    {
        if(hasMoved)
            return new Move(this, board, position);
        else
            return new FirstMove(this, board, position);
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

    @Override
    public boolean getHasMoved()
    {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved)
    {
        this.hasMoved = hasMoved;
    }

    public static Rook parseRook(String[] data, Board board)
    {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);

        return new Rook(position, isWhite, hasMoved, board);
    }
}
