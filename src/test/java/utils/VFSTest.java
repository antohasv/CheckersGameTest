package utils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class VFSTest {
    public static final String FAKE_FILE = "fakeName.txt";
    public static final String FAKE_DATA = "fake_data";

    public static final String PATH_TO_FILE = VFS.PROJECT_DIRECTORY + FAKE_FILE;

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @Test
    public void testAbsolutePath() throws Exception {
        String path1 = VFS.getAbsolutePath(VFS.PROJECT_DIRECTORY + FAKE_FILE);
        String path2 = VFS.getAbsolutePath(FAKE_FILE);

        Assert.assertEquals(path1, VFS.PROJECT_DIRECTORY + FAKE_FILE);
        Assert.assertEquals(path2, VFS.PROJECT_DIRECTORY + FAKE_FILE);
    }

    @Test
    public void testRelativePath() throws Exception {
        String path1 = VFS.getRelativePath(VFS.PROJECT_DIRECTORY + FAKE_FILE);
        String path2 = VFS.getRelativePath(FAKE_FILE);

        Assert.assertEquals(path1, FAKE_FILE);
        Assert.assertEquals(path2, FAKE_FILE);
    }

    @Test
    public void testListOfPathByPath() throws Exception {
        List<File> fileList = VFS.getListOfFileByPath(VFS.PROJECT_DIRECTORY);
        Assert.assertTrue(fileList.size() > 100);
    }

    @Test
    public void testWriteAndReadToFile() throws Exception {
        VFS.writeToFile(PATH_TO_FILE, FAKE_DATA);
        Assert.assertEquals(VFS.readFile(PATH_TO_FILE), FAKE_DATA);
       deleteFile(PATH_TO_FILE);
    }

    @Test
    public void testIsCleanFile() throws Exception {
        VFS.writeToFile(PATH_TO_FILE, FAKE_DATA);
        VFS.cleanFile(PATH_TO_FILE);
        Assert.assertEquals(VFS.readFile(PATH_TO_FILE), "");
        deleteFile(PATH_TO_FILE);
    }

    @Test
    public void testWriteToEndOfFile() throws Exception {
        String fakeData = "";
        for (int i = 0; i < 1000; i++) {
            VFS.writeToEndOfFile(PATH_TO_FILE, FAKE_DATA);
            fakeData += FAKE_DATA;
        }
        Assert.assertEquals(VFS.readFile(PATH_TO_FILE), fakeData);
        deleteFile(PATH_TO_FILE);
    }

    public static boolean deleteFile(String path) {
        return new File(path).delete();
    }

    @AfterMethod
    public void tearDown() throws Exception {


    }
}
