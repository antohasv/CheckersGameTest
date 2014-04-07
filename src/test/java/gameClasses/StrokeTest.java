package gameClasses;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StrokeTest {

    public static final int toX = 0;
    public static final int toY = 3;
    public static final int fromX = 0;
    public static final int fromY = 5;
    public static final String status = "OK";
    private String black = "b";
    private String white = "w";
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {
        Stroke stroke = new Stroke(toX, toY, fromX, fromY, status, black);
        Assert.assertTrue(stroke.getTo_X() == toX);
        Assert.assertTrue(stroke.getTo_Y() == toY);
        Assert.assertTrue(stroke.getFrom_X() == fromX);
        Assert.assertTrue(stroke.getFrom_Y() == fromY);

        Assert.assertTrue(stroke.getColor().equals(black));
        stroke = stroke.getInverse();
        Assert.assertTrue(stroke.getColor().equals(white));

        Stroke strokeNew = new Stroke(stroke);
        strokeNew.setColor(white);
        strokeNew = strokeNew.getInverse();
        Assert.assertTrue(strokeNew.getColor().equals(black));

    }

}
