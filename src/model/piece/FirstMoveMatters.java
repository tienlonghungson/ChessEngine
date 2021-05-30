package src.model.piece;

/**
 * interface for pieces whose first move matter
 * for example : the first move of {@code Pawn} is whether forward 1 or 2
 */
public interface FirstMoveMatters {
    boolean getHasMoved();
    void setHasMoved(boolean hasMoved);
}
