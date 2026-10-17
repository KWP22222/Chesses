package ru.vsu.cs.oop26.g61.chesses.demo;

import ru.vsu.cs.oop26.g61.chesses.*;
import ru.vsu.cs.oop26.g61.chesses.engine.ChessEngine;
import ru.vsu.cs.oop26.g61.chesses.exception.IllegalMoveException;
import ru.vsu.cs.oop26.g61.chesses.figures.*;
import ru.vsu.cs.oop26.g61.chesses.figures.Figure.*;
import java.util.List;

/**
 * Демонстрация шахматного движка.
 * Показывает все операции базового уровня.
 */
public class Demo {

    public static void main(String[] args) throws IllegalMoveException {
        ChessEngine engine = new ChessEngine();

        System.out.println("1. Создание начальной позиции...");
        Position initialPosition = createInitialPosition();
        System.out.println("Ходят: " + initialPosition.getActiveColor());
        System.out.println("Количество фигур на доске: " + countPieces(initialPosition.getBoard()));


        System.out.println("\n2. Легальные ходы белых в начальной позиции:");
        List<Move> legalMoves = engine.getLegalMoves(initialPosition);
        System.out.println("Всего ходов: " + legalMoves.size());
        System.out.println("Первые 5 ходов:");
        for (int i = 0; i < Math.min(5, legalMoves.size()); i++) {
            Move move = legalMoves.get(i);
            System.out.println("   " + (i + 1) + ". " + move.from() + " -> " + move.to());
        }


        System.out.println("\n3. Применение хода: e2 -> e4");
        Move moveE2E4 = findMove(legalMoves, new Cell('e', 2), new Cell('e', 4));
        if (moveE2E4 != null) {
            Position positionAfterE4 = engine.makeMove(initialPosition, moveE2E4);
            System.out.println("Теперь ходят: " + positionAfterE4.getActiveColor());


            System.out.println("\n4. Проверка шаха:");
            boolean isCheck = engine.isInCheck(positionAfterE4);
            System.out.println("Король под шахом? " + (isCheck ? "да" : "нет"));


            System.out.println("\n5. Попытка сделать недопустимый ход (король e1 -> e2):");
            try {
                Move illegalMove = new Move(new Cell('e', 1), new Cell('e', 2),
                        new King(Color.WHITE));
                engine.makeMove(positionAfterE4, illegalMove);
                System.out.println("Ошибка: Ход должен был быть отклонен!");
            } catch (IllegalMoveException e) {
                System.out.println("Исключение поймано: " + e.getMessage());
                System.out.println("Причина: " + e.getReason());
            }


            System.out.println("\n6. Взятие фигуры:");
            //белая ладья может взять черную пешку
            Position capturePosition = createCapturePosition();
            List<Move> captureMoves = engine.getLegalMoves(capturePosition);
            System.out.println("Легальных ходов: " + captureMoves.size());
            for (Move move : captureMoves) {
                if (move.inTarget() != null) {
                    System.out.println("Ход со взятием: " + move.from() + " -> " + move.to()
                            + " (взята: " + move.inTarget().getSymbol() + ")");
                }
            }


            System.out.println("\n7. Демонстрация рокировки:");
            Position castlingPosition = createCastlingPosition();
            List<Move> castlingMoves = engine.getLegalMoves(castlingPosition);
            System.out.println("Позиция для рокировки создана.");
            for (Move move : castlingMoves) {
                if (move.type() == Move.MoveType.CASTLING_KING_SIDE) {
                    System.out.println("Найдена короткая рокировка: " + move.from() + " -> " + move.to());
                } else if (move.type() == Move.MoveType.CASTLING_QUEEN_SIDE) {
                    System.out.println("Найдена длинная рокировка: " + move.from() + " -> " + move.to());
                }
            }


            System.out.println("\n8. Демонстрация взятия на проходе:");
            Position enPassantPosition = createEnPassantPosition();
            List<Move> enPassantMoves = engine.getLegalMoves(enPassantPosition);
            System.out.println("   Позиция для взятия на проходе создана.");
            for (Move move : enPassantMoves) {
                if (move.type() == Move.MoveType.EN_PASSANT) {
                    System.out.println("   Найдено взятие на проходе: " + move.from() + " -> " + move.to());
                }
            }

            // 9. Демонстрация превращения пешки
            System.out.println("\n9. Демонстрация превращения пешки:");
            Position promotionPosition = createPromotionPosition();
            List<Move> promotionMoves = engine.getLegalMoves(promotionPosition);
            System.out.println("Позиция для превращения пешки создана.");
            System.out.println("Количество ходов превращения: " + promotionMoves.stream()
                    .filter(m -> m.morphTo() != null).count());
            for (Move move : promotionMoves) {
                if (move.morphTo() != null) {
                    System.out.println("Превращение: " + move.from() + " -> " + move.to()
                            + " в " + move.morphTo());
                }
            }
        }
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

        for (char file = 'a'; file <= 'h'; file++) {
            board.setFigureAt(new Cell(file, 7), new Pawn(Color.BLACK));
        }

        for (char file = 'a'; file <= 'h'; file++) {
            board.setFigureAt(new Cell(file, 2), new Pawn(Color.WHITE));
        }

        board.setFigureAt(new Cell('a', 1), new Rook(Color.WHITE));
        board.setFigureAt(new Cell('b', 1), new Knight(Color.WHITE));
        board.setFigureAt(new Cell('c', 1), new Bishop(Color.WHITE));
        board.setFigureAt(new Cell('d', 1), new Queen(Color.WHITE));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('f', 1), new Bishop(Color.WHITE));
        board.setFigureAt(new Cell('g', 1), new Knight(Color.WHITE));
        board.setFigureAt(new Cell('h', 1), new Rook(Color.WHITE));

        return new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);
    }

    /**
     * Создает позицию для демонстрации взятия.
     */
    private static Position createCapturePosition() {
        Board board = new Board();
        board.setFigureAt(new Cell('e', 4), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('d', 5), new Pawn(Color.BLACK));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        return new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);
    }


    private static Position createCastlingPosition() {
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('h', 1), new Rook(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        return new Position(board, Color.WHITE,
                true, false, false, false,
                null, 0, 1);
    }

    private static Position createEnPassantPosition() {
        Board board = new Board();
        board.setFigureAt(new Cell('e', 5), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('d', 7), new Pawn(Color.BLACK));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Cell enPassantSquare = new Cell('d', 6);

        return new Position(board, Color.WHITE,
                true, true, true, true,
                enPassantSquare, 0, 1);
    }

    private static Position createPromotionPosition() {
        Board board = new Board();
        board.setFigureAt(new Cell('e', 7), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        return new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);
    }

    private static Move findMove(List<Move> moves, Cell from, Cell to) {
        return moves.stream()
                .filter(m -> m.from().equals(from) && m.to().equals(to))
                .findFirst()
                .orElse(null);
    }

    private static int countPieces(Board board) {
        int count = 0;
        for (int rank = 1; rank <= 8; rank++) {
            for (char file = 'a'; file <= 'h'; file++) {
                if (board.getFigureAt(new Cell(file, rank)) != null) {
                    count++;
                }
            }
        }
        return count;
    }
}