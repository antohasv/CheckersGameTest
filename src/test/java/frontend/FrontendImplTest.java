package frontend;

import base.Address;
import base.MessageSystem;
import base.Msg;
import dbService.DBServiceImpl;
import dbService.UserDataSet;
import org.eclipse.jetty.server.Request;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.TemplateHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class FrontendImplTest {
    public static final int INIT_NUMBER_OF_COOKIES = 2;
    public static final String FAKE_USERNAME = "fake_user";
    public static final String FAKE_PASSWORD = "fake_password";

    private MessageSystem messageSystem;
    private FrontendImpl frontend;
    private DBServiceImpl dbService;
    private UserDataImpl userData;


    @BeforeMethod
    public void setUp() throws Exception {
        messageSystem = mock(MessageSystem.class);

        dbService = new DBServiceImpl(messageSystem);
        dbService.createConnection();
        userData = new UserDataImpl(messageSystem);
        frontend = new FrontendImpl(messageSystem);

        when(messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME)).thenReturn(dbService.getAddress());
        when(messageSystem.getAddressByName(UserDataImpl.SERVICE_NAME)).thenReturn(userData.getAddress());
        when(messageSystem.getAddressByName(FrontendImpl.SERVICE_NAME)).thenReturn(frontend.getAddress());
    }

    @Test
    public void testSiteIndex() throws Exception {
        String path = "/";

        Request request = mock(Request.class);

        HttpServletRequest servletRequest = getHttpServletRequest(getEmptyCookie());

        StringWriter stringWriter = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        frontend.handle(path, request, servletRequest, response);
        Assert.assertEquals(stringWriter.toString(), LoadPage.getPage(Site.INDEX));
    }

    private HttpServletRequest getHttpServletRequest(Cookie[] cookies) {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(cookies);
        when(servletRequest.getMethod()).thenReturn(FrontendImpl.HTTP_POST);
        return servletRequest;
    }

    @Test
    public void testUnCorrectCookie() throws Exception {
        String path = "/";

        Request request = mock(Request.class);

        HttpServletRequest servletRequest = getHttpServletRequest(getUnCorrectCookies());

        StringWriter stringWriter = new StringWriter();

        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        when(servletResponse.getWriter()).thenReturn(new PrintWriter(stringWriter));

        frontend.handle(path, request, servletRequest, servletResponse);
        Mockito.verify(servletResponse).getWriter();

        Assert.assertNotEquals(stringWriter.toString(), "");
    }

    @Test
    public void testGetSession() throws Exception {
        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);
    }

    @Test
    public void testSiteWithSesson() throws Exception {
        String path = "/reg";
        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = getHttpServletRequest(cookies);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(path, request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.REG), htmlTemplate.toString());
    }

    @Test
    public void testRegistration() throws Exception {
        String path = "/reg";
        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = getHttpServletRequest(cookies);

        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(path, request, servletRequest, response);

        ArgumentCaptor<Address> captorAddress = ArgumentCaptor.forClass(Address.class);
        ArgumentCaptor<Msg> captorMsg = ArgumentCaptor.forClass(Msg.class);
        verify(messageSystem, times(1)).putMsg(captorAddress.capture(), captorMsg.capture());

        Address address = captorAddress.getValue();
        Msg message = captorMsg.getValue();
        Assert.assertNotNull(address);
        Assert.assertNotNull(message);

        message.exec(dbService);
        verify(messageSystem, times(2)).putMsg(captorAddress.capture(), captorMsg.capture());
        captorMsg.getValue().exec(userData);
    }

    @Test
    public void testAuthorizeNoRegUser() throws Exception {
        String path = "/reg";
        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = getHttpServletRequest(cookies);

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME + "1000");
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(path, request, servletRequest, response);

        ArgumentCaptor<Address> captorAddress = ArgumentCaptor.forClass(Address.class);
        ArgumentCaptor<Msg> captorMsg = ArgumentCaptor.forClass(Msg.class);
        verify(messageSystem, times(1)).putMsg(captorAddress.capture(), captorMsg.capture());

        captorMsg.getValue().exec(dbService);
        verify(messageSystem, times(2)).putMsg(captorAddress.capture(), captorMsg.capture());
    }

    @Test
    public void testAuthorizeRegUser() throws Exception {
        String path = "/reg";
        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(cookies);
        when(servletRequest.getMethod()).thenReturn(FrontendImpl.HTTP_POST);

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(path, request, servletRequest, response);

        ArgumentCaptor<Address> captorAddress = ArgumentCaptor.forClass(Address.class);
        ArgumentCaptor<Msg> captorMsg = ArgumentCaptor.forClass(Msg.class);
        verify(messageSystem, times(1)).putMsg(captorAddress.capture(), captorMsg.capture());

        captorMsg.getValue().exec(dbService);
        verify(messageSystem, times(2)).putMsg(captorAddress.capture(), captorMsg.capture());
    }

    private Cookie[] getCookie() throws IOException {
        String path = "/";

        Request request = mock(Request.class);

        HttpServletRequest servletRequest = getHttpServletRequest(getEmptyCookie());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        frontend.handle(path, request, servletRequest, response);

        ArgumentCaptor<Cookie> captorCookie = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(INIT_NUMBER_OF_COOKIES)).addCookie(captorCookie.capture());

        List<Cookie> cookieList = captorCookie.getAllValues();
        Cookie[] cookies = new Cookie[cookieList.size()];
        cookieList.toArray(cookies);
        return cookies;
    }

    private Cookie[] getEmptyCookie() {
        return new Cookie[0];
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
