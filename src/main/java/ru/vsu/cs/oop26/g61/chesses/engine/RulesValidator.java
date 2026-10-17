package ru.vsu.cs.oop26.g61.chesses.engine;

import ru.vsu.cs.oop26.g61.chesses.*;
import ru.vsu.cs.oop26.g61.chesses.figures.*;

import java.util.ArrayList;
import java.util.List;

import static ru.vsu.cs.oop26.g61.chesses.FigureType.*;

public class RulesValidator {
    private final MoveGenerator moveGenerator = new MoveGenerator();

    private Cell findKing(Board board, Color color) {
        for (int num = 1; num <= 8; num++) {
            for (char sy = 'a'; sy <= 'h'; sy++) {
                Cell cell = new Cell(sy, num);
                Figure figure = board.getFigureAt(cell);
                if (figure instanceof King king && king.getColor() == color) {
                    return cell;
                }
            }
        }
        return null;
    }

    /**
     * Проверяет, атакована ли клетка cell фигурами цвета attackerColor.
     */
    public boolean isCellAttacked(Position position, Cell cell, Color attackerColor) {
        Board board = position.getBoard();
        int[][] knightOffsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] offset : knightOffsets) {
            Cell c = getCellIfExists(cell, offset[0], offset[1]);
            if (c != null && board.getFigureAt(c) instanceof Knight knight && knight.getColor() == attackerColor) {
                return true;
            }
        }

        int pawnAttackDirection = (attackerColor == Color.WHITE) ? -1 : 1;
        for (int syOffset = -1; syOffset <= 1; syOffset += 2) {
            Cell c = getCellIfExists(cell, syOffset, pawnAttackDirection);
            if (c != null && board.getFigureAt(c) instanceof Pawn pawn && pawn.getColor() == attackerColor) {
                return true;
            }
        }
