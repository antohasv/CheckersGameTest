package gameClasses;


import org.testng.Assert;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {
        Field field = new Field(Field.checker.black);
        Assert.assertFalse(field.isEmpty());

        field.clear();
        Assert.assertTrue(field.isEmpty());
    }
}
