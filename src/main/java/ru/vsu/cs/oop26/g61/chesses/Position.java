package ru.vsu.cs.oop26.g61.chesses;
import java.util.Objects;

public final class Position {
    private final Board board;
    private final Color activeColor;
    private final int halfMoveClock;
    private final int fullMoveClock;


    public Position(Board board, Color activeColor, int halfMoveClock, int fullMoveClock) {
        this.board = new Board(board);
        this.activeColor = activeColor;
        this.halfMoveClock = halfMoveClock;
        this.fullMoveClock = fullMoveClock;
    }

    public Board getBoard(){
        return new Board(board);
    }
    public Color getColor(){ return activeColor; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return halfMoveClock == position.halfMoveClock && fullMoveClock == position.fullMoveClock && Objects.equals(board, position.board) && activeColor == position.activeColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, activeColor, halfMoveClock, fullMoveClock);
    }
}
