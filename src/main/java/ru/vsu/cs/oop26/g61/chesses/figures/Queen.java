package ru.vsu.cs.oop26.g61.chesses.figures;

import ru.vsu.cs.oop26.g61.chesses.Color;
import ru.vsu.cs.oop26.g61.chesses.FigureType;

public record Queen(Color color) implements Figure{
    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'Q' : 'q';
    }

    @Override
    public FigureType getType() {
        return FigureType.QUEEN;
    }
    @Override
    public Color getColor() {return color;}
}
