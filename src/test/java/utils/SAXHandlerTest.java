package utils;


import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import resource.GameSettings;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class SAXHandlerTest {
    private GameSettings settings;
    public static final int TEST_FIELD_SIZE = 8;
    public static final int TEST_PLAYER_SIZE = 3;
    public static final int TEST_STROKE = 60;

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @Test
    public void testSAX()  throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        SAXHandler saxHandler = new SAXHandler();
        parser.parse(new File("settings/gameSettings.xml"), saxHandler);
        settings = (GameSettings) saxHandler.object;

        Assert.assertEquals(settings.getFieldSize(), TEST_FIELD_SIZE);
        Assert.assertEquals(settings.getPlayerSize(), TEST_PLAYER_SIZE);
        Assert.assertEquals(settings.getStrokeTime(), TEST_STROKE);
    }

}
