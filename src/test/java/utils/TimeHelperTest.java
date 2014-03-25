package utils;


import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeHelperTest {

    private TimeHelper timeHelper;

    @BeforeMethod
    public void setUp() throws Exception {
        timeHelper = new TimeHelper();
    }

    @Test
    public void testGetCurrentTime() throws Exception  {
        long currentTime = TimeHelper.getCurrentTime();
        long alternativeCurrentDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Assert.assertEquals(currentTime, alternativeCurrentDate);
    }

    @Test
    public void testGetGMT() throws Exception  {
        String currentGMT = TimeHelper.getGMT();
        Assert.assertNotNull(currentGMT);
    }

    @Test
    public void testGetTime() throws Exception  {
        String currentTime = TimeHelper.getTime();
        Thread.sleep(1000);
        String newTime = TimeHelper.getTime();
        Assert.assertTrue(newTime.compareTo(currentTime) > 0);
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }
}
