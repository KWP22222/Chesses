package ru.vsu.cs.oop26.g61.chesses;

import ru.vsu.cs.oop26.g61.chesses.engine.ChessEngine;
import ru.vsu.cs.oop26.g61.chesses.exception.IllegalMoveException;
import ru.vsu.cs.oop26.g61.chesses.figures.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChessEngine engine = new ChessEngine();

        System.out.println("Застрял и не можешь придумать следующий ход? Не беда! Я помогу тебе ;)");
        System.out.println("1 - Начать стандартную партию");
        System.out.println("2 - Расставить фигуры вручную");
        System.out.println("3 - Выйти");
        System.out.print("Твой выбор: ");

        String choice = scanner.nextLine().trim();
        Position currentPosition;

        if (choice.equals("1")) {
            currentPosition = createInitialPosition();
            System.out.println("\nЗагружена стандартная начальная_POSITION.");
        } else if (choice.equals("2")) {
            System.out.println("\nРучная расстановка");
            System.out.println("Вводи координаты через пробел (например: 'e2 d2') или 'null', если фигур нет.");
            currentPosition = createManualPosition(scanner);
            System.out.println("Позиция создана");
        } else {
            System.out.println("До встречи! Удачи в следующих партиях.");
            return;
        }

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            printBoard(currentPosition);
            System.out.println("=".repeat(50));

            List<Move> legalMoves = engine.getLegalMoves(currentPosition);
            System.out.println("Ходят: " + (currentPosition.getActiveColor() == Color.WHITE ? "БЕЛЫЕ" : "ЧЕРНЫЕ"));

            if (legalMoves.isEmpty()) {
                if (engine.isInCheck(currentPosition)) {
                    System.out.println("Мат. Игра окончена.");
                } else {
                    System.out.println("Пат. Ничья.");
                }
                break;
            }

            System.out.print("\nВведите ход (например, 'e2 e4' или 'e7e8q'), или 'exit' для выхода: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Игра прервана. До встречи!");
                break;
            }

            try {
                Move moveToMake = parseMove(input, legalMoves);
                currentPosition = engine.makeMove(currentPosition, moveToMake);
                System.out.println("Ход сделан");

                if (engine.isInCheck(currentPosition)) {
                    System.out.println("Внимание: Шах.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка формата: " + e.getMessage() + ". Попробуйте еще раз (например, 'e2 e4').");
            } catch (IllegalMoveException e) {
                System.out.println("Недопустимый ход!");
                System.out.println("Причина: " + e.getReason());
            }
        }

        scanner.close();
    }

    
    private static void readFigurePositions(Scanner scanner, String prompt, Color color, FigureType type, Board board) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("null")) {
            return; // Фигур этого типа нет
        }

        String[] coords = input.split("\\s+");
        for (String coord : coords) {
            if (coord.isEmpty()) continue;
            try {
                char file = coord.charAt(0);
                int rank = Character.getNumericValue(coord.charAt(1));
                Cell sq = new Cell(file, rank);
                board.setFigureAt(sq, createFigure(type, color));
            } catch (Exception e) {
                System.out.println("Ошибка в координате '" + coord + "'. Пропускаем её.");
            }
        }
    }

    /**
     * Создает позицию путем опроса пользователя.
     */
    private static Position createManualPosition(Scanner scanner) {
        Board board = new Board();

        System.out.println("\n--- БЕЛЫЕ ФИГУРЫ ---");
        readFigurePositions(scanner, "Белые пешки (или 'null'): ", Color.WHITE, FigureType.PAWN, board);
        readFigurePositions(scanner, "Белые кони (или 'null'): ", Color.WHITE, FigureType.KNIGHT, board);
        readFigurePositions(scanner, "Белые слоны (или 'null'): ", Color.WHITE, FigureType.BISHOP, board);
        readFigurePositions(scanner, "Белые ладьи (или 'null'): ", Color.WHITE, FigureType.ROOK, board);
        readFigurePositions(scanner, "Белый ферзь (или 'null'): ", Color.WHITE, FigureType.QUEEN, board);
        readFigurePositions(scanner, "Белый король (или 'null'): ", Color.WHITE, FigureType.KING, board);

        System.out.println("\n--- ЧЕРНЫЕ ФИГУРЫ ---");
        readFigurePositions(scanner, "Черные пешки (или 'null'): ", Color.BLACK, FigureType.PAWN, board);
        readFigurePositions(scanner, "Черные кони (или 'null'): ", Color.BLACK, FigureType.KNIGHT, board);
        readFigurePositions(scanner, "Черные слоны (или 'null'): ", Color.BLACK, FigureType.BISHOP, board);
        readFigurePositions(scanner, "Черные ладьи (или 'null'): ", Color.BLACK, FigureType.ROOK, board);
        readFigurePositions(scanner, "Черный ферзь (или 'null'): ", Color.BLACK, FigureType.QUEEN, board);
        readFigurePositions(scanner, "Черный король (или 'null'): ", Color.BLACK, FigureType.KING, board);

        // По умолчанию считаем, что ходят белые, и все права на рокировку есть 
        return new Position(board, Color.WHITE, true, true, true, true, null, 0, 1);
    }

   
    private static Figure createFigure(FigureType type, Color color) {
        return switch (type) {
            case PAWN -> new Pawn(color);
            case KNIGHT -> new Knight(color);
            case BISHOP -> new Bishop(color);
            case ROOK -> new Rook(color);
            case QUEEN -> new Queen(color);
            case KING -> new King(color);
        };
    }


    private static Move parseMove(String input, List<Move> legalMoves) throws IllegalMoveException {
        input = input.toLowerCase().replaceAll("\\s+", "");
        if (input.length() < 4) {
            throw new IllegalArgumentException("Слишком короткий ввод. Формат: 'e2 e4'");
        }

        char fromFile = input.charAt(0);
        int fromRank = Character.getNumericValue(input.charAt(1));
        char toFile = input.charAt(2);
        int toRank = Character.getNumericValue(input.charAt(3));

        FigureType promotion = null;
        if (input.length() >= 5) {
            char promoChar = input.charAt(4);
            if (promoChar == 'q') promotion = FigureType.QUEEN;
            else if (promoChar == 'r') promotion = FigureType.ROOK;
            else if (promoChar == 'b') promotion = FigureType.BISHOP;
            else if (promoChar == 'n') promotion = FigureType.KNIGHT;
        }

        Cell from = new Cell(fromFile, fromRank);
        Cell to = new Cell(toFile, toRank);

        for (Move move : legalMoves) {
            if (move.from().equals(from) && move.to().equals(to)) {
                if (promotion != null && move.morphTo() == promotion) return move;
                if (promotion == null) return move;
            }
        }
        throw new IllegalMoveException("Этот ход отсутствует в списке легальных ходов для текущей позиции.");
    }


    private static void printBoard(Position position) {
        Board board = position.getBoard();
        System.out.println("  +------------------------+");
        for (int rank = 8; rank >= 1; rank--) {
            System.out.print(rank + " |");
            for (char file = 'a'; file <= 'h'; file++) {
                Cell sq = new Cell(file, rank);
                Figure figure = board.getFigureAt(sq);
                if (figure != null) {
                    System.out.print(" " + figure.getSymbol() + " ");
                } else {
                    System.out.print(" . ");
                }
            }
            System.out.println("|");
        }
        System.out.println("  +------------------------+");
        System.out.println("    a  b  c  d  e  f  g  h");
    }


    private static Position createInitialPosition() {
        Board board = new Board();
        board.setFigureAt(new Cell('a', 8), new Rook(Color.BLACK));
        board.setFigureAt(new Cell('b', 8), new Knight(Color.BLACK));
        board.setFigureAt(new Cell('c', 8), new Bishop(Color.BLACK));
        board.setFigureAt(new Cell('d', 8), new Queen(Color.BLACK));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));
        board.setFigureAt(new Cell('f', 8), new Bishop(Color.BLACK));
        board.setFigureAt(new Cell('g', 8), new Knight(Color.BLACK));
        board.setFigureAt(new Cell('h', 8), new Rook(Color.BLACK));
        for (char file = 'a'; file <= 'h'; file++) board.setFigureAt(new Cell(file, 7), new Pawn(Color.BLACK));

        for (char file = 'a'; file <= 'h'; file++) board.setFigureAt(new Cell(file, 2), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('a', 1), new Rook(Color.WHITE));
        board.setFigureAt(new Cell('b', 1), new Knight(Color.WHITE));
        board.setFigureAt(new Cell('c', 1), new Bishop(Color.WHITE));
        board.setFigureAt(new Cell('d', 1), new Queen(Color.WHITE));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('f', 1), new Bishop(Color.WHITE));
        board.setFigureAt(new Cell('g', 1), new Knight(Color.WHITE));
        board.setFigureAt(new Cell('h', 1), new Rook(Color.WHITE));

        return new Position(board, Color.WHITE, true, true, true, true, null, 0, 1);
    }
}