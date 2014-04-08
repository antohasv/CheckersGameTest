package resource;

import junit.framework.TestCase;
import org.testng.Assert;

public class RatingTest extends TestCase {

    public void testDiff() throws Exception {
        Rating.decreaseThreshold = 0;
        Assert.assertEquals(Rating.getDiff(50, 30), 0);
        Rating.decreaseThreshold = 1;
        Assert.assertEquals(Rating.getDiff(50, 30), 0);
    }
}
