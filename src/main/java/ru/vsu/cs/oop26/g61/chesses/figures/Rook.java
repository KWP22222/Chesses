package ru.vsu.cs.oop26.g61.chesses.figures;

import ru.vsu.cs.oop26.g61.chesses.Color;
import ru.vsu.cs.oop26.g61.chesses.FigureType;

public record Rook(Color color) implements Figure {
    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public FigureType getType() {
        return FigureType.ROOK;
    }
}
