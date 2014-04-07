package frontend;

import base.Address;
import base.MessageSystem;
import base.Msg;
import dbService.DBServiceImpl;
import dbService.UserDataSet;
import frontend.msg.MsgUpdateUser;
import org.eclipse.jetty.server.Request;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
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
    public static final String FAKE_PAGE_HTML = "/fake_page.html";
    public static final String LONG_USER_NAME = "looooooooooooooooooooooooooooooooooooooooooooooooooongUserName";
    public static final String FAKE_SESSION_ID = "fake_SessionId123";
    public static final String SESSION_ID = "sessionId";

    private MessageSystem messageSystem;
    private FrontendImpl frontend;
    private DBServiceImpl dbService;
    private UserDataImpl userData;

    private Site[] sitesWithSession = {Site.INDEX, Site.REG, Site.RULES};
    private Cookie[] cookies;

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

        cookies = getCookie();
        HttpServletHelper.setCookies(cookies);
        Assert.assertNotNull(cookies);
        Assert.assertNotEquals(cookies.length, 0);
    }

    @Test
    public void testSiteIndex() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequest();
        StringWriter stringWriter = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, response);
        Assert.assertEquals(stringWriter.toString(), LoadPage.getPage(Site.INDEX));
    }

    @Test
    public void testUnCorrectCookie() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestUnCorrectCookie();
        StringWriter stringWriter = new StringWriter();

        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        when(servletResponse.getWriter()).thenReturn(new PrintWriter(stringWriter));

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, servletResponse);
        Mockito.verify(servletResponse).getWriter();

        Assert.assertNotEquals(stringWriter.toString(), "");
    }

    @Test
    public void testSiteWithSesson() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie(FrontendImpl.HTTP_GET);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter htmlTemplate;
        for (Site site : sitesWithSession) {
            htmlTemplate = new StringWriter();
            when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

            frontend.handle(site.getUrl(), request, servletRequest, response);
            Assert.assertEquals(LoadPage.getPage(site), htmlTemplate.toString());
        }
    }

    @Test
    public void testSiteWithSessonRedirect() throws Exception {
        Cookie[] cookies = getCookie();

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie(FrontendImpl.HTTP_GET);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter htmlTemplate = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.GAME.getUrl(), request, servletRequest, response);
        verify(response).addHeader(Matchers.eq(FrontendImpl.LOCATION), Matchers.eq(Site.INDEX.getUrl()));
    }

    @Test
    public void testRegistration() throws Exception {
        Cookie[] cookies = getCookie();

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.REG.getUrl(), request, servletRequest, response);

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
    public void testRegistrationForm() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_NICKNAME)).thenReturn(LONG_USER_NAME);
        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.REG.getUrl(), request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.REG), htmlTemplate.toString());
    }


    @Test
    public void testRegistrationFormWithUnCorrectData() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.PARAM_REG_NICKNAME)).thenReturn(LONG_USER_NAME);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.REG.getUrl(), request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.REG), htmlTemplate.toString());
    }

    @Test
    public void testAuthorizationFormUncorrectData() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.REG.getUrl(), request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.REG), htmlTemplate.toString());
    }

    @Test
    public void testAuthorizeNoRegUser() throws Exception {
        Cookie[] cookies = getCookie();

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME + "1000");
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.REG.getUrl(), request, servletRequest, response);

        ArgumentCaptor<Address> captorAddress = ArgumentCaptor.forClass(Address.class);
        ArgumentCaptor<Msg> captorMsg = ArgumentCaptor.forClass(Msg.class);
        verify(messageSystem, times(1)).putMsg(captorAddress.capture(), captorMsg.capture());

        captorMsg.getValue().exec(dbService);
        verify(messageSystem, times(2)).putMsg(captorAddress.capture(), captorMsg.capture());
    }

    @Test
    public void testAuthorizeRegUser() throws Exception {
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

        frontend.handle(Site.REG.getUrl(), request, servletRequest, response);

        ArgumentCaptor<Address> captorAddress = ArgumentCaptor.forClass(Address.class);
        ArgumentCaptor<Msg> captorMsg = ArgumentCaptor.forClass(Msg.class);
        verify(messageSystem, times(1)).putMsg(captorAddress.capture(), captorMsg.capture());

        captorMsg.getValue().exec(dbService);
        verify(messageSystem, times(2)).putMsg(captorAddress.capture(), captorMsg.capture());
    }

    @Test
    public void testUserLogout() throws Exception {
        Cookie[] cookies = getCookie();
        Request request = mock(Request.class);

        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();
        when(servletRequest.getMethod()).thenReturn(FrontendImpl.HTTP_GET);

        StringWriter htmlTemplate = new StringWriter();

        HttpServletResponse response = mock(HttpServletResponse.class);

        StoreCookie storeCookie = new StoreCookie(cookies);
        UserDataImpl.putSessionIdAndUserSession(storeCookie.getCookieByName("sessionId"), getFakeNickName());

        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));
        frontend.handle(FrontendImpl.LOGOUT, request, servletRequest, response);

        verify(response).addHeader(Matchers.eq(FrontendImpl.LOCATION), Matchers.eq(Site.INDEX.getUrl()));
    }

    @Test
    public void testUserWait() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequest();

        HttpServletResponse response = mock(HttpServletResponse.class);
        frontend.handle(Site.WAIT.getUrl(), request, servletRequest, response);
        verify(response).addHeader(Matchers.eq(FrontendImpl.LOCATION), Matchers.eq(Site.INDEX.getUrl()));
    }

    @Test
    public void testUserWaitWithCookie() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        HttpServletResponse response = mock(HttpServletResponse.class);
        frontend.handle(Site.WAIT.getUrl(), request, servletRequest, response);
        verify(response).addHeader(Matchers.eq(FrontendImpl.LOCATION), Matchers.eq(Site.INDEX.getUrl()));

        servletRequest = HttpServletHelper.getRequestWithCookie(FrontendImpl.HTTP_GET);
        frontend.handle(Site.WAIT.getUrl(), request, servletRequest, response);
        verify(response, times(2)).addHeader(Matchers.eq(FrontendImpl.LOCATION), Matchers.eq(Site.INDEX.getUrl()));
    }

    @Test
    public void testUserWaitPost123() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        StringWriter html = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(html));

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, response);

        String sessionId = new StoreCookie(servletRequest.getCookies()).getCookieByName(SESSION_ID);
        Assert.assertEquals(UserDataImpl.getUserSessionBySessionId(sessionId).getPostStatus(), 1);


        Site[] readySite = new Site[]{ Site.INDEX, Site.GAME, Site.PROFILE};

        for (Site site : readySite) {
            performRequestReadyStatus(request, response, sessionId, site.getUrl());
        }

        performRequestReadyStatus(request, response, sessionId, FrontendImpl.LOGOUT);
        performRequestReadyStatus(request, response, sessionId, "fakesite.ru");

    }

    private void performRequestReadyStatus(Request request, HttpServletResponse response, String sessionId, String url) throws IOException {
        StringWriter html;HttpServletRequest servletRequest;
        html = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(html));

        UserDataImpl.getUserSessionBySessionId(sessionId).makeLike(new UserDataSet(100, "dsads", 0 , 0 , 0));
        servletRequest = HttpServletHelper.getRequestWithCookie(FrontendImpl.HTTP_GET);

        frontend.handle(url, request, servletRequest, response);
    }

    @Test
    public void testUserWaitPost() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, response);

        String sessionId = new StoreCookie(servletRequest.getCookies()).getCookieByName("sessionId");
        Assert.assertEquals(UserDataImpl.getUserSessionBySessionId(sessionId).getPostStatus(), 1);

        frontend.handle(Site.WAIT.getUrl(), request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.WAIT), htmlTemplate.toString());
    }

    @Test
    public void testUserWaitWithoutCookie() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, response);

        String sessionId = new StoreCookie(servletRequest.getCookies()).getCookieByName(SESSION_ID);
        Assert.assertEquals(UserDataImpl.getUserSessionBySessionId(sessionId).getPostStatus(), 1);

        servletRequest = HttpServletHelper.getRequest();
        frontend.handle(Site.WAIT.getUrl(), request, servletRequest, response);
        verify(response, times(1)).addHeader(Matchers.eq(FrontendImpl.LOCATION), Matchers.eq(Site.INDEX.getUrl()));
    }

    @Test
    public void testPageNotFound() throws Exception {
        String path = "/abcderfg";
        Request request = mock(Request.class);

        HttpServletRequest servletRequest = HttpServletHelper.getRequest();
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
        HttpServletRequest servletRequest = HttpServletHelper.getRequest();

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

    @Test
    public void testStaticUrl() throws Exception {

        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequest();

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter htmlTemplate = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle("/ab", request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.ERROR), htmlTemplate.toString());

        htmlTemplate = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle("/abc", request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.ERROR), htmlTemplate.toString());

        htmlTemplate = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle("/downloadssss.js", request, servletRequest, response);
        Assert.assertEquals(LoadPage.getPage(Site.ERROR), htmlTemplate.toString());

        htmlTemplate = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle("/css/", request, servletRequest, response);
        Assert.assertEquals(htmlTemplate.toString(), "");

        htmlTemplate = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle("/img/", request, servletRequest, response);
        Assert.assertEquals(htmlTemplate.toString(), "");
    }

