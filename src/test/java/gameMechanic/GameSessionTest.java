package gameMechanic;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GameSessionTest {
    private GameSession gameSession;

    @BeforeMethod
    public void setUp() throws Exception {
        gameSession = new GameSession(100, 101);
    }

    @Test
    public void testIsDirCreated() throws Exception {
       //gameSession.saveAILog("Hello World");
        gameSession.saveLog(100);
    }
}
