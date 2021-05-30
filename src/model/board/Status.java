package src.model.board;

public enum Status {
    WHITE(), BLACK(), FREEZE(), CREATIVE();

    public String toString() {
        return this.name();
    }
}
