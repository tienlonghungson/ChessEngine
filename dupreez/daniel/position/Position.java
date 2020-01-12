package dupreez.daniel.position;

import dupreez.daniel.chess.Board.Board;

/**
 * Position object used to store the position of a piece
 *
 * NOTE! While the object parses data in the form of row (from 8 to 1) and column (from A to E),
 * the object stores the data in the form of integer positions ex. row (from 0 to 7) and column (from 0 to 7).
 * Rows are stored from 0 to 8 but is outputted from 8 to 1
 * ex. "1A" = R: 7, C: 0
 *
 * @author Daniël du Preez
 */
public class Position
{

    private int row;
    private int column;

    /**
     * Constructor for a position object
     *
     * @param row The value for the row
     * @param column The value for the column
     */
    public Position(int row, int column)
    {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row value of the position
     *
     * @return Returns the row value
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Returns the column value of the position
     *
     * @return Returns the column value
     */
    public int getCol()
    {
        return column;
    }

    /**
     * Returns a new Position object with the same row and column value as the Position object it was called from
     *
     * @return Returns a new Position object
     */
    public Position getPosition()
    {
        return new Position(this.row, this.column);
    }

    /**
     * Returns a new Position object that is offset from the position of the object it was called from
     *
     * @param row the row offset
     * @param column the column offset
     * @return Returns a new Position object
     */
    public Position getPosition(int row, int column)
    {
        return new Position(this.row + row, this.column + column);
    }

    /**
     * Checks to see if an object equals this position
     *
     * @param o The Object to be checked
     * @return Returns true if o is a Position object and has the same row and column value
     */
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Position)
        {
            Position temp = (Position)o;
            if(temp.getRow() == this.getRow() && temp.getCol() == this.getCol())
                return true;
        }
        return false;
    }

    /**
     * Parses and returns a Position object from a raw String.
     * The String should be in the form of a single digit followed by a single uppercase letter
     *
     * @param rawData A String ex. "1A" or "8E"
     * @return Returns a new Position object
     */
    public static Position parsePosition(String rawData)
    {
        int row = 8 - (rawData.charAt(0) - 48);
        int column = rawData.charAt(1) - 65;

        if(row < 0 || row >= Board.ROWS || column < 0 || column >= Board.COLUMNS)
            return null;
        else
            return new Position(row, column);
    }

    /**
     * Prints the Position in the form of a single digit followed by a single uppercase character
     * The digit represents the row
     * The character represents the column
     *
     * @return Returns a string representation of a Position
     */
    public String toString()
    {
       return String.format("%d%c", Board.ROWS - row, (char)(column + 65));
    }

    public static int distanceFromMiddle(Position position)
    {
        return (int)Math.abs(((Board.ROWS - 1) / 2.0) - position.getRow()) + (int)Math.abs(((Board.COLUMNS - 1) / 2.0) - position.getCol());
    }
}