/*
    @Test
    public void testVerifyUser() throws Exception {
        Request request = mock(Request.class);
        HttpServletRequest servletRequest = HttpServletHelper.getRequestWithCookie();

        when(servletRequest.getParameter(FrontendImpl.USER_NICKNAME)).thenReturn(FAKE_USERNAME);
        when(servletRequest.getParameter(FrontendImpl.USER_PASSWORD)).thenReturn(FAKE_PASSWORD);

        StringWriter htmlTemplate = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(htmlTemplate));

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, response);
        Address dbService = messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME);
        MsgUpdateUser msg = (MsgUpdateUser)messageSystem.getMessages().get(dbService).poll();
    }
*/

    public Cookie[] getCookie() throws IOException {
        Request request = mock(Request.class);

        HttpServletRequest servletRequest = HttpServletHelper.getRequest();

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        frontend.handle(Site.INDEX.getUrl(), request, servletRequest, response);

        ArgumentCaptor<Cookie> captorCookie = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(FrontendImplTest.INIT_NUMBER_OF_COOKIES)).addCookie(captorCookie.capture());

        List<Cookie> cookieList = captorCookie.getAllValues();
        Cookie[] cookies = new Cookie[cookieList.size()];
        cookieList.toArray(cookies);
        return cookies;
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }
}
