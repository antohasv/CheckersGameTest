package utils;

import org.testng.Assert;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemplateHelperTest {
    Map<String, String> data;
    public static final String PAGE = "page";
    public static final String USER_ID = "id";
    public static final String USER_RATING = "rating";
    public static final String USER_NICKNAME = "nick";
    public static final String USER_TIME = "Time";
    public static final String USER_MEMORY = "MemoryUsage";
    public static final String USER_TOTAL_MEMORY = "TotalMemory";
    public static final String USER_CCU = "CCU";
    public static final String TEST_USER_ID = "1";
    public static final String TEST_USER_NICKNAME = "Nick";
    public static final String TEST_USER_RATING = "75";
    public static final String TEST_USER_TIME = "13:38:59";
    public static final String TEST_USER_MEMORY = "128";
    public static final String TEST_USER_TOTAL_MEMORY = "512";

    @BeforeMethod
    public void setUp() throws Exception {
        data = new HashMap<String, String>();
    }

    @Test
    public void testTemplate() throws Exception  {
        HttpServletResponse response = mock(HttpServletResponse.class);
        data.put(PAGE, "admin.html");
        data.put(USER_ID, TEST_USER_ID);
        data.put(USER_NICKNAME, TEST_USER_NICKNAME);
        data.put(USER_RATING, TEST_USER_RATING);
        data.put(USER_TIME, TEST_USER_TIME);
        data.put(USER_MEMORY, TEST_USER_MEMORY);
        data.put(USER_TOTAL_MEMORY, TEST_USER_TOTAL_MEMORY);
        data.put(USER_CCU, TEST_USER_TOTAL_MEMORY);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        TemplateHelper.renderTemplate("template.html", data, writer);
        Assert.assertNotNull(response.getWriter());
    }
}
