package dupreez.daniel.chess.piece;

import dupreez.daniel.chess.Board.Board;
import dupreez.daniel.chess.move.CastleMove;
import dupreez.daniel.chess.move.FirstMove;
import dupreez.daniel.chess.move.Move;
import dupreez.daniel.position.Position;

import java.util.LinkedList;

public class King extends Piece implements FirstMoveMatters
{
    public static final int SCORE = 400;
    public static final String ID = "K";
    public static final String NAME = "King";


    public boolean hasMoved;

    public King (Position position, boolean isWhite, Board board)
    {
        this(position, isWhite, false, board);
    }

    public King(Position position, boolean isWhite, boolean hasMoved, Board board)
    {
        super(position, isWhite, board);
        this.hasMoved = hasMoved;
    }

    @Override
    public LinkedList<Move> getMoves()
    {
        LinkedList<Move> moves = new LinkedList<>();

        moves.addAll(getMovesNoCastle());
        moves.addAll(getMovesCastle());

        return moves;
    }

    public LinkedList<Move> getMovesNoCastle()
    {
        LinkedList<Move> moves = new LinkedList<>();

        getMovesHelper(1, 0, moves, board);
        getMovesHelper(1, 1, moves, board);
        getMovesHelper(0, 1, moves, board);
        getMovesHelper(-1, 1, moves, board);
        getMovesHelper(-1, 0, moves, board);
        getMovesHelper(-1, -1, moves, board);
        getMovesHelper(0, -1, moves, board);
        getMovesHelper(1, -1, moves, board);

        return moves;
    }

    public void getMovesHelper(int rowOffset, int colOffset, LinkedList<Move> moves, Board board)
    {
        Position temp = this.position.getPosition(colOffset, rowOffset);
        if(board.inBounds(temp) && !board.hasFriendlyPieceAtPosition(temp, isWhite))
            moves.add(setupMove(temp));
    }

    public LinkedList<Move> getMovesCastle()
    {
        LinkedList<Move> moves = new LinkedList<>();

        if(!hasMoved && board.isSafeMove(this.position, isWhite))
        {
            Piece leftPiece = board.getPiece(new Position(position.getRow(), 0));
            if(leftPiece instanceof Rook)
            {
                if (!((Rook) leftPiece).hasMoved)
                {
                    boolean canCastle = true;
                    for (int i = position.getCol() - 1; i > 0 && canCastle; i--)
                        if(!board.isCleanMove(new Position(position.getRow(), i), isWhite))
                            canCastle = false;
                    if(canCastle)
                        moves.add(new CastleMove(this, leftPiece, position.getPosition(0, -2), position.getPosition(0, -1), board));
                }
            }

            Piece rightPiece = board.getPiece(new Position(position.getRow(), Board.COLUMNS - 1));
            if(rightPiece instanceof Rook)
            {
                if (!((Rook) rightPiece).hasMoved)
                {
                    boolean canCastle = true;
                    for (int i = position.getCol() + 1; i < Board.COLUMNS - 1 && canCastle; i++)
                        if(!board.isCleanMove(new Position(position.getRow(), i), isWhite))
                            canCastle = false;
                    if(canCastle)
                        moves.add(new CastleMove(this, rightPiece, position.getPosition(0, 2), position.getPosition(0, 1), board));
                }
            }
        }

        return moves;
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

    public static King parseKing(String[] data, Board board)
    {
        Position position = Position.parsePosition(data[1] + data[2]);
        boolean isWhite = Boolean.parseBoolean(data[3]);
        boolean hasMoved = Boolean.parseBoolean(data[4]);

        return new King(position, isWhite, hasMoved, board);
    }
}
