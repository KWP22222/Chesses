package ru.vsu.cs.oop26.g61.chesses.figures;

import ru.vsu.cs.oop26.g61.chesses.Color;
import ru.vsu.cs.oop26.g61.chesses.FigureType;

public record Knight(Color color) implements Figure {
    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'N' : 'n';
    }

    @Override
    public FigureType getType() {
        return FigureType.KNIGHT;
    }
    @Override
    public Color getColor() {return color;}
}
