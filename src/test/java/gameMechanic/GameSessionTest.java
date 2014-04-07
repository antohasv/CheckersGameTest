package gameMechanic;

import gameClasses.Snapshot;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.VFS;

public class GameSessionTest {
    public static final int PLAYER_ID_1 = 100;
    public static final int PLAYER_ID_2 = 101;
    private GameSession gameSession;

    @BeforeMethod
    public void setUp() throws Exception {
        gameSession = new GameSession(PLAYER_ID_1, PLAYER_ID_2);
    }

    @Test
    public void testCheckStroke() throws Exception {
        //int id, int fromX = 6, int fromY = 5, int toX = 7, int toY = 4
        //100, 6, 5, 7, 4
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
        Snapshot snapshotPlayer1 = gameSession.getSnapshot(PLAYER_ID_1);
        System.out.print(snapshotPlayer1.toString());
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
