package resource;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ResourceFactoryTest {

    public static final String GAME_SETTINGS_RESOURCE_PATH = "settings/gameSettings.xml";
    public static final String UNEXIST_RESOURCE = "UNEXIST_RESOURCE";
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
        Assert.assertNotEquals(gameSettings.getPlayerFieldSize(), 0);
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

    @Test
    public void testResExist() throws Exception {
        GameSettings gameSettings1 = (GameSettings) resourceFactory.getResource(GAME_SETTINGS_RESOURCE_PATH);
        Assert.assertEquals(gameSettings1, (GameSettings) resourceFactory.getResource(GAME_SETTINGS_RESOURCE_PATH));
    }

    @Test
    public void testUncorrectValue() throws Exception {
        Assert.assertNull(resourceFactory.getResource(UNEXIST_RESOURCE));
    }

    @AfterMethod
    public void tearDown() throws Exception {


    }
}
