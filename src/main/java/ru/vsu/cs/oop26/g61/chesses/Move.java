package ru.vsu.cs.oop26.g61.chesses;

import ru.vsu.cs.oop26.g61.chesses.figures.Figure;

public record Move(
    Cell from,
    Cell to,
    Figure figure,
    Figure inTarget,
    MoveType type,
    FigureType morphTo){

    public enum MoveType {
        NORMAL,              // Обычный ход
        CASTLING_KING_SIDE,  // Короткая рокировка
        CASTLING_QUEEN_SIDE, // Длинная рокировка
        EN_PASSANT           // Взятие на проходе
    }
    public Move(Cell from, Cell to, Figure figure){
        this(from, to, figure, null, MoveType.NORMAL, null);
    }
    public Move(Cell from, Cell to, Figure figure, Figure inTarget){
        this(from, to, figure, inTarget, MoveType.NORMAL, null);
    }
}

