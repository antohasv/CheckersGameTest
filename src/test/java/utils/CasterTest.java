package utils;

import dbService.UserDataSet;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CasterTest {

    private Caster caster;
    public static Map<String, Long> castKeys;
    public static final String ONE = "one";
    public static final String TWO = "two";
    public static final String THREE = "three";
    public static final String FOUR = "four";

    @BeforeMethod
    public void setUp() throws Exception {
        caster = new Caster();
        castKeys = new ConcurrentHashMap<String, Long>();
        castKeys.put(ONE, 1L);
        castKeys.put(TWO, 2L);
        castKeys.put(THREE, 3L);
    }

    @Test
    public void testCast()  throws Exception {
        String[] keys = caster.castKeysToStrings(castKeys);
        ArrayList<String> keysList = new ArrayList<String>(Arrays.asList(keys));

        Assert.assertTrue(keysList.contains(ONE));
        Assert.assertTrue(keysList.contains(TWO));
        Assert.assertTrue(keysList.contains(THREE));
        Assert.assertFalse(keysList.contains(FOUR));
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }
}
