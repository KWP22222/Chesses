package ru.vsu.cs.oop26.g61.chesses.engine;

import ru.vsu.cs.oop26.g61.chesses.Move;
import ru.vsu.cs.oop26.g61.chesses.Position;
import ru.vsu.cs.oop26.g61.chesses.exception.IllegalMoveException;
import ru.vsu.cs.oop26.g61.chesses.figures.*;
import java.util.List;


public class ChessEngine {

    private final RulesValidator rulesValidator = new RulesValidator();

    /**
     * Применяет ход к позиции.
     * @throws IllegalMoveException если ход недопустим
     */
    public Position makeMove(Position position, Move move) throws IllegalMoveException {
        List<Move> legalMoves = rulesValidator.generateLegalMoves(position);

        if (!legalMoves.contains(move)) {
            throw new IllegalMoveException("Ход не входит в список легальных ходов");
        }
        return rulesValidator.applyMove(position, move);
    }

    /**
     * Получает все легальные ходы для текущей позиции.
     */
    public List<Move> getLegalMoves(Position position) {
        return rulesValidator.generateLegalMoves(position);
    }

    /**
     * Проверяет, находится ли король под шахом.
     */
    public boolean isInCheck(Position position) {
        return rulesValidator.isInCheck(position, position.getActiveColor());
    }
}