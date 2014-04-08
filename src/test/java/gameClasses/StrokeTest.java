package gameClasses;


import org.testng.Assert;
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
        Assert.assertFalse(stroke.isEmpty());
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

    @Test
    public void testInvert() throws Exception {
        Stroke strokeNew = new Stroke(toX, toY, fromX, fromY, status, black);
        strokeNew.setColor(white);
        strokeNew = strokeNew.getInverse();
        Assert.assertTrue(strokeNew.getColor().equals(black));

        Stroke strokeEmpty = new Stroke();
        Assert.assertTrue(strokeEmpty.isEmpty());

        Stroke strokeSt = new Stroke(status);
        Assert.assertTrue(strokeSt.getStatus().equals(status));

        strokeNew.clear();
        Assert.assertTrue(strokeNew.getStatus().equals(""));
    }

    @Test
    public void testOnlyStatus() throws Exception {
        Stroke strokeNew = new Stroke(toX, toY, fromX, fromY, status);
        Assert.assertTrue(strokeNew.getTo_X() == toX);
        Assert.assertTrue(strokeNew.getTo_Y() == toY);
        Assert.assertTrue(strokeNew.getFrom_X() == fromX);
        Assert.assertTrue(strokeNew.getFrom_Y() == fromY);
    }

    @Test
    public void testSetTo() throws Exception {
        Stroke strokeNew = new Stroke();
        strokeNew.setTo_X(toX);
        strokeNew.setTo_Y(toY);
        strokeNew.setFrom_X(fromX);
        strokeNew.setFrom_Y(fromY);
        strokeNew.setStatus(status);
        Assert.assertTrue(strokeNew.getTo_X() == toX);
        Assert.assertTrue(strokeNew.getTo_Y() == toY);
        Assert.assertTrue(strokeNew.getFrom_X() == fromX);
        Assert.assertTrue(strokeNew.getFrom_Y() == fromY);
        Assert.assertTrue(strokeNew.getStatus() == status);
    }

    @Test
    public void testFullSet() throws Exception {
        Stroke strokeNew = new Stroke();
        strokeNew.fullSet(toX, toY, fromX, fromY);
        Assert.assertTrue(strokeNew.getTo_X() == toX);
        Assert.assertTrue(strokeNew.getTo_Y() == toY);
        Assert.assertTrue(strokeNew.getFrom_X() == fromX);
        Assert.assertTrue(strokeNew.getFrom_Y() == fromY);
    }

    @Test
    public void testCreation() throws Exception {
        int x1 = 1;
        int y1 = 2;
        int x2 = 4;
        int y2 = 5;
        Stroke stroke1 = new Stroke();
        Assert.assertTrue(stroke1.getTo_X() == -1 && stroke1.getTo_Y() == -1 && stroke1.getFrom_X() == -1 && stroke1.getFrom_Y() == -1);

        Stroke stroke = new Stroke(x1, y1, x2, y2, "ST", "b");
        stroke.clear();
        Assert.assertTrue(stroke1.getTo_X() == -1 && stroke1.getTo_Y() == -1 && stroke1.getFrom_X() == -1 && stroke1.getFrom_Y() == -1);
        Assert.assertTrue(stroke.isEmpty());

        stroke.fullSet(x1, y1, x2, y2);
        Assert.assertFalse(stroke.isEmpty());
        Stroke strokeInv = stroke.getInverse();
        Assert.assertTrue(strokeInv.getTo_X() == 7 - x1 && strokeInv.getTo_Y() == 7 - y1 && strokeInv.getFrom_X() == 7 - x2 && strokeInv.getFrom_Y() == 7 - y2);

        stroke.setTo_X(x1);
        stroke.setTo_Y(y1);
        stroke.setFrom_X(x2);
        stroke.setFrom_Y(y2);
        Assert.assertTrue(stroke.getTo_X() == x1 && stroke.getTo_Y() == y1 && stroke.getFrom_X() == x2 && stroke.getFrom_Y() == y2);
    }
}
