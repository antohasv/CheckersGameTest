package utils;

import base.GameChat;
import org.testng.Assert;
import org.testng.annotations.Test;
import resource.GameSettings;

public class ReflectionHelperTest {

    public static final String RESOURCE_FAKE_CLASS = "resource.FAKE_CLASS";
    public static final String RESOURCE_GAME_SETTINGS = "resource.GameSettings";

    @Test
    public void testCreateInstanceUnCorrectClass() throws Exception {
        Assert.assertNull(ReflectionHelper.createInstance(RESOURCE_FAKE_CLASS));
    }

    @Test
    public void testCreateInstance() throws Exception {
        Assert.assertNotNull(ReflectionHelper.createInstance(RESOURCE_GAME_SETTINGS));
    }

    @Test
    public void testSetFieldValue() throws Exception {
        ReflectionHelper.createInstance(RESOURCE_GAME_SETTINGS);
    }

    @Test
    public void testSetUncorrectField() throws Exception {
        GameSettings gameSettings = (GameSettings) ReflectionHelper.createInstance(RESOURCE_GAME_SETTINGS);
        ReflectionHelper.setFieldValue(gameSettings, "FieldName", "Value");
    }
}
