package ru.vsu.cs.oop26.g61.chesses.engine;

import ru.vsu.cs.oop26.g61.chesses.Board;
import ru.vsu.cs.oop26.g61.chesses.Color;
import ru.vsu.cs.oop26.g61.chesses.Move;
import ru.vsu.cs.oop26.g61.chesses.Position;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    public List<Move> allLegalMoves(Position position){
        List<Move> moves = new ArrayList<>();
        Board board = position.getBoard();
        Color activeColor = position.getColor();
    }
}
