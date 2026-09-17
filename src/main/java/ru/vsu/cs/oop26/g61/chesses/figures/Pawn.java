package ru.vsu.cs.oop26.g61.chesses.figures;

import ru.vsu.cs.oop26.g61.chesses.Color;
import ru.vsu.cs.oop26.g61.chesses.FigureType;

public record Pawn(Color color) implements Figure {
    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'P' : 'p';
    }

    @Override
    public FigureType getType() {
        return FigureType.PAWN;
    }
}
