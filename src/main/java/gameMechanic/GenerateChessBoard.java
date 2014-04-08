package gameMechanic;

import gameClasses.Field;
import resource.GameSettings;
import resource.ResourceFactory;

public class GenerateChessBoard {

    private static GameSettings settings = (GameSettings) ResourceFactory.instanse().getResource(GameSession.FILE_GAME_SETTINGS);
    private static Field[][] chessBoard;

    public static Field[][] generate() {
        chessBoard = new Field[settings.getFieldSize()][settings.getFieldSize()];
        fillChessBoard();
        return chessBoard;
    }

    private static void fillChessBoard() {
        for (int y = 0; y < settings.getFieldSize(); y++) {
            if (y < settings.getPlayerFieldSize()) {
                generateLine(y, Field.Checker.white, isOdd(y));
            } else if (y >= (settings.getFieldSize() - settings.getPlayerFieldSize())) {
                generateLine(y, Field.Checker.black, isOdd(y));
            } else {
                generateEmptyLine(y);
            }
        }
    }

    private static void generateEmptyLine(int y) {
        for (int x = 0; x < settings.getFieldSize(); x++) {
            generateField(x, y, Field.Checker.nothing);
        }
    }

    private static void generateLine(int y, Field.Checker color, boolean needOdd) {
        for (int x = 0; x < settings.getFieldSize(); x++) {
            if (isOdd(x) == needOdd) {
                generateField(x, y, color);
            } else {
                generateField(x, y, Field.Checker.nothing);
            }
        }
    }

    private static void generateField(int x, int y, Field.Checker field) {
        chessBoard[y][x] = new Field(field);
    }

    private static boolean isOdd(int number) {
        return number % 2 == 1;
    }
}
