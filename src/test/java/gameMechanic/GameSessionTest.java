package gameMechanic;

import com.google.inject.Guice;
import com.google.inject.Injector;
import gameClasses.Field;
import gameClasses.Snapshot;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.VFS;

public class GameSessionTest {

    private final int fieldSize= 8;
    private final int playerFieldSize = 3;
    public static final int PLAYER_ID_1 = 1;
    public static final int PLAYER_ID_2 = 2;

    private GameSession gameSession;
    private GameModule gameModule;
    private Injector injector;
    private Field[][] field;

    @BeforeMethod
    public void setUp() throws Exception {
        field = GenerateChessBoard.generate();
        gameSession = new GameSession(field, PLAYER_ID_1, PLAYER_ID_2);
        gameModule = new GameModule();
        injector = Guice.createInjector(gameModule);
    }

    @Test
    public void testWhiteWinner() throws Exception {
        gameModule.setField(Field.Checker.black, false, 2, 4);
        gameModule.setField(Field.Checker.black, false, 5, 1);
        injector.injectMembers(this.gameSession);
        Assert.assertEquals(gameSession.getWinnerId(), PLAYER_ID_1);
    }

    @Test
    public void testCheckingStroke() throws Exception {
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 0, 5, 1, 4));
        Assert.assertEquals(gameSession.getNext(), 'b');
        Assert.assertEquals(gameSession.getWinnerId(), 0);
    }


    @Test
    public void testKing() {
        gameModule.setField(Field.Checker.white, true, 7, 7);
        gameModule.setField(Field.Checker.black, false, 5, 5);
        gameModule.setField(Field.Checker.black, false, 0, 0);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 7, 0, 4, 3));
    }

    @Test
    public void testKing1() {
        gameModule.setField(Field.Checker.white, false, 1, 1);
        gameModule.setField(Field.Checker.white, true, 3, 3);
        injector.injectMembers(gameSession);
        Assert.assertTrue(this.gameSession.checkStroke(PLAYER_ID_1, 3, 4, 0, 7));
    }

    @Test
    public void testKing2() {
        gameModule.setField(Field.Checker.black, false, 1, 1);
        gameModule.setField(Field.Checker.white, true, 3, 3);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 3, 4, 0, 7));
    }

    @Test
    public void testDiagonalStroke() throws Exception {
        Assert.assertFalse(gameSession.checkStroke(PLAYER_ID_1, 1, 1, 1, 2));
        Assert.assertFalse(gameSession.checkStroke(PLAYER_ID_1, 1, 2, 1, 1));
    }

    @Test
    public void testChecker() throws Exception {
        Assert.assertFalse(gameSession.checkStroke(PLAYER_ID_1, 1, 2, 1, 2));
        Assert.assertFalse(gameSession.checkStroke(PLAYER_ID_1, 1, 2, 0, 3));
    }


    public void testKingEatLeftDown() {
        gameModule.setField(Field.Checker.black, false, 2, 2);
        gameModule.setField(Field.Checker.white, true, 3, 3);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.kingCanEat(3, 3));
    }

    @Test
    public void testKingEatRightDown() {
        gameModule.setField(Field.Checker.black, false, 4, 2);
        gameModule.setField(Field.Checker.white, true, 3, 3);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.kingCanEat(3, 3));
    }

    @Test
    public void testKingEatLeftUp() {
        gameModule.setField(Field.Checker.black, false, 2, 4);
        gameModule.setField(Field.Checker.white, true, 3, 3);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.kingCanEat(3, 3));
    }

    @Test
    public void testKingEatRightUp() {
        gameModule.setField(Field.Checker.black, false, 4, 4);
        gameModule.setField(Field.Checker.white, true, 3, 3);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.kingCanEat(4, 4));
    }

    @Test
    public void testBecomeKing() {
        gameModule.setField(Field.Checker.white, false, 0, 0);
        gameModule.setField(Field.Checker.black, false, 2, 2);
        injector.injectMembers(this.gameSession);
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 0, 7, 1, 6) && gameSession.checkStroke(PLAYER_ID_2, 5, 2, 7, 0));
    }
    @Test
    public void testNotBecomeKing(){
        gameModule.setField(Field.Checker.black, false, 1, 1);
        gameModule.setField(Field.Checker.white, false, 2, 2);
        gameModule.setField(Field.Checker.white, false, 6, 6);
        gameModule.setField(Field.Checker.black, false, 5, 5);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 2, 5, 0, 7) && gameSession.checkStroke(PLAYER_ID_2, 2, 5, 0, 7));
    }

    @Test
    public void testLostAllCheckers() {
        gameModule.setField(Field.Checker.white, false, 0, 0);
        injector.injectMembers(gameSession);
        Assert.assertEquals(gameSession.getWinnerId(), PLAYER_ID_1);
    }

    @Test
    public void testLostAllCheckers2(){
        gameModule.setField(Field.Checker.white, false, 1, 1);
        gameModule.setField(Field.Checker.black, false, 3, 3);
        Injector injector = Guice.createInjector(gameModule);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 1, 6, 2, 5) && gameSession.checkStroke(PLAYER_ID_2, 4, 3, 6, 1));
        Assert.assertEquals(gameSession.getWinnerId(), 2);
    }

    @Test
    public void testCheckEating() throws Exception {
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
    }

    @Test
    public void testLooserHasNoStrokes() {
        gameModule.setField(Field.Checker.white, false, 0, 0);
        gameModule.setField(Field.Checker.black, false, 1, 1);
        gameModule.setField(Field.Checker.black, false, 2, 2);
        Injector injector = Guice.createInjector(gameModule);
        injector.injectMembers(this.gameSession);
        Assert.assertEquals(this.gameSession.getWinnerId(), 2);
    }

    @Test
    public void testLooserHasNoStrokes2(){
        gameModule.setField(Field.Checker.black, false, 7, 7);
        gameModule.setField(Field.Checker.white, false, 6, 6);
        gameModule.setField(Field.Checker.white, false, 4, 4);
        injector.injectMembers(gameSession);
        Assert.assertTrue(gameSession.checkStroke(PLAYER_ID_1, 4, 3, 5, 2));
        Assert.assertEquals(gameSession.getWinnerId(), 1);
    }


    @Test
    public void testSimultaneusStroke() throws Exception {
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
        gameSession.checkStroke(PLAYER_ID_1, 2, 5, 1, 4);
        Snapshot snapshotPlayer1 = gameSession.getSnapshot(PLAYER_ID_1);
        System.out.print(snapshotPlayer1.toString());
    }

    @Test
    public void testCanMove() {
        this.gameSession = new GameSession(1, 2);
        gameModule.setField(Field.Checker.white, false, 0, 0);
        gameModule.setField(Field.Checker.black, false, 4, 4);
        injector = Guice.createInjector(gameModule);
        injector.injectMembers(gameSession);
        Assert.assertEquals(gameSession.getWinnerId(), 0);
    }

    @Test
    public void testCanMove2() {
        gameModule.setField(Field.Checker.white, false, 7, 5);
        gameModule.setField(Field.Checker.black, false, 4, 4);
        Injector injector = Guice.createInjector(gameModule);
        injector.injectMembers(gameSession);
        Assert.assertEquals(gameSession.getWinnerId(), 0);
    }

    @Test
    public void testCanMove3() {
        gameModule.setField(Field.Checker.white, false, 5, 7);
        gameModule.setField(Field.Checker.black, false, 4, 4);
        Injector injector = Guice.createInjector(gameModule);
        injector.injectMembers(gameSession);
        Assert.assertEquals(gameSession.getWinnerId(), 2);
    }

    @Test
    public void testCanMove4(){
        gameModule.setField(Field.Checker.white, false, 7, 5);
        gameModule.setField(Field.Checker.black, false, 6, 6);
        Injector injector = Guice.createInjector(gameModule);
        injector.injectMembers(gameSession);
        Assert.assertEquals(gameSession.getWinnerId(), 0);
    }


    @Test
    public void testEating() throws Exception {
        Assert.assertTrue(gameSession.canEat(7, 4));
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
        Assert.assertTrue(gameSession.canEat(1, 4));
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 1, 4);
        Assert.assertTrue(gameSession.canEat(5,2));
        gameSession.checkStroke(PLAYER_ID_1, 7, 4, 5, 2);
    }

    @Test
    public void testKingEatingOpportunity(){
        gameModule.setField(Field.Checker.white, true, 0, 2);
        gameModule.setField(Field.Checker.black, false, 2, 4);
        gameModule.setField(Field.Checker.black, false, 4, 6);
        injector.injectMembers(gameSession);
        Assert.assertFalse(gameSession.checkStroke(PLAYER_ID_1, 0, 5, 3, 2));
        Assert.assertFalse(gameSession.checkStroke(PLAYER_ID_1, 3, 2, 5, 0));
    }

    @Test
    public void testStrokes() throws Exception {
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 7, 4);
        Assert.assertTrue(gameSession.checkOtherEatingOpportunityForField(6, 5, 7, 4));
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 1, 4);
        Assert.assertTrue(gameSession.pawnCanEatLeftUp(5, 2));
        gameSession.checkStroke(PLAYER_ID_1, 7, 4, 5, 2);

        //Pawn Eating
        //fieldIsKing
        Assert.assertTrue(gameSession.canEat(3, 4));
        gameSession.checkStroke(PLAYER_ID_2, 1, 6, 3, 4);
        //Pawn Eating
       // fieldIsKing
        Assert.assertTrue(gameSession.canEat(6, 5));
        gameSession.checkStroke(PLAYER_ID_1, 5, 6, 6, 5);
        Assert.assertTrue(gameSession.canEat(2, 3));
        gameSession.checkStroke(PLAYER_ID_2, 3, 4, 2, 3);
        Assert.assertTrue(gameSession.pawnCanEatLeftUp(6, 3));
        gameSession.checkStroke(PLAYER_ID_1, 4, 5, 6, 3);
        //Pawn Eating
        //fieldIsKing
        Assert.assertTrue(gameSession.canEat(2, 3));
        gameSession.checkStroke(PLAYER_ID_2, 0, 5, 2, 3);
        //Pawn Eating
       // fieldIsKing
        Assert.assertTrue(gameSession.pawnCanEatLeftUp(4, 3));
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 4, 3);
        //Pawn Eating
        //fieldIsKing
        Assert.assertTrue(gameSession.canEat(2, 3));
        gameSession.checkStroke(PLAYER_ID_2, 4, 5, 2, 3);
       // Pawn Eating
       // fieldIsKing
        Assert.assertTrue(gameSession.canEat(4, 5));
        gameSession.checkStroke(PLAYER_ID_1, 3, 6, 4, 5);
        Assert.assertTrue(gameSession.canEat(4, 1));
        gameSession.checkStroke(PLAYER_ID_2, 2, 3, 4, 1);
       // Pawn Eating
       // fieldIsKing
        Assert.assertTrue(gameSession.canEat(1, 4));
        gameSession.checkStroke(PLAYER_ID_1, 3, 6, 1, 4);
        gameSession.checkStroke(PLAYER_ID_2, 4, 1, 6, 3);
       // Pawn Eating
       // fieldIsKing
        Assert.assertTrue(gameSession.canEat(2, 3));
        gameSession.checkStroke(PLAYER_ID_1, 0, 5, 2, 3);
       // Pawn Eating
       // fieldIsKing
        Assert.assertTrue(gameSession.canEat(4, 3));
        gameSession.checkStroke(PLAYER_ID_2, 6, 5, 4, 3);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 2, 7, 3, 6);
        Assert.assertTrue(gameSession.canEat(6, 5));
        gameSession.checkStroke(PLAYER_ID_2, 5, 6, 6, 5);
        Assert.assertTrue(gameSession.canEat(5, 6));
        gameSession.checkStroke(PLAYER_ID_1, 6, 7, 5, 6);
        Assert.assertTrue(gameSession.canEat(7,4));
        gameSession.checkStroke(PLAYER_ID_2, 6, 5, 7, 4);
        Assert.assertTrue(gameSession.canEat(6,5));
        gameSession.checkStroke(PLAYER_ID_1, 7, 6, 6, 5);
        Assert.assertTrue(gameSession.canEat(2, 5));
        gameSession.checkStroke(PLAYER_ID_2, 3, 6, 2, 5);
        gameSession.checkStroke(PLAYER_ID_1, 3, 6, 2, 5);
        Assert.assertTrue(gameSession.canEat(3,4));
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 3, 4);
        Assert.assertTrue(gameSession.canEat(4,5));
        gameSession.checkStroke(PLAYER_ID_1, 5, 6, 4, 5);
        Assert.assertTrue(gameSession.canEat(2,1));
        gameSession.checkStroke(PLAYER_ID_2, 4, 3, 2, 1);
        //Pawn Eating
        //fieldIsKing
        Assert.assertTrue(gameSession.canEat(0,3));
        gameSession.checkStroke(PLAYER_ID_2, 2, 1, 0, 3);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 4, 7, 5, 6);
        gameSession.checkStroke(PLAYER_ID_2, 0, 3, 1, 2);
        gameSession.checkStroke(PLAYER_ID_1, 5, 6, 7, 4);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 3, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_1, 2, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_2, 7, 4, 5, 2);
        //Pawn Eating
        //fieldIsKing
        Assert.assertTrue(gameSession.checkOtherEatingOpportunityForField(1, 6, 3, 4));
        gameSession.checkStroke(PLAYER_ID_1, 1, 6, 3, 4);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 4, 7, 5, 6);
        gameSession.checkStroke(PLAYER_ID_1, 3, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_2, 2, 3, 3, 2);
        gameSession.checkStroke(PLAYER_ID_1, 7, 4, 6, 3);
        gameSession.checkStroke(PLAYER_ID_2, 3, 2, 4, 1);
        gameSession.checkStroke(PLAYER_ID_1, 2, 3, 1, 2);
        Assert.assertTrue(gameSession.checkOtherEatingOpportunityForField(2, 3, 1, 2));
        gameSession.checkStroke(PLAYER_ID_2, 7, 6, 5, 4);
       // Pawn Eating
       // fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 6, 3, 7, 2);
        gameSession.checkStroke(PLAYER_ID_2, 4, 1, 5, 0);
       // Make King
        gameSession.checkStroke(PLAYER_ID_1, 0, 7, 1, 6);
        //Assert.assertTrue(gameSession.kingCanEat(5, 0));
        gameSession.checkStroke(PLAYER_ID_2, 5, 0, 7, 2);
       // King Eating
       // checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_1, 7, 2, 6, 1);
        Assert.assertTrue(gameSession.checkOtherEatingOpportunityForField(7, 2, 6, 1));
    }

    @Test
    public void testKingEating() throws Exception {
        gameSession.checkStroke(PLAYER_ID_2, 4, 5, 5, 4);
        gameSession.checkStroke(PLAYER_ID_1, 2, 5, 3, 4);
        gameSession.checkStroke(PLAYER_ID_2, 6, 5, 7, 4);
        gameSession.checkStroke(PLAYER_ID_1, 3, 4, 1, 2);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 7, 6, 5, 4);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 4, 5, 3, 4);
        gameSession.checkStroke(PLAYER_ID_2, 5, 4, 3, 2);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 5, 6, 3, 4);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 5, 6, 4, 5);
        gameSession.checkStroke(PLAYER_ID_1, 3, 6, 4, 5);
        gameSession.checkStroke(PLAYER_ID_2, 7, 4, 6, 3);
        gameSession.checkStroke(PLAYER_ID_1, 0, 5, 2, 3);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 4, 5, 6, 3);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 1, 6, 0, 5);
        gameSession.checkStroke(PLAYER_ID_2, 6, 3, 5, 2);
        gameSession.checkStroke(PLAYER_ID_1, 3, 4, 1, 6);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 3, 6, 4, 5);
        gameSession.checkStroke(PLAYER_ID_1, 4, 5, 3, 4);
        gameSession.checkStroke(PLAYER_ID_2, 4, 5, 3, 4);
        gameSession.checkStroke(PLAYER_ID_1, 3, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_2, 3, 4, 4, 3);
        gameSession.checkStroke(PLAYER_ID_1, 2, 3, 4, 5);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 3, 4);
        gameSession.checkStroke(PLAYER_ID_1, 4, 5, 3, 4);
        gameSession.checkStroke(PLAYER_ID_2, 3, 4, 5, 2);
        // Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_1, 1, 6, 3, 4);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 4, 7, 3, 6);
        gameSession.checkStroke(PLAYER_ID_1, 3, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_2, 3, 6, 2, 5);
        gameSession.checkStroke(PLAYER_ID_2, 5, 4, 4, 3);
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 5, 4);
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_1, 5, 4, 4, 3);
        gameSession.checkStroke(PLAYER_ID_2, 5, 4, 4, 3);
        gameSession.checkStroke(PLAYER_ID_2, 6, 7, 5, 6);
        gameSession.checkStroke(PLAYER_ID_1, 0, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_2, 5, 6, 6, 5);
        gameSession.checkStroke(PLAYER_ID_1, 2, 3, 0, 1);
        // Pawn Eating
        // fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 1, 4, 0, 3);
        gameSession.checkStroke(PLAYER_ID_1, 0, 1, 1, 0);
        // Make King
        gameSession.checkStroke(PLAYER_ID_2, 0, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_1, 1, 0, 0, 1);
        gameSession.checkStroke(PLAYER_ID_2, 1, 6, 0, 5);
        gameSession.checkStroke(PLAYER_ID_1, 4, 3, 3, 2);
        gameSession.checkStroke(PLAYER_ID_2, 1, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_1, 0, 1, 1, 2);
        gameSession.checkStroke(PLAYER_ID_2, 0, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_1, 1, 2, 0, 3);
        gameSession.checkStroke(PLAYER_ID_2, 2, 3, 3, 2);
        gameSession.checkStroke(PLAYER_ID_1, 2, 7, 1, 6);
        gameSession.checkStroke(PLAYER_ID_2, 1, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_1, 4, 7, 3, 6);
        gameSession.checkStroke(PLAYER_ID_2, 3, 2, 5, 0);
        // Pawn Eating
        // fieldIsKing
        // Make King
        gameSession.checkStroke(PLAYER_ID_1, 1, 6, 0, 5);
        gameSession.checkStroke(PLAYER_ID_2, 2, 3, 3, 2);
        gameSession.checkStroke(PLAYER_ID_1, 3, 2, 2, 1);
        gameSession.checkStroke(PLAYER_ID_2, 2, 7, 1, 6);
        gameSession.checkStroke(PLAYER_ID_1, 2, 1, 3, 0);
        //Make King
        gameSession.checkStroke(PLAYER_ID_1, 4, 5, 5, 4);
        gameSession.checkStroke(PLAYER_ID_2, 3, 2, 2, 1);
        gameSession.checkStroke(PLAYER_ID_1, 6, 7, 4, 5);
        //Pawn Eating
        //fieldIsKing
        gameSession.checkStroke(PLAYER_ID_2, 5, 0, 0, 5);
        //King Eating
        //checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_2, 4, 7, 6, 5);
        gameSession.checkStroke(PLAYER_ID_1, 3, 0, 4, 1);
        gameSession.checkStroke(PLAYER_ID_2, 0, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_1, 1, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_2, 1, 4, 4, 7);
        //King Eating
        //checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_1, 2, 3, 3, 2);
        gameSession.checkStroke(PLAYER_ID_2, 4, 7, 5, 6);
        gameSession.checkStroke(PLAYER_ID_1, 3, 2, 1, 0);
        //Pawn Eating
        //fieldIsKing
        //Make King
        gameSession.checkStroke(PLAYER_ID_2, 1, 6, 2, 5);
        gameSession.checkStroke(PLAYER_ID_2, 6, 7, 7, 6);
        gameSession.checkStroke(PLAYER_ID_1, 1, 0, 0, 1);
        gameSession.checkStroke(PLAYER_ID_2, 2, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_1, 0, 3, 4, 7);
        // King Eating
        gameSession.checkStroke(PLAYER_ID_2, 1, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_1, 0, 1, 6, 7);
        // King Eating
        gameSession.checkStroke(PLAYER_ID_2, 0, 3, 1, 2);
        gameSession.checkStroke(PLAYER_ID_1, 4, 7, 7, 4);
        //King Eating
        //checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_2, 0, 7, 1, 6);
        gameSession.checkStroke(PLAYER_ID_1, 6, 7, 5, 6);
        gameSession.checkStroke(PLAYER_ID_2, 1, 6, 0, 5);
        gameSession.checkStroke(PLAYER_ID_1, 5, 6, 6, 5);
        gameSession.checkStroke(PLAYER_ID_2, 2, 3, 3, 2);
        gameSession.checkStroke(PLAYER_ID_1, 7, 4, 4, 1);
        //King Eating
        gameSession.checkStroke(PLAYER_ID_2, 3, 2, 4, 1);
        gameSession.checkStroke(PLAYER_ID_1, 0, 5, 1, 4);
        gameSession.checkStroke(PLAYER_ID_2, 4, 1, 5, 0);
        // Make King
        gameSession.checkStroke(PLAYER_ID_1, 4, 1, 2, 3);
        // King Eating
        gameSession.checkStroke(PLAYER_ID_2, 5, 4, 3, 2);
        gameSession.checkStroke(PLAYER_ID_2, 5, 0, 1, 4);
        //King Eating
        gameSession.checkStroke(PLAYER_ID_2, 5, 4, 3, 6);
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 5, 4);
        gameSession.checkStroke(PLAYER_ID_2, 1, 4, 5, 0);
        //King Eating
        //checkKingOtherEating
        // checkeatinggameSession.checkStroke(PLAYER_ID_2, 1, 4, 3, 2)
        // King Eating
        //checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_2, 3, 2, 6, 5);
        //King Eating
        // checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_1, 1, 2, 2, 3);
        gameSession.checkStroke(PLAYER_ID_1, 1, 2, 0, 1);
        gameSession.checkStroke(PLAYER_ID_1, 1, 4, 2, 3);
        gameSession.checkStroke(PLAYER_ID_2, 6, 5, 4, 3);
        //King Eating
        // checkKingOtherEating
        gameSession.checkStroke(PLAYER_ID_1, 0, 7, 1, 6);
        gameSession.checkStroke(PLAYER_ID_2, 4, 3, 7, 0);
        // King Eating
        // checkKingOtherEating
        // Make King
        gameSession.checkStroke(PLAYER_ID_1, 7, 6, 6, 5);
        gameSession.checkStroke(PLAYER_ID_2, 7, 0, 3, 4);
        // King Eating
        gameSession.checkStroke(PLAYER_ID_1, 6, 5, 5, 4);
        gameSession.checkStroke(PLAYER_ID_2, 3, 4, 1, 2);
        // King Eating
        //checkKingOtherEating
    }

    @Test
    public void testGetWinnerId() throws Exception {
        Assert.assertEquals(gameSession.getWinnerId(), 0);
    }

    @Test
    public void testSaveAILog() throws Exception {
        gameSession.saveAILog(GameSession.WHITE);
        Assert.assertEquals(VFS.readFile(GameSession.PATH_TO_LOG_AI), GameSession.WHITE);
    }

    @Test
    public void testSaveLog() throws Exception {
        gameSession.saveLog(PLAYER_ID_1);
        Assert.assertNotEquals(VFS.readFile(gameSession.getFileName()), "");
        gameSession.saveLog(PLAYER_ID_2);
        Assert.assertNotEquals(VFS.readFile(gameSession.getFileName()), "");
    }

    @Test
    public void testOutBorder() throws Exception {
        Assert.assertFalse(gameSession.checkStroke(1, -1, 2, 1, 2));
        Assert.assertFalse(gameSession.checkStroke(1, 9, 2, 1, 2));
        Assert.assertFalse(gameSession.checkStroke(1, 1, -2, 1, 2));
        Assert.assertFalse(gameSession.checkStroke(1, 1, 10, 1, 2));
        Assert.assertFalse(gameSession.checkStroke(1, 1, 2, -1, 2));
        Assert.assertFalse(gameSession.checkStroke(1, 1, 2, 9, 2));
        Assert.assertFalse(gameSession.checkStroke(1, 1, 2, 1, -2));
        Assert.assertFalse(gameSession.checkStroke(1, 1, 2, 1, 10));
    }


    @Test
    public void testGetSnapshot() throws Exception {
        Assert.assertNotNull(gameSession.getSnapshot(PLAYER_ID_1));
        Assert.assertNotNull(gameSession.getSnapshot(PLAYER_ID_2));
    }
}
