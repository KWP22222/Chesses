package ru.vsu.cs.oop26.g61.chesses.figures;

import ru.vsu.cs.oop26.g61.chesses.Color;
import ru.vsu.cs.oop26.g61.chesses.FigureType;

public sealed interface Figure permits King, Queen, Pawn, Bishop, Knight, Rook{
    char getSymbol();
    Color getColor();
    FigureType getType();
}
