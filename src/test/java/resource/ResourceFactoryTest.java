package resource;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ResourceFactoryTest {

    public static final String GAME_SETTINGS_RESOURCE_PATH = "settings/gameSettings.xml";
    ResourceFactory resourceFactory;

    @BeforeMethod
    public void setUp() throws Exception {
        resourceFactory = ResourceFactory.instanse();
    }

    @Test
    public void testGameSettingsResource() throws Exception {
        GameSettings gameSettings = (GameSettings) resourceFactory.getResource(GAME_SETTINGS_RESOURCE_PATH);

        Assert.assertNotNull(gameSettings);
        Assert.assertNotEquals(gameSettings.getFieldSize(), 0);
        Assert.assertNotEquals(gameSettings.getPlayerSize(), 0);
        Assert.assertNotEquals(gameSettings.getStrokeTime(), 0);
    }

    @Test
    public void testRatingResource() throws Exception {
        Assert.assertNotEquals(Rating.avgDiff, 0);
        Assert.assertNotEquals(Rating.decreaseThreshold, 0);
        Assert.assertNotEquals(Rating.maxDiff, 0);
        Assert.assertNotEquals(Rating.minDiff, 0);
    }

    @Test
    public void testTimeResource() throws Exception {
        Assert.assertNotEquals(TimeSettings.getExitTime(), 0);
    }

    @AfterMethod
    public void tearDown() throws Exception {


    }
}
