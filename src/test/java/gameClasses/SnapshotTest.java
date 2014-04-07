package gameClasses;


import org.testng.Assert;
import org.junit.Before;
import org.junit.Test;

public class SnapshotTest {

    private Field[][] field;
    private char color;
    private char next;
    private int fieldSize;


    @Before
    public void setUp() throws Exception {
        this.color = 'b';
        this.fieldSize = 5;
        this.next = 'w';
        this.field = new Field[this.fieldSize][this.fieldSize];
        for(int i = 0; i < this.fieldSize; i++){
            for (int j = 0; j < this.fieldSize; j++){
                field[i][j] = new Field();
            }
        }
    }

    @Test
    public void test() throws Exception {
        Snapshot snapshot = new Snapshot(this.field, this.color, this.fieldSize, this.next);
        int indexStr = snapshot.toString().indexOf("\"color\":\"b\"");
        boolean strIsExist = false;
        if (indexStr > 0)
            strIsExist = true;
//        Assert.assertTrue(strIsExist);
        strIsExist = false;
        int indexStrTest = snapshot.toStringTest().indexOf("'next':'w'");
        if (indexStrTest > 0)
            strIsExist = true;
        Assert.assertTrue(strIsExist);
    }
}
