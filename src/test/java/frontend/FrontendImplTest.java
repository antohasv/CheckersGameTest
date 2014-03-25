package frontend;

import base.MessageSystem;
import messageSystem.MessageSystemImpl;
import org.eclipse.jetty.server.Request;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrontendImplTest {
    private FrontendImpl frontend;

    @BeforeMethod
    public void setUp() throws Exception {
        MessageSystem messageSystem = new MessageSystemImpl();
        frontend = new FrontendImpl(messageSystem);
    }

    @Test
    public void testSite() throws Exception {
        String path = "/";

        Request request = mock(Request.class);

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(getUnCorrectCookies());
        when(servletRequest.getMethod()).thenReturn(FrontendImpl.HTTP_POST);

        StringWriter stringWriter = new StringWriter();

        PrintWriter writer = new PrintWriter(stringWriter);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        when(servletResponse.getWriter()).thenReturn(writer);

        frontend.handle(path, request, servletRequest, servletResponse);
        Mockito.verify(servletResponse).getWriter();

        Assert.assertNotEquals(stringWriter.toString(), "");
    }

    private Cookie[] getUnCorrectCookies() {
        Cookie cookies[] = new Cookie[2];
        cookies[0] = new Cookie(FrontendImpl.SESSION_ID, "0");
        cookies[1] = new Cookie(FrontendImpl.SERVER_TIME, "0");
        return cookies;
    }

    @AfterMethod
    public void tearDown() throws Exception {


    }
}
