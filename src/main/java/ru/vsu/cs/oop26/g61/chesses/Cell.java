package ru.vsu.cs.oop26.g61.chesses;

public record Cell(char symbol, int num) {
    public Cell(char symbol, int num) {
        if (symbol < 'a' || symbol > 'h') {
             throw  new IllegalArgumentException("Please, enter a letter of cell (a - h)");
        }
        if (num < 0 || num > 8) {
            throw  new IllegalArgumentException("Please, enter a number of cell (0 - 8)");
        }
    }
    public String toString() {
        return String.valueOf(symbol) + String.valueOf(num);
    }
    public Color getColor() {
        Color color = Color.WHITE;
        if ((symbol - 'a' + num - 1) % 2 == 0) {
            color = Color.BLACK;
        }
        return color;
    }
}
