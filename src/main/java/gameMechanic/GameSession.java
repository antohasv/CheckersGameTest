package gameMechanic;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import resource.GameSettings;
import resource.ResourceFactory;

import gameClasses.Field;
import gameClasses.Snapshot;
import utils.TimeHelper;
import utils.VFS;

import static java.lang.Math.abs;

public class GameSession {
    public static final String FILE_GAME_SETTINGS = "settings/gameSettings.xml";
    public static final String BLACK = "black";
    public static final String WHITE = "white";
    public static final String SLASH = "\\";
    public static final String EXTEND_TXT = ".txt";
    public static final String PATH_TO_LOG_AI = "log\\AI\\1.txt";
    public static final String ID1 = "Id1";
    public static final String ID2 = "Id2";
    public static final String NAME_BLACK_QUANTITY = "blackQuantity";
    public static final String NAME_WHITE_QUANTITY = "whiteQuantity";

    private static String dirForLog;
    private static AtomicInteger creatorId = new AtomicInteger();

    private int whiteId;
    private int blackId;
    private int lastStroke;

    @Inject @Named(NAME_BLACK_QUANTITY)
    private int blackQuantity;
    @Inject @Named(NAME_WHITE_QUANTITY)
    private int whiteQuantity;

    private long lastStrokeTime = TimeHelper.getCurrentTime();

    @Inject
    public Field[][] field;
    private StringBuilder log = new StringBuilder();

    final private GameSettings settings;
    public static final String LOG = "log/";

    public static final String GAME_SESSION_DIR = "gameSessionDir";

    static {
        dirForLog = GAME_SESSION_DIR + TimeHelper.getCurrentTime();
        File dir = new File(LOG.concat(dirForLog));
        dir.mkdirs();
    }

    public GameSession() {
        this.settings = (GameSettings) ResourceFactory.instanse().getResource(FILE_GAME_SETTINGS);
    }

    public GameSession(int id1, int id2) {
        this();
        this.field = GenerateChessBoard.generate();
        intializeChessBoard(id1, id2);
    }

    public GameSession(Field[][] field, int id1, int id2) {
        this();
        this.field = field;
        intializeChessBoard(id1, id2);
    }

    private void intializeChessBoard(int id1, int id2) {
        blackQuantity = getNumCheckers();
        whiteQuantity = getNumCheckers();
        whiteId = id1;
        blackId = id2;
        lastStroke = id2;
    }

    private int getNumCheckers() {
        return settings.getFieldSize() * settings.getPlayerFieldSize() / 2;
    }

    private boolean isOdd(int number) {
        return number % 2 == 1;
    }

    private Field.Checker getAnotherColor(Field.Checker myColor) {
        if (myColor == Field.Checker.black) {
            return Field.Checker.white;
        } else if (myColor == Field.Checker.white) {
            return Field.Checker.black;
        }
        return Field.Checker.nothing;
    }


