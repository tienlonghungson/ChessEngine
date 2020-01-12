package dupreez.daniel.chess.Board;

public enum Mode
{
    COMPUTER_VS_COMPUTER(), PLAYER_VS_PLAYER(), COMPUTER_VS_PLAYER();

    public String toString()
    {
        return this.name();
    }
}
