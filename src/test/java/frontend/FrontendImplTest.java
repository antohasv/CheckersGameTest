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
import system.Metric;
import system.SystemInfo;
import utils.StoreCookie;

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

    private Site[] sitesWithSession = {Site.REG, Site.RULES};

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

        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = getHttpServletRequest(cookies);

        StringWriter htmlTemplate;

        HttpServletResponse response = mock(HttpServletResponse.class);

        for (Site site : sitesWithSession) {
            htmlTemplate = new StringWriter();
            when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

            frontend.handle(site.getUrl(), request, servletRequest, response);
            Assert.assertEquals(LoadPage.getPage(site), htmlTemplate.toString());
        }
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

    @Test
    public void testUserLogout() throws Exception {
        String path = "/logout";
        Cookie[] cookies = getCookie();
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);

        Request request = mock(Request.class);

        HttpServletRequest servletRequest = getHttpServletRequest(cookies);
        when(servletRequest.getMethod()).thenReturn(FrontendImpl.HTTP_GET);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);

        StoreCookie storeCookie = new StoreCookie(cookies);
        UserDataImpl.putSessionIdAndUserSession(storeCookie.getCookieByName("sessionId"), getFakeNickName());

        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));
        frontend.handle(path, request, servletRequest, response);

        ArgumentCaptor<Cookie> captorCookie = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(captorCookie.capture());
    }

    @Test
    public void testUserWait() throws Exception {
        String path = "/wait";
        Request request = mock(Request.class);

        HttpServletRequest servletRequest = getHttpServletRequest(getEmptyCookie());
        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        frontend.handle(path, request, servletRequest, response);

        ArgumentCaptor<String> captorLocation = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorValue = ArgumentCaptor.forClass(String.class);
        verify(response, times(1)).addHeader(captorLocation.capture(), captorValue.capture());

        Assert.assertEquals(captorLocation.getValue(), FrontendImpl.LOCATION);
    }

    @Test
    public void testPageNotFound() throws Exception {
        String path = "/abcderfg";
        Request request = mock(Request.class);

        HttpServletRequest servletRequest = getHttpServletRequest(getEmptyCookie());
        StringWriter html = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(html));

        frontend.handle(path, request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.ERROR), html.toString());
    }

    private UserDataSet getFakeNickName() {
        return new UserDataSet(100, "NickName", 100, 100, 100);
    }

    @Test
    public void testStatistic() throws Exception {
        Site statistic = Site.ADMIN;

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = getHttpServletRequest(getEmptyCookie());

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        Map<String, String> data = new HashMap<String, String>();
        for (Metric metric : Metric.values()) {
            data.put(metric.getFileName(), SystemInfo.getMetricInfo(metric));
        }

        frontend.handle(statistic.getUrl(), request, servletRequest, response);
        Assert.assertEquals(htmlTemplate.toString(), LoadPage.getPage(statistic.getHtmlPath(), data, null));
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
