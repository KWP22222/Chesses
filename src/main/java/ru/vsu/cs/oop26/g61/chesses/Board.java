package ru.vsu.cs.oop26.g61.chesses;

import ru.vsu.cs.oop26.g61.chesses.figures.Figure;

import java.util.Arrays;

public class Board {
    private final Figure[][] cells;

    public Board(){
        this.cells = new Figure[8][8];
    }

    public Board(Board other) {
        this.cells = new Figure[8][8];
        for (int i = 0; i < 8; i++){
            this.cells[i] = Arrays.copyOf(other.cells[i], 8);
        }
    }

    public Figure getFigureAt(Cell cell){
        return cells[cell.num() - 1][cell.symbol() - 'a'];
    }

    public void setFigureAt(Cell cell, Figure figure){
        cells[cell.num() - 1][cell.symbol() - 'a'] = figure;
    }

    public boolean isEmpty(Cell cell){
        return getFigureAt(cell) == null;
    }

    public Figure[][] getBoardCopy(){
        Figure[][] copy = new Figure[8][8];
        for (int i = 0; i < 8; i++){
            copy[i] = Arrays.copyOf(cells[i], 8);
        }
        return copy;
    }
}
