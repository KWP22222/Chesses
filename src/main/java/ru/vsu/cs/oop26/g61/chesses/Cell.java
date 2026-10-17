package ru.vsu.cs.oop26.g61.chesses;

public record Cell(char symbol, int num) {
    // Валидация при создании (compact constructor)
    public Cell {
        if (symbol < 'a' || symbol > 'h') {
             throw  new IllegalArgumentException("Please, enter a letter of cell (a - h)");
        }
        if (num < 0 || num > 8) {
            throw  new IllegalArgumentException("Please, enter a number of cell (0 - 8)");
        }
    }

    /**
     * Возвращает строковое представление клетки (например, "e4").
     */
    @Override
    public String toString() {
        return String.valueOf(symbol) + String.valueOf(num);
    }
}