//for king
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                Cell c = getCellIfExists(cell, i, j);
                if (c != null && board.getFigureAt(c) instanceof King king && king.getColor() == attackerColor) {
                    return true;
                }
            }
        }

        // "диагональные"
        int[][] straightDirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int[][] diagonalDirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : straightDirs) {
            if (isSlidingAttacker(board, cell, dir, attackerColor, true)) return true;
        }
        for (int[] dir : diagonalDirs) {
            if (isSlidingAttacker(board, cell, dir, attackerColor, false)) return true;
        }

        return false;
    }

    //Ищет дальнобойную фигуру в заданном направлении от клетки
    private boolean isSlidingAttacker(Board board, Cell from, int[] dir, Color attackerColor, boolean checkStraights) {
        char sy = from.symbol();
        int num = from.num();

        while (true) {
            sy = (char) (sy + dir[0]);
            num = num + dir[1];

            if (sy < 'a' || sy > 'h' || num < 1 || num > 8) return false;
            Cell c = new Cell(sy, num);
            Figure figure = board.getFigureAt(c);

            if (figure != null) {
                if (figure.getColor() == attackerColor) {
                    if (checkStraights) {
                        return figure instanceof Rook || figure instanceof Queen;
                    } else {
                        return figure instanceof Bishop || figure instanceof Queen;
                    }
                }
                return false;
            }
        }
    }

    //коорд клетки со сдвигом
    private Cell getCellIfExists(Cell from, int syOffset, int numOffset) {
        char newSy = (char) (from.symbol() + syOffset);
        int newNum = from.num() + numOffset;
        if (newSy >= 'a' && newSy <= 'h' && newNum >= 1 && newNum <= 8) {
            return new Cell(newSy, newNum);
        }
        return null;
    }

    //король под шахом
    public boolean isInCheck(Position position, Color color) {
        Cell kingCell = findKingCell(position.getBoard(), color);
        if (kingCell == null) return false;

        Color attackerColor = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isCellAttacked(position, kingCell, attackerColor);
    }


    private Cell findKingCell(Board board, Color color) {
        for (int num = 1; num <= 8; num++) {
            for (char sy = 'a'; sy <= 'h'; sy++) {
                Cell c = new Cell(sy, num);
                Figure f = board.getFigureAt(c);
                if (f instanceof King k && k.getColor() == color) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Генерирует все легальные ходы для текущей позиции.
     * Фильтрует псевдолегальные ходы, убирая те, что оставляют короля под шахом.
     */
    public List<Move> generateLegalMoves(Position position) {
        List<Move> pseudoLegalMoves = moveGenerator.allLegalMoves(position);
        List<Move> legalMoves = new ArrayList<>();

        for (Move move : pseudoLegalMoves) {
            Position newPosition = applyMove(position, move);
            if (!isInCheck(newPosition, position.getActiveColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Применяет ход к позиции и возвращает новую позицию.
     */
    public Position applyMove(Position position, Move move) {
        Board newBoard = new Board(position.getBoard());

        Figure figure = newBoard.getFigureAt(move.from());
        newBoard.setFigureAt(move.from(), null);

        // Обработка взятия на проходе
        if (move.type() == Move.MoveType.EN_PASSANT) {
            Color color = figure.getColor();
            int direction = (color == Color.WHITE) ? -1 : 1;
            Cell capturedSquare = new Cell(move.to().symbol(), move.to().num() + direction);
            newBoard.setFigureAt(capturedSquare, null);
        }

        // Обработка рокировки
        if (move.type() == Move.MoveType.CASTLING_KING_SIDE) {
            Color color = figure.getColor();
            int num = (color == Color.WHITE) ? 1 : 8;
            Cell rookFrom = new Cell('h', num);
            Cell rookTo = new Cell('f', num);
            Figure rook = newBoard.getFigureAt(rookFrom);
            newBoard.setFigureAt(rookFrom, null);
            newBoard.setFigureAt(rookTo, rook);
        } else if (move.type() == Move.MoveType.CASTLING_QUEEN_SIDE) {
            Color color = figure.getColor();
            int num = (color == Color.WHITE) ? 1 : 8;
            Cell rookFrom = new Cell('a', num);
            Cell rookTo = new Cell('d', num);
            Figure rook = newBoard.getFigureAt(rookFrom);
            newBoard.setFigureAt(rookFrom, null);
            newBoard.setFigureAt(rookTo, rook);
        }

        // Обработка превращения пешки
        if (move.morphTo() != null) {
            Figure promotedFigure = switch (move.morphTo()) {
                case QUEEN -> new Queen(figure.getColor());
                case ROOK -> new Rook(figure.getColor());
                case BISHOP -> new Bishop(figure.getColor());
                case KNIGHT -> new Knight(figure.getColor());
                default -> figure;
            };
            newBoard.setFigureAt(move.to(), promotedFigure);
        } else {
            newBoard.setFigureAt(move.to(), figure);
        }

        // Обновляем права на рокировку
        boolean whiteKingSide = position.hasWhiteKingSideCastle();
        boolean whiteQueenSide = position.hasWhiteQueenSideCastle();
        boolean blackKingSide = position.hasBlackKingSideCastle();
        boolean blackQueenSide = position.hasBlackQueenSideCastle();

        if (figure instanceof King) {
            if (figure.getColor() == Color.WHITE) {
                whiteKingSide = false;
                whiteQueenSide = false;
            } else {
                blackKingSide = false;
                blackQueenSide = false;
            }
        } else if (figure instanceof Rook) {
            if (move.from().equals(new Cell('h', 1))) whiteKingSide = false;
            if (move.from().equals(new Cell('a', 1))) whiteQueenSide = false;
            if (move.from().equals(new Cell('h', 8))) blackKingSide = false;
            if (move.from().equals(new Cell('a', 8))) blackQueenSide = false;
        }

        // Обновляем en passant
        Cell newEnPassant = null;
        if (figure instanceof Pawn && Math.abs(move.to().num() - move.from().num()) == 2) {
            int enPassantRank = (move.from().num() + move.to().num()) / 2;
            newEnPassant = new Cell(move.from().symbol(), enPassantRank);
        }

        // Обновляем счетчики
        int newHalfMoveClock = (figure instanceof Pawn || move.inTarget() != null) ? 0 : position.getHalfMoveClock() + 1;
        int newFullMoveNumber = position.getActiveColor() == Color.BLACK ? position.getFullMoveClock() + 1 : position.getFullMoveClock();

        // Меняем цвет активного игрока
        Color newActiveColor = (position.getActiveColor() == Color.WHITE) ? Color.BLACK : Color.WHITE;

        return new Position(newBoard, newActiveColor,
                whiteKingSide, whiteQueenSide,
                blackKingSide, blackQueenSide,
                newEnPassant, newHalfMoveClock, newFullMoveNumber);
    }
}
