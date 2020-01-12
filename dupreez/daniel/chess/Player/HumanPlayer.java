package dupreez.daniel.chess.Player;

import dupreez.daniel.chess.AI.Settings;
import dupreez.daniel.chess.move.Move;
import dupreez.daniel.chess.move.MoveConformationWindow;
import dupreez.daniel.chess.piece.Piece;
import dupreez.daniel.position.Position;
import dupreez.daniel.position.PositionMap;

import java.util.LinkedList;

public class HumanPlayer extends Player
{

    private Piece highlightedPiece;
    private PositionMap<Move> positionMap;

    public HumanPlayer(boolean isWhite)
    {
        super(isWhite);
    }

    public void calculateNextMove()
    {

    }

    public void forwardBoardInput(Position position)
    {
        board.clearHighlights();
        if(highlightedPiece == null)
        {
            Piece piece = board.getPiece(position);
            if(piece != null && piece.isWhite() == isWhite)
            {
                board.highlight(position);
                highlightedPiece = piece;
                LinkedList<Move> moves = piece.getValidMoves();
                positionMap = Piece.getMoveMap(moves);
                if(Settings.showMoves)
                    for (Move move: moves)
                        board.highlight(move.getEndPosition());
            }
        }
        else
        {
            if (positionMap != null)
            {
                LinkedList<Move> moves = new LinkedList<>();
                Move move = positionMap.remove(position);
                while (move != null)
                {
                    moves.add(move);
                    move = positionMap.remove(position);
                }
                highlightedPiece = null;
                positionMap = null;
                if (moves.size() == 1)
                    returnMove(moves.get(0));
                else if(moves.size() > 1)
                {
                    returnMove(MoveConformationWindow.display("Confirm Move", "Which move would you like to take?", moves));
                }
                else
                    forwardBoardInput(position);
            }
            else
            {
                board.highlight(position);
            }
        }
    }

    public void stop()
    {

    }

    public String toString()
    {
        return "Human";
    }
}
