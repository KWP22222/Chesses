import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop26.g61.chesses.*;
import ru.vsu.cs.oop26.g61.chesses.engine.ChessEngine;
import ru.vsu.cs.oop26.g61.chesses.exception.IllegalMoveException;
import ru.vsu.cs.oop26.g61.chesses.figures.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты шахматного движка.
 */
public class ChessEngineTest {

    private ChessEngine engine;
    private Position initialPosition;

    @BeforeEach
    void setUp() {
        engine = new ChessEngine();
        initialPosition = createInitialPosition();
    }

    @Test
    void testInitialPositionHas20LegalMoves() {
        // В начальной позиции у белых 20 легальных ходов (16 ходов пешками + 4 хода конями)
        List<Move> moves = engine.getLegalMoves(initialPosition);
        assertEquals(20, moves.size(), "В начальной позиции должно быть 20 легальных ходов");
    }

    @Test
    void testKnightMoves() {
        // Конь на b1 может пойти на a3 или c3
        List<Move> moves = engine.getLegalMoves(initialPosition);
        long knightMoves = moves.stream()
                .filter(m -> m.from().equals(new Cell('b', 1)))
                .count();
        assertEquals(2, knightMoves, "Конь на b1 должен иметь 2 хода");
    }

    @Test
    void testBishopMoves() {
        // Создаем позицию, где слон может ходить
        Board board = new Board();
        board.setFigureAt(new Cell('c', 1), new Bishop(Color.WHITE));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1 );

        List<Move> moves = engine.getLegalMoves(position);
        long bishopMoves = moves.stream()
                .filter(m -> m.from().equals(new Cell('c', 1)))
                .count();
        assertTrue(bishopMoves > 0, "Слон должен иметь хотя бы 1 ход");
    }

    @Test
    void testCastling() {
        // Создаем позицию для короткой рокировки
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('h', 1), new Rook(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, false, false, false,
                null, 0, 1 );

        List<Move> moves = engine.getLegalMoves(position);
        boolean hasCastling = moves.stream()
                .anyMatch(m -> m.type() == Move.MoveType.CASTLING_KING_SIDE);
        assertTrue(hasCastling, "Должна быть доступна короткая рокировка");
    }

    @Test
    void testEnPassant() {
        // Создаем позицию для взятия на проходе
        Board board = new Board();
        board.setFigureAt(new Cell('e', 5), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('d', 7), new Pawn(Color.BLACK));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Cell enPassantCell = new Cell('d', 6);
        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                enPassantCell, 0, 1 );

        List<Move> moves = engine.getLegalMoves(position);
        boolean hasEnPassant = moves.stream()
                .anyMatch(m -> m.type() == Move.MoveType.EN_PASSANT);
        assertTrue(hasEnPassant, "Должно быть доступно взятие на проходе");
    }

    @Test
    void testPawnPromotion() {
        // Создаем позицию, где пешка может превратиться
        Board board = new Board();
        board.setFigureAt(new Cell('e', 7), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        List<Move> moves = engine.getLegalMoves(position);
        long promotionMoves = moves.stream()
                .filter(m -> m.morphTo() != null)
                .count();
        assertEquals(4, promotionMoves, "Пешка должна иметь 4 варианта превращения");
    }

    @Test
    void testCheck() {
        // Создаем позицию, где белый король под шахом
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));
        board.setFigureAt(new Cell('e', 5), new Rook(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        assertTrue(engine.isInCheck(position), "Король должен быть под шахом");
    }

    @Test
    void testIllegalMoveThrowsException() {
        // Пытаемся сделать ход, который оставляет короля под шахом
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));
        board.setFigureAt(new Cell('e', 5), new Rook(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1 );

        // Ход королем на f1 (все еще под шахом от ладьи)
        Move illegalMove = new Move(new Cell('e', 1), new Cell('f', 1),
                new King(Color.WHITE));

        assertThrows(IllegalMoveException.class, () -> {
            engine.makeMove(position, illegalMove);
        }, "Должно быть выброшено исключение для недопустимого хода");
    }

    @Test
    void testCellEqualsAndHashCode() {
        Cell e4_1 = new Cell('e', 4);
        Cell e4_2 = new Cell('e', 4);
        Cell d4 = new Cell('d', 4);

        assertEquals(e4_1, e4_2, "Одинаковые клетки должны быть равны");
        assertEquals(e4_1.hashCode(), e4_2.hashCode(), "Хеш-коды одинаковых клеток должны совпадать");
        assertNotEquals(e4_1, d4, "Разные клетки не должны быть равны");
    }

    @Test
    void testPositionEqualsAndHashCode() {
        Board board1 = new Board();
        board1.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board1.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Board board2 = new Board();
        board2.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board2.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position pos1 = new Position(board1, Color.WHITE,
                true, true, true, true,
                null, 0, 1 );
        Position pos2 = new Position(board2, Color.WHITE,
                true, true, true, true,
                null, 0, 1 );

        assertEquals(pos1, pos2, "Одинаковые позиции должны быть равны");
        assertEquals(pos1.hashCode(), pos2.hashCode(), "Хеш-коды одинаковых позиций должны совпадать");
    }

    @Test
    void testEncapsulation() {
        // Проверяем, что геттеры возвращают копии, а не оригиналы
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1 );

        Board returnedBoard = position.getBoard();
        returnedBoard.setFigureAt(new Cell('e', 1), null); // Пытаемся изменить

        // Оригинальная позиция не должна измениться
        assertNotNull(position.getBoard().getFigureAt(new Cell('e', 1)),
                "Изменение возвращенной доски не должно влиять на оригинал");
    }

    @Test
    void testMakeMoveCreatesNewPosition() throws IllegalMoveException {
        // Проверяем неизменяемость позиции
        List<Move> moves = engine.getLegalMoves(initialPosition);
        Move firstMove = moves.get(0);

        Position newPosition = engine.makeMove(initialPosition, firstMove);

        assertNotSame(initialPosition, newPosition, "makeMove должен создавать новую позицию");
        assertNotEquals(initialPosition.getActiveColor(), newPosition.getActiveColor(),
                "После хода должен измениться активный цвет");
    }

    /**
     * Создает начальную шахматную позицию.
     */
    private Position createInitialPosition() {
        Board board = new Board();

        // Черные фигуры
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
                null, 0, 1 );
    }
}