package ru.vsu.cs.oop26.g61.chesses;
import java.util.Objects;

public final class Position {
    private final Board board;
    private final Color activeColor; // Чей ход
    private final boolean whiteKingSideCastle;  // Право на короткую рокировку белых
    private final boolean whiteQueenSideCastle; // Право на длинную рокировку белых
    private final boolean blackKingSideCastle;  // Право на короткую рокировку черных
    private final boolean blackQueenSideCastle; // Право на длинную рокировку черных
    private final Cell enPassantCell;       // Клетка для взятия на проходе (или null)
    private final int halfMoveClock;            // Счетчик для правила 50 ходов
    private final int fullMoveClock;

    public Position(Board board, Color activeColor, boolean whiteKingSideCastle, boolean whiteQueenSideCastle,
                    boolean blackKingSideCastle, boolean blackQueenSideCastle,
                    Cell enPassantCell, int halfMoveClock, int fullMoveClock) {
        this.board = new Board(board); // Сохраняем копию доски!
        this.activeColor = activeColor;
        this.whiteKingSideCastle = whiteKingSideCastle;
        this.whiteQueenSideCastle = whiteQueenSideCastle;
        this.blackKingSideCastle = blackKingSideCastle;
        this.blackQueenSideCastle = blackQueenSideCastle;
        this.enPassantCell = enPassantCell;
        this.halfMoveClock = halfMoveClock;
        this.fullMoveClock = fullMoveClock;
    }

    public Board getBoard(){
        return new Board(board);
    }
    public Color getActiveColor() { return activeColor; }
    public boolean hasWhiteKingSideCastle() { return whiteKingSideCastle; }
    public boolean hasWhiteQueenSideCastle() { return whiteQueenSideCastle; }
    public boolean hasBlackKingSideCastle() { return blackKingSideCastle; }
    public boolean hasBlackQueenSideCastle() { return blackQueenSideCastle; }
    public Cell getEnPassantSquare() { return enPassantCell; }
    public int getHalfMoveClock() { return halfMoveClock; }
    public int getFullMoveClock() { return fullMoveClock; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return activeColor == position.activeColor &&
                whiteKingSideCastle == position.whiteKingSideCastle &&
                whiteQueenSideCastle == position.whiteQueenSideCastle &&
                blackKingSideCastle == position.blackKingSideCastle &&
                blackQueenSideCastle == position.blackQueenSideCastle &&
                Objects.equals(board, position.board) &&
                Objects.equals(enPassantCell, position.enPassantCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeColor, whiteKingSideCastle, whiteQueenSideCastle,
                blackKingSideCastle, blackQueenSideCastle, board, enPassantCell);
    }
}
