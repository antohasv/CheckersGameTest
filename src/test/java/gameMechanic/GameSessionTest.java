package gameMechanic;

import gameClasses.Snapshot;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.VFS;

public class GameSessionTest {
    public static final int PLAYER_ID_1 = 451;
    public static final int PLAYER_ID_2 = 452;
    private GameSession gameSession;

    @BeforeMethod
    public void setUp() throws Exception {
        gameSession = new GameSession(PLAYER_ID_1, PLAYER_ID_2);
    }

    @Test
    public void testSimultaneusStroke() throws Exception {
        //int id, int fromX = 6, int fromY = 5, int toX = 7, int toY = 4
        //  6 5 7 4
        //  2 5 1 4
        //  7 4 5 2
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
        gameSession.checkStroke(PLAYER_ID_1, 2, 5, 1, 4);
        Snapshot snapshotPlayer1 = gameSession.getSnapshot(PLAYER_ID_1);
        System.out.print(snapshotPlayer1.toString());
    }

    @Test
    public void testEating() throws Exception {
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_1, 7, 4, 5, 2);
    }

    @Test
    public void testStrokes() throws Exception {
        gameSession.checkStroke(451, 6, 5, 7, 4);
        gameSession.checkStroke(452, 2, 5, 1, 4);
        gameSession.checkStroke(451, 7, 4, 5, 2);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(452, 1, 6, 3, 4);
        //Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 5, 6, 6, 5);
        gameSession.checkStroke(452, 3, 4, 2, 3);
        gameSession.checkStroke(451, 4, 5, 6, 3);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(452, 0, 5, 2, 3);
        //Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 6, 5, 4, 3);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(452, 4, 5, 2, 3);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 3, 6, 4, 5);
        gameSession.checkStroke(452, 2, 3, 4, 1);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 3, 6, 1, 4);
        gameSession.checkStroke(451, 3, 6, 1, 4);
        gameSession.checkStroke(451, 3, 6, 1, 4);
        gameSession.checkStroke(452, 4, 1, 6, 3);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 0, 5, 2, 3);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(452, 6, 5, 4, 3);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 2, 7, 3, 6);
        gameSession.checkStroke(452, 5, 6, 6, 5);
        gameSession.checkStroke(451, 6, 7, 5, 6);
        gameSession.checkStroke(452, 6, 5, 7, 4);
        gameSession.checkStroke(451, 7, 6, 6, 5);
        gameSession.checkStroke(452, 3, 6, 2, 5);
        gameSession.checkStroke(451, 3, 6, 2, 5);
        gameSession.checkStroke(452, 2, 5, 3, 4);
        gameSession.checkStroke(451, 5, 6, 4, 5);
        gameSession.checkStroke(452, 4, 3, 2, 1);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(452, 2, 1, 0, 3);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(451, 4, 7, 5, 6);
        gameSession.checkStroke(452, 0, 3, 1, 2);
        gameSession.checkStroke(451, 5, 6, 7, 4);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(452, 3, 4, 2, 3);
        gameSession.checkStroke(451, 2, 5, 1, 4);
        gameSession.checkStroke(452, 7, 4, 5, 2);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(451, 1, 6, 3, 4);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(452, 4, 7, 5, 6);
        gameSession.checkStroke(451, 3, 4, 2, 3);
        gameSession.checkStroke(452, 2, 3, 3, 2);
        gameSession.checkStroke(451, 7, 4, 6, 3);
        gameSession.checkStroke(452, 3, 2, 4, 1);
        gameSession.checkStroke(451, 2, 3, 1, 2);
        gameSession.checkStroke(452, 7, 6, 5, 4);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(451, 6, 3, 7, 2);
        gameSession.checkStroke(452, 4, 1, 5, 0);
       // Make King
        gameSession.checkStroke(451, 0, 7, 1, 6);
        gameSession.checkStroke(452, 5, 0, 7, 2);
       // King Eating
       // checkKingOtherEating
        gameSession.checkStroke(451, 7, 2, 6, 1);

    }

    @Test
    public void testGetWinnerId() throws Exception {
        Assert.assertEquals(gameSession.getWinnerId(), 0);
    }

    @Test
    public void testSaveAILog() throws Exception {
        gameSession.saveAILog(GameSession.WHITE);
        Assert.assertEquals(VFS.readFile(GameSession.PATH_TO_LOG_AI), GameSession.WHITE);
    }

    @Test
    public void testSaveLog() throws Exception {
        gameSession.saveLog(PLAYER_ID_1);
        Assert.assertNotEquals(VFS.readFile(gameSession.getFileName()), "");
        gameSession.saveLog(PLAYER_ID_2);
        Assert.assertNotEquals(VFS.readFile(gameSession.getFileName()), "");
    }
}
