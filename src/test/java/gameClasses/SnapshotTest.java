package gameClasses;

import gameMechanic.GenerateChessBoard;
import junit.framework.TestCase;
import org.testng.Assert;

public class SnapshotTest extends TestCase {
    public void testToStringTest() throws Exception {
        Snapshot snapshot = new Snapshot(GenerateChessBoard.generate(), 'w', 7, '0');
        String text = snapshot.toStringTest();
        Assert.assertEquals(text, "{'status':'snapshot','next':'0','color':'w','field':[['white', 'nothing', 'white', 'nothing', 'white', 'nothing', 'white'], ['nothing', 'white', 'nothing', 'white', 'nothing', 'white', 'nothing'], ['white', 'nothing', 'white', 'nothing', 'white', 'nothing', 'white'], ['nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing'], ['nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing'], ['nothing', 'black', 'nothing', 'black', 'nothing', 'black', 'nothing'], ['black', 'nothing', 'black', 'nothing', 'black', 'nothing', 'black']],'king':[['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false']]}");

        snapshot = new Snapshot(GenerateChessBoard.generate(), 'b', 7, '0');
        text = snapshot.toStringTest();
        Assert.assertEquals(text, "{'status':'snapshot','next':'0','color':'b','field':[['white', 'nothing', 'white', 'nothing', 'white', 'nothing', 'white'], ['nothing', 'white', 'nothing', 'white', 'nothing', 'white', 'nothing'], ['white', 'nothing', 'white', 'nothing', 'white', 'nothing', 'white'], ['nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing'], ['nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing', 'nothing'], ['nothing', 'black', 'nothing', 'black', 'nothing', 'black', 'nothing'], ['black', 'nothing', 'black', 'nothing', 'black', 'nothing', 'black']],'king':[['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false'], ['false', 'false', 'false', 'false', 'false', 'false', 'false']]}");
        System.out.print(text);
    }
}