    public void printArray(Field[][] fields) {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields.length; j++) {
                System.out.println(String.format("(%d, %d -> %s)", i, j, fields[i][j].getType().toString()));
            }
        }
    }

    public boolean checkStroke(int id, int fromX, int fromY, int toX, int toY) {
        StringBuffer sb = new StringBuffer("gameSession.checkStroke(");
        sb.append(id).append(",").append(fromX).append(",").append(fromY).append(",").append(toX).append(",").append(toY).append(");\n");

        boolean changeId = true;

        if (id == whiteId) {
            toY = settings.getFieldSize() - 1 - toY;
            fromY = settings.getFieldSize() - 1 - fromY;
        } else {
            toX = settings.getFieldSize() - 1 - toX;
            fromX = settings.getFieldSize() - 1 - fromX;
        }

        if (!isStrokeRight(id, fromX, fromY, toX, toY)) {
            return false;
        }

        if (canEating(fromX, fromY, toX, toY)) {

            if (!checkEating(fromX, fromY, toX, toY)) {
                return false;
            }

            changeId = !makeEatingStroke(fromX, fromY, toX, toY);
        } else {
            if (!makeUsualStroke(fromX, fromY, toX, toY)) {
                return false;
            }
        }

        if (changeId) {
            lastStroke = id;
        }

        lastStrokeTime = TimeHelper.getCurrentTime();
        log.append(sb.toString());
        return true;
    }

    private boolean checkEating(int fromX, int fromY, int toX, int toY) {
        if (!fieldIsKing(fromX, fromY)) {
            return true;
        } else {
            return !checkKingOtherEating(fromX, fromY, toX, toY);
        }
    }

    private boolean checkKingOtherEating(int fromX, int fromY, int toX, int toY) {
        Field.Checker anotherColor = getAnotherColor(getFieldType(fromX, fromY));
        int onX = normal(toX - fromX);
        int onY = normal(toY - fromY);
        int x = fromX;
        int y = fromY;

        while (getFieldType(x, y) != anotherColor) {
            x += onX;
            y += onY;
        }

        Field eatingField = new Field(getField(x, y));
        int eatingFieldX = x, eatingFieldY = y;
        clearField(x, y);

        boolean ans = checkOtherEatingOpportunity(x, y, fromX, fromY, toX, toY);
        getField(eatingFieldX, eatingFieldY).make(eatingField);
        return ans;
    }

    public boolean checkOtherEatingOpportunityForField(int fromX, int fromY, int x, int y) {
        Field wasField = new Field();
        boolean ans = false;

        wasField.make(getField(x, y));
        getField(x, y).make(getField(fromX, fromY));

        if (canEat(x, y)) {
            ans = true;
        }
        getField(x, y).make(wasField);
        return ans;
    }

    private boolean checkOtherEatingOpportunity(int x, int y, int fromX, int fromY, int toX, int toY) {
        int onX = normal(toX - fromX);
        int onY = normal(toY - fromY);
        boolean ans = false;

        for (x += onX, y += onY; inBorder(x) && (inBorder(y)); x += onX, y += onY) {
            if ((x == toX) && (y == toY)) {
                continue;
            }
            ans |= checkOtherEatingOpportunityForField(fromX, fromY, x, y);
        }
        return ans;
    }

    private Field getField(int x, int y) {
        return field[y][x];
    }

    private Field.Checker getPlayerColor(int id) {
        if (id == whiteId) {
            return Field.Checker.white;
        } else {
            return Field.Checker.black;
        }
    }

    private boolean isStrokeRight(int id, int fromX, int fromY, int toX, int toY) {
        // Test last Stroke. User can't stroke twice.
        if (id == lastStroke) {
            return false;
        }

        // 
        if (!standartCheck(fromX, fromY, toX, toY)) {
            return false;
        }

        Field.Checker myColor = getPlayerColor(id);

        if (getFieldType(fromX, fromY) != myColor) {
            return false;
        }
        return true;
    }

    private boolean makeEatingStroke(int fromX, int fromY, int toX, int toY) {
        eat(fromX, fromY, toX, toY);
        if (becameKing(toX, toY)) {
            makeKing(toX, toY);
        }
        return canEat(toX, toY);
    }

    private boolean makeUsualStroke(int fromX, int fromY, int toX, int toY) {
        Field.Checker myColor = getFieldType(fromX, fromY);

        if (canEat(myColor)) {
            return false;
        }

        move(fromX, fromY, toX, toY);

        if (becameKing(toX, toY)) {
            makeKing(toX, toY);
        }

        return true;
    }

    private void makeKing(int x, int y) {
        field[y][x].makeKing();
    }

    private boolean becameKing(int x, int y) {
        Field.Checker myColor = getFieldType(x, y);
        return ((myColor == Field.Checker.black) && (y == 0)) || ((myColor == Field.Checker.white) && (y == settings.getFieldSize() - 1));
    }

    private boolean fieldIsKing(int x, int y) {
        return field[y][x].isKing();
    }

    private Field.Checker getFieldType(int x, int y) {
        return field[y][x].getType();
    }

    public boolean canEat(int x, int y) {
        if (fieldIsKing(x, y)) {
            return kingCanEat(x, y);
        } else {
            return pawnCanEat(x, y);
        }
    }

    private boolean pawnCanEatRightUp(int x, int y) {
        Field.Checker anotherColor = getAnotherColor(getFieldType(x, y));
        return (y < settings.getFieldSize() - 2) && (x < settings.getFieldSize() - 2) && (getFieldType(x + 1, y + 1) == anotherColor) && (fieldIsEmpty(x + 2, y + 2));
    }

    public boolean pawnCanEatRightDown(int x, int y) {
        Field.Checker anotherColor = getAnotherColor(getFieldType(x, y));
        return (y > 1) && (x < settings.getFieldSize() - 2) && (getFieldType(x + 1, y - 1) == anotherColor) && (fieldIsEmpty(x + 2, y - 2));
    }

    public boolean pawnCanEatLeftUp(int x, int y) {
        Field.Checker anotherColor = getAnotherColor(getFieldType(x, y));
        return (y < settings.getFieldSize() - 2) && (x > 1) && (getFieldType(x - 1, y + 1) == anotherColor) && (fieldIsEmpty(x - 2, y + 2));
    }

    private boolean pawnCanEatLeftDown(int x, int y) {
        Field.Checker anotherColor = getAnotherColor(getFieldType(x, y));
        return (y > 1) && (x > 1) && (getFieldType(x - 1, y - 1) == anotherColor) && (fieldIsEmpty(x - 2, y - 2));
    }

    public boolean pawnCanEat(int x, int y) {
        return pawnCanEatRightUp(x, y) || pawnCanEatLeftUp(x, y) || pawnCanEatRightDown(x, y) || pawnCanEatLeftDown(x, y);
    }

    private boolean kingCanEatRightUp(int x, int y) {
        Field.Checker myColor = getFieldType(x, y);
        Field.Checker anotherColor = getAnotherColor(myColor);

        int nextX;
        int nextY;
        for (int i = 1; i < settings.getFieldSize(); i++) {
            nextX = i + 1;
            nextY = i + 1;

            if (nextX >= settings.getFieldSize() - 2 || nextY >= settings.getFieldSize() - 2 || getFieldType(nextX, nextY) == myColor) {
                return false;
            }

            if (getFieldType(nextX, nextY) == anotherColor) {
                return fieldIsEmpty(nextX + 1, nextY + 1);
            }
        }
        return false;
    }

    private boolean kingCanEatLeftUp(int x, int y) {
        Field.Checker myColor = getFieldType(x, y), anotherColor = getAnotherColor(myColor);
        for (int counter = 1; counter < settings.getFieldSize(); counter++) {
            if ((x - counter <= 1) || (y + counter >= settings.getFieldSize() - 2)
                    || (getFieldType(x - counter, y + counter) == myColor)) {
                return false;
            }

            if (getFieldType(x - counter, y + counter) == anotherColor) {
                return fieldIsEmpty(x - counter - 1, y + counter + 1);
            }
        }
        return false;
    }

    private boolean kingCanEatRightDown(int x, int y) {
        Field.Checker myColor = getFieldType(x, y), anotherColor = getAnotherColor(myColor);
        for (int counter = 1; counter < settings.getFieldSize(); counter++) {
            if ((x + counter >= settings.getFieldSize() - 2) || (y + counter <= 1)
                    || (getFieldType(x + counter, y - counter) == myColor)) {
                return false;
            }

            if ((x + counter >= settings.getFieldSize()) || (y - counter <= 0)) {
                return false;
            }

            if (getFieldType(x + counter, y - counter) == anotherColor) {
                return fieldIsEmpty(x + counter + 1, y - counter - 1);
            }
        }
        return false;
    }

    private boolean kingCanEatLeftDown(int x, int y) {
        Field.Checker myColor = getFieldType(x, y), anotherColor = getAnotherColor(myColor);
        for (int counter = 1; counter < settings.getFieldSize(); counter++) {
            if ((x - counter <= 1) || (y - counter <= 1) || (getFieldType(x - counter, y - counter) == myColor)) {
                return false;
            }

            if (getFieldType(x - counter, y - counter) == anotherColor) {
                return fieldIsEmpty(x - counter - 1, y - counter - 1);
            }
        }
        return false;
    }

    public boolean kingCanEat(int x, int y) {
        System.out.print("");
        return kingCanEatRightUp(x, y) || kingCanEatRightDown(x, y) || kingCanEatLeftUp(x, y) || kingCanEatLeftDown(x, y);
    }

    private boolean canEat(Field.Checker myColor) {
        for (int x = 0; x < settings.getFieldSize(); x++)
            for (int y = 0; y < settings.getFieldSize(); y++) {
                if ((getFieldType(x, y) == myColor) && (canEat(x, y))) {
                    return true;
                }
            }
        return false;
    }

    private void move(int fromX, int fromY, int toX, int toY) {
        field[toY][toX].make(field[fromY][fromX]);
        clearField(fromX, fromY);
    }

    private void clearField(int x, int y) {
        field[y][x].clear();
    }

    private void eat(int fromX, int fromY, int toX, int toY) {
        int onX = normal(toX - fromX);
        int onY = normal(toY - fromY);
        int x = fromX;
        int y = fromY;

        for (int i = 1; i < abs(toX - fromX); i++) {
            x += onX;
            y += onY;

            if (getFieldType(x, y) == Field.Checker.black) {
                blackQuantity--;
            } else if (getFieldType(x, y) == Field.Checker.white) {
                whiteQuantity--;
            }

            clearField(x, y);
        }

        move(fromX, fromY, toX, toY);
    }


    private int normal(int number) {
        if (number == 0) {
            return 0;
        } else {
            return number / abs(number);
        }
    }

    private boolean inBorder(int number) {
        return (number >= 0) && (number <= settings.getFieldSize() - 1);
    }

    private boolean standartCheck(int fromX, int fromY, int toX, int toY) {
        if (isOdd(abs(toX - toY)) || isOdd(abs(fromX - fromY))) {
            return false;
        }

        // User can go only in border
        if (!inBorder(toX) || !inBorder(toY) || !inBorder(fromX) || !inBorder(fromY)) {
            return false;
        }

        if (getFieldType(toX, toY) != Field.Checker.nothing) {
            return false;
        }
        return true;
    }

    private boolean kingEating(int fromX, int fromY, int toX, int toY) {
        System.out.println("King Eating");
        Field.Checker myColor = getFieldType(fromX, fromY), anotherColor = getAnotherColor(myColor);
        int onX = normal(toX - fromX);
        int onY = normal(toY - fromY);
        int x = fromX;
        int y = fromY;

        for (int counter = 1; counter < abs(toX - fromX); counter++) {
            x += onX;
            y += onY;

            if (getFieldType(x, y) == myColor) {
                return false;
            }

            if (getFieldType(x, y) == anotherColor) {
                return (fieldIsEmpty(x + onX, y + onY));
            }
        }
        return false;
    }

    private boolean pawnEating(int fromX, int fromY, int toX, int toY) {
        if (abs(fromX - toX) != 2 || abs(fromY - toY) != 2) {
            return false;
        }

        Field.Checker myColor = getFieldType(fromX, fromY);
        Field.Checker anotherColor = getAnotherColor(myColor);
        int onX = normal(toX - fromX);
        int onY = normal(toY - fromY);

        return getFieldType(fromX + onX, fromY + onY) == anotherColor && fieldIsEmpty(toX, toY);
    }

    private boolean canEating(int fromX, int fromY, int toX, int toY) {
        if (abs(fromX - toX) < 2 || abs(fromY - toY) < 2) {
            return false;
        }

        if (fieldIsKing(fromX, fromY)) {
            return kingEating(fromX, fromY, toX, toY);
        } else {
            return pawnEating(fromX, fromY, toX, toY);
        }
    }

    private boolean canMoveRightUp(int x, int y) {
        if (y < settings.getFieldSize() - 1 && x < settings.getFieldSize() - 1 && fieldIsEmpty(x + 1, y + 1)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canMoveRightDown(int x, int y) {
        if (y > 0 && x < settings.getFieldSize() - 1 && fieldIsEmpty(x + 1, y - 1)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canMoveLeftUp(int x, int y) {
        if (y < settings.getFieldSize() - 1 && x > 0 && fieldIsEmpty(x - 1, y + 1)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canMoveLeftDown(int x, int y) {
        if (y > 0 && x > 0 && fieldIsEmpty(x - 1, y - 1)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canMove(int x, int y) {
        Field.Checker myColor = getFieldType(x, y);
        if (myColor == Field.Checker.white) {
            return canMoveRightUp(x, y) || canMoveLeftUp(x, y);
        } else {
            return canMoveRightDown(x, y) || canMoveLeftDown(x, y);
        }
    }

    private boolean canMove(Field.Checker myColor) {
        for (int x = 0; x < settings.getFieldSize(); x++)
            for (int y = 0; y < settings.getFieldSize(); y++) {
                if (getFieldType(x, y) != myColor || isOdd(x + y)) {
                    continue;
                }

                if (canMove(x, y) || canEat(x, y)) {
                    return true;
                }
            }
        return false;
    }

    private boolean fieldIsEmpty(int x, int y) {
        return field[y][x].isEmpty();
    }

    public int getAnotherId(int id) {
        return whiteId + blackId - id;
    }

    public int getWinnerId() {
        return getWinnerId(TimeHelper.getCurrentTime());
    }

    private int getWinnerId(long currentTime) {
        if (blackLose() || whiteWin(currentTime)) {
            return whiteId;
        } else if (whiteLose() || blackWin(currentTime)) {
            return blackId;
        } else {
            return 0;
        }
    }

    private boolean blackLose() {
        return (blackQuantity == 0 || !canMove(Field.Checker.black));
    }

    private boolean blackWin(long currentTime) {
        return (lastStroke == blackId) && (currentTime - lastStrokeTime > settings.getStrokeTime());
    }

    private boolean whiteLose() {
        return (whiteQuantity == 0 || !canMove(Field.Checker.white));
    }

    private boolean whiteWin(long currentTime) {
        return lastStroke == whiteId && currentTime - lastStrokeTime > settings.getStrokeTime();
    }

    public Snapshot getSnapshot(int id) {
        if (id == whiteId) {
            return returnSnapshot('w');
        } else {
            return returnSnapshot('b');
        }
    }

    private Snapshot returnSnapshot(char color) {
        return new Snapshot(field, color, settings.getFieldSize(), getNext());
    }

    public void saveAILog(String winner) {
        VFS.writeToFile(PATH_TO_LOG_AI, winner + "\n" + log.toString());
    }

    public void saveLog(int winnerId) {
        if (winnerId == blackId) {
            saveAILog(BLACK);
        } else {
            saveAILog(WHITE);
        }

        StringBuffer data = new StringBuffer(log.toString());
        data.append("\n").append(getSnapshot(whiteId).toStringTest());

        VFS.writeToFile(getFileName(), data.toString());
    }

    public String getFileName() {
        StringBuffer fileName = new StringBuffer(LOG);
        fileName.append(dirForLog).append(SLASH).append("1").append(EXTEND_TXT);
        return fileName.toString();
    }

    public char getNext() {
        if (lastStroke == whiteId) {
            return 'b';
        } else {
            return 'w';
        }
    }

    public void clearWhiteQuantity() { whiteQuantity = 0; }
}
//Черная клетка, если координаты один. четности