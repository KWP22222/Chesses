package ru.vsu.cs.oop26.g61.chesses;

import ru.vsu.cs.oop26.g61.chesses.figures.Figure;

import java.util.Arrays;

public class Board {
    private final Figure[][] cells;

    /**
     * Создает пустую доску.
     */
    public Board(){
        this.cells = new Figure[8][8];
    }

    /**
     * Создает копию другой доски для неизменяемости позиций.
     */
    public Board(Board other) {
        this.cells = new Figure[8][8];
        for (int i = 0; i < 8; i++){
            this.cells[i] = Arrays.copyOf(other.cells[i], 8);
        }
    }

    /**
     * Получает фигуру на заданной клетке.
     * @return фигуру или null, если клетка пуста
     */
    public Figure getFigureAt(Cell cell){
        return cells[cell.num() - 1][cell.symbol() - 'a'];
    }

    /**
     * Устанавливает фигуру на клетку.
     * @param cell клетка
     * @param figure фигура (или null, чтобы убрать фигуру)
     */
    public void setFigureAt(Cell cell, Figure figure){
        cells[cell.num() - 1][cell.symbol() - 'a'] = figure;
    }

    /**
     * Проверяет, пуста ли клетка.
     */
    public boolean isEmpty(Cell cell){
        return getFigureAt(cell) == null;
    }

    /**
     * Возвращает КОПИЮ массива доски.
     * (не возвращаем оригинальный массив, чтобы пользователь не мог его изменить)
     */
    public Figure[][] getBoardCopy(){
        Figure[][] copy = new Figure[8][8];
        for (int i = 0; i < 8; i++){
            copy[i] = Arrays.copyOf(cells[i], 8);
        }
        return copy;
    }
}
