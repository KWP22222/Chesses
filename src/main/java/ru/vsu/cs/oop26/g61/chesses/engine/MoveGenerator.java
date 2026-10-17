package ru.vsu.cs.oop26.g61.chesses.engine;

import ru.vsu.cs.oop26.g61.chesses.*;
import ru.vsu.cs.oop26.g61.chesses.figures.*;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    /**
     * Генерирует все псевдолегальные ходы для текущей позиции.
     * Псевдолегальные = все ходы по правилам фигур, без проверки шаха.
     */
    public List<Move> allLegalMoves(Position position){
        List<Move> moves = new ArrayList<>();
        Board board = position.getBoard();
        Color activeColor = position.getActiveColor();
        for (int num = 1; num <= 8; num++) {
            for (char symb = 'a'; symb <= 'h'; symb++) {
                Cell cell = new Cell(symb, num);
                Figure figure = board.getFigureAt(cell);
                if (figure != null && figure.getColor() == activeColor) {
                    generateFigureMoves(position, cell, figure, moves);
                }
            }
        }
        return moves;
    }

    private void generateFigureMoves(Position position, Cell from, Figure figure, List<Move> moves) {
        switch (figure) {
            case Pawn pawn -> generatePawnMoves(position, from, pawn, moves);
            case Knight knight -> generateKnightMoves(position, from, knight, moves);
            case Bishop bishop -> generateBishopMoves(position, from, bishop, moves);
            case Rook rook -> generateRookMoves(position, from, rook, moves);
            case Queen queen -> generateQueenMoves(position, from, queen, moves);
            case King king -> generateKingMoves(position, from, king, moves);
        }
    }

    private void generatePawnMoves(Position position, Cell from, Pawn pawn, List<Move> moves) {
        Board board = position.getBoard();
        Color color = pawn.getColor();
        int direction = (color == Color.WHITE) ? 1 : -1;

        Cell oneStep = new Cell(from.symbol(), from.num() + direction);
        if (board.isEmpty(oneStep)) {
            moves.add(new Move(from, oneStep, pawn));

            int startNum = (color == Color.WHITE) ? 2 : 7;
            if (from.num() == startNum) {
                Cell twoSteps = new Cell(from.symbol(), from.num() + 2 * direction);
                if (board.isEmpty(twoSteps)) {
                    moves.add(new Move(from, twoSteps, pawn));
                }
            }
        }


        for (int fileOffset = -1; fileOffset <= 1; fileOffset += 2) {
            char newSy = (char) (from.symbol() + fileOffset);
            if (newSy >= 'a' && newSy <= 'h') {
                Cell captureSquare = new Cell(newSy, from.num() + direction);
                Figure target = board.getFigureAt(captureSquare);
                if (target != null && target.getColor() != color) {
                    moves.add(new Move(from, captureSquare, pawn, target));
                }

            }
        }

        int promotionNum = (color == Color.WHITE) ? 8 : 1;
        List<Move> promotionMoves = new ArrayList<>();
        for (Move move : moves) {
            if (move.to().num() == promotionNum && move.figure() instanceof Pawn) {
                promotionMoves.add(new Move(move.from(), move.to(), move.figure(),
                        move.inTarget(), move.type(), FigureType.QUEEN));
                promotionMoves.add(new Move(move.from(), move.to(), move.figure(),
                        move.inTarget(), move.type(), FigureType.ROOK));
                promotionMoves.add(new Move(move.from(), move.to(), move.figure(),
                        move.inTarget(), move.type(), FigureType.BISHOP));
                promotionMoves.add(new Move(move.from(), move.to(), move.figure(),
                        move.inTarget(), move.type(), FigureType.KNIGHT));
            }
        }
        moves.removeAll(moves.stream()
                .filter(m -> m.to().num() == promotionNum && m.figure() instanceof Pawn)
                .toList());
        moves.addAll(promotionMoves);
    }


    private void generateKnightMoves(Position position, Cell from, Knight knight, List<Move> moves) {
        Board board = position.getBoard();
        Color color = knight.getColor();

        int[][] offsets = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] offset : offsets) {
            char newSy = (char) (from.symbol() + offset[0]);
            int newNum = from.num() + offset[1];

            if (newSy >= 'a' && newSy <= 'h' && newNum >= 1 && newNum <= 8) {
                Cell to = new Cell(newSy, newNum);
                Figure target = board.getFigureAt(to);
                if (target == null || target.getColor() != color) {
                    moves.add(new Move(from, to, knight, target));
                }
            }
        }
    }

    private void generateBishopMoves(Position position, Cell from, Bishop bishop, List<Move> moves) {
        generateSlidingMoves(position, from, bishop, moves,
                new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}});
    }

    private void generateRookMoves(Position position, Cell from, Rook rook, List<Move> moves) {
        generateSlidingMoves(position, from, rook, moves,
                new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}});
    }


    private void generateQueenMoves(Position position, Cell from, Queen queen, List<Move> moves) {
        generateSlidingMoves(position, from, queen, moves,
                new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1},
                        {-1, 0}, {1, 0}, {0, -1}, {0, 1}});
    }


    private void generateSlidingMoves(Position position, Cell from, Figure figure,
                                      List<Move> moves, int[][] directions) {
        Board board = position.getBoard();
        Color color = figure.getColor();

        for (int[] direction : directions) {
            char sy = from.symbol();
            int num = from.num();
            while (true) {
                sy = (char) (sy + direction[0]);
                num = num + direction[1];
                if (sy < 'a' || sy > 'h' || num < 1 || num > 8) {
                    break;
                }

                Cell to = new Cell(sy, num);
                Figure target = board.getFigureAt(to);

                if (target == null) {
                    moves.add(new Move(from, to, figure));
                } else {
                    if (target.getColor() != color) {
                        moves.add(new Move(from, to, figure, target));
                    }
                    break;
                }
            }
        }
    }


    private void generateKingMoves(Position position, Cell from, King king, List<Move> moves) {
        Board board = position.getBoard();
        Color color = king.getColor();

        for (int fileOffset = -1; fileOffset <= 1; fileOffset++) {
            for (int rankOffset = -1; rankOffset <= 1; rankOffset++) {
                if (fileOffset == 0 && rankOffset == 0) continue;

                char newSy = (char) (from.symbol() + fileOffset);
                int newNum = from.num() + rankOffset;

                if (newSy >= 'a' && newSy <= 'h' && newNum >= 1 && newNum <= 8) {
                    Cell to = new Cell(newSy, newNum);
                    Figure target = board.getFigureAt(to);

                    if (target == null || target.getColor() != color) {
                        moves.add(new Move(from, to, king, target));
                    }
                }
            }
        }

        generateCastlingMoves(position, from, king, moves); //рокировка
    }


    private void generateCastlingMoves(Position position, Cell from, King king, List<Move> moves) {
        Color color = king.getColor();
        int num = (color == Color.WHITE) ? 1 : 8;
        Board board = position.getBoard();

        // Короткая рокировка (король на e, ладья на h)
        if (hasCastlingRight(position, color, true)) {
            Cell f1 = new Cell('f', num);
            Cell g1 = new Cell('g', num);
            if (board.isEmpty(f1) && board.isEmpty(g1)) {
                moves.add(new Move(from, g1, king, null, Move.MoveType.CASTLING_KING_SIDE, null));
            }
        }

        // Длинная рокировка (король на e, ладья на a)
        if (hasCastlingRight(position, color, false)) {
            Cell d1 = new Cell('d', num);
            Cell c1 = new Cell('c', num);
            Cell b1 = new Cell('b', num);
            if (board.isEmpty(d1) && board.isEmpty(c1) && board.isEmpty(b1)) {
                moves.add(new Move(from, c1, king, null, Move.MoveType.CASTLING_QUEEN_SIDE, null));
            }
        }
    }

    private boolean hasCastlingRight(Position position, Color color, boolean kingSide) {
        if (color == Color.WHITE) {
            return kingSide ? position.hasWhiteKingSideCastle() : position.hasWhiteQueenSideCastle();
        } else {
            return kingSide ? position.hasBlackKingSideCastle() : position.hasBlackQueenSideCastle();
        }
    }

}
