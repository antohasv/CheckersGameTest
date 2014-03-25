package dbService;

import base.DataAccessObject;
import base.MessageSystem;
import messageSystem.MessageSystemImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DBServiceImplTest {
    DataAccessObject dataAccessObject;

    @BeforeMethod
    public void setUp() throws Exception {
        MessageSystem messageSystem = new MessageSystemImpl();
        dataAccessObject = new DBServiceImpl(messageSystem);
    }

    @Test
    public void testAddUser() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }
}
