import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop26.g61.chesses.*;
import ru.vsu.cs.oop26.g61.chesses.engine.ChessEngine;
import ru.vsu.cs.oop26.g61.chesses.exception.IllegalMoveException;
import ru.vsu.cs.oop26.g61.chesses.figures.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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
        System.out.println("\n=== Тест: 20 легальных ходов в начальной позиции ===");
        printBoard(initialPosition);

        List<Move> moves = engine.getLegalMoves(initialPosition);
        System.out.println("Легальных ходов: " + moves.size());

        assertEquals(20, moves.size(), "В начальной позиции должно быть 20 легальных ходов");
    }

    @Test
    void testKnightMoves() {
        System.out.println("\n=== Тест: Ходы коня ===");
        printBoard(initialPosition);

        List<Move> moves = engine.getLegalMoves(initialPosition);
        long knightMoves = moves.stream()
                .filter(m -> m.from().equals(new Cell('b', 1)))
                .count();

        System.out.println("Ходов коня на b1: " + knightMoves);
        assertEquals(2, knightMoves, "Конь на b1 должен иметь 2 хода (a3 и c3)");
    }

    @Test
    void testCastling() {
        System.out.println("\n=== Тест: Рокировка ===");
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('h', 1), new Rook(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, false, false, false,
                null, 0, 1);

        printBoard(position);

        List<Move> moves = engine.getLegalMoves(position);
        boolean hasCastling = moves.stream()
                .anyMatch(m -> m.type() == Move.MoveType.CASTLING_KING_SIDE);

        System.out.println("Доступна короткая рокировка: " + hasCastling);
        assertTrue(hasCastling, "Должна быть доступна короткая рокировка");
    }

    @Test
    void testEnPassant() {
        System.out.println("\n=== Тест: Взятие на проходе ===");
        Board board = new Board();
        board.setFigureAt(new Cell('e', 5), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('d', 7), new Pawn(Color.BLACK));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Cell enPassantCell = new Cell('d', 6);
        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                enPassantCell, 0, 1);

        printBoard(position);
        System.out.println("Поле для взятия на проходе: " + enPassantCell);

        List<Move> moves = engine.getLegalMoves(position);
        boolean hasEnPassant = moves.stream()
                .anyMatch(m -> m.type() == Move.MoveType.EN_PASSANT);

        System.out.println("Доступно взятие на проходе: " + hasEnPassant);
        assertTrue(hasEnPassant, "Должно быть доступно взятие на проходе");
    }

    @Test
    void testPawnPromotion() {
        System.out.println("\n Тест: Превращение пешки");
        Board board = new Board();
        board.setFigureAt(new Cell('e', 7), new Pawn(Color.WHITE));
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        printBoard(position);

        List<Move> moves = engine.getLegalMoves(position);
        long promotionMoves = moves.stream()
                .filter(m -> m.morphTo() != null)
                .count();

        System.out.println("Ходов с превращением пешки: " + promotionMoves);
        assertEquals(4, promotionMoves, "Пешка должна иметь 4 варианта превращения (Ф, Л, С, К)");
    }

    @Test
    void testCheck() {
        System.out.println("\n=== Тест: Шах ===");
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));
        board.setFigureAt(new Cell('e', 5), new Rook(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        printBoard(position);

        boolean inCheck = engine.isInCheck(position);
        System.out.println("Король под шахом: " + inCheck);

        assertTrue(inCheck, "Король должен быть под шахом от черной ладьи на e5");
    }

    @Test
    void testIllegalMoveThrowsException() {
        System.out.println("\nТест: Недопустимый ход (исключение)");
        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board.setFigureAt(new Cell('e', 8), new King(Color.BLACK));
        board.setFigureAt(new Cell('e', 5), new Rook(Color.BLACK));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        printBoard(position);
        System.out.println("Попытка хода: Ke1-f1 (король останется под шахом от ладьи)");

        Move illegalMove = new Move(new Cell('e', 1), new Cell('f', 1),
                new King(Color.WHITE));

        assertThrows(IllegalMoveException.class, () -> {
            engine.makeMove(position, illegalMove);
        }, "Должно быть выброшено исключение для недопустимого хода");
    }

    @Test
    void testCellEqualsAndHashCode() {
        System.out.println("\n=== Тест: Контракт equals/hashCode для Cell ===");

        Cell e4_1 = new Cell('e', 4);
        Cell e4_2 = new Cell('e', 4);
        Cell d4 = new Cell('d', 4);

        System.out.println("Cell 1: " + e4_1);
        System.out.println("Cell 2: " + e4_2);
        System.out.println("Cell 3: " + d4);

        assertEquals(e4_1, e4_2, "Одинаковые клетки должны быть равны");
        assertEquals(e4_1.hashCode(), e4_2.hashCode(), "Хеш-коды одинаковых клеток должны совпадать");
        assertNotEquals(e4_1, d4, "Разные клетки не должны быть равны");
    }

    @Test
    void testPositionEqualsAndHashCode() {
        System.out.println("\nТест: Контракт equals/hashCode для Position");

        Board board1 = new Board();
        board1.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board1.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Board board2 = new Board();
        board2.setFigureAt(new Cell('e', 1), new King(Color.WHITE));
        board2.setFigureAt(new Cell('e', 8), new King(Color.BLACK));

        Position pos1 = new Position(board1, Color.WHITE,
                true, true, true, true,
                null, 0, 1);
        Position pos2 = new Position(board2, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        System.out.println("Position 1: " + pos1);
        System.out.println("Position 2: " + pos2);

        assertEquals(pos1, pos2, "Одинаковые позиции должны быть равны");
        assertEquals(pos1.hashCode(), pos2.hashCode(), "Хеш-коды одинаковых позиций должны совпадать");
    }

    @Test
    void testEncapsulation() {
        System.out.println("\n=== Тест: Инкапсуляция (защита от изменения) ===");

        Board board = new Board();
        board.setFigureAt(new Cell('e', 1), new King(Color.WHITE));

        Position position = new Position(board, Color.WHITE,
                true, true, true, true,
                null, 0, 1);

        System.out.println("Исходная позиция: король на e1");

        Board returnedBoard = position.getBoard();
        returnedBoard.setFigureAt(new Cell('e', 1), null);

        assertNotNull(position.getBoard().getFigureAt(new Cell('e', 1)),
                "Изменение возвращенной доски не должно влиять на оригинал");

        System.out.println("Инкапсуляция работает: оригинальная позиция не изменена");
    }

    @Test
    void testMakeMoveCreatesNewPosition() throws IllegalMoveException {
        System.out.println("\nТест: Неизменяемость позиции");

        printBoard(initialPosition);

        List<Move> moves = engine.getLegalMoves(initialPosition);
        Move firstMove = moves.get(0);

        System.out.println("Применяем ход: " + firstMove.from() + " -> " + firstMove.to());

        Position newPosition = engine.makeMove(initialPosition, firstMove);

        assertNotSame(initialPosition, newPosition, "makeMove должен создавать новую позицию");
        assertNotEquals(initialPosition.getActiveColor(), newPosition.getActiveColor(),
                "После хода должен измениться активный цвет");

        System.out.println("Создана новая позиция, ходят: " + newPosition.getActiveColor());
    }

    /**
     * Рисует шахматную доску в консоли.
     */
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

    private Position createInitialPosition() {
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
}