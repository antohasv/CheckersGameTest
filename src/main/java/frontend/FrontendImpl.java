package frontend;


import base.Address;
import base.Frontend;
import base.MessageSystem;
import com.google.inject.Inject;
import dbService.DBServiceImpl;
import dbService.UserDataSet;
import frontend.msg.MsgAddUser;
import frontend.msg.MsgGetUser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import system.Metric;
import system.SystemInfo;
import utils.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FrontendImpl extends AbstractHandler implements Frontend {
    public static final String SERVICE_NAME = "Frontend";

    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String EXPIRES = "Expires";
    public static final String CACHE_CONTROL_VALUES = "no-store, no-cache, must-revalidate";

    public static final String SESSION_ID = "sessionId";
    public static final String SERVER_TIME = "startServerTime";

    public static final String USER_ID = "id";
    public static final String USER_RATING = "rating";
    public static final String DEFAULT_USER_ID = "0";
    public static final String DEFAULT_USER_NICKNAME = "Noname";
    public static final String DEFAULT_USER_RATING = "500";
    public static final String PAGE = "page";
    public static final String LOCATION = "Location";

    public static final String USER_NICKNAME = "nick";
    public static final String USER_PASSWORD = "password";

    public static final String PARAM_REG_NICKNAME = "regNick";
    public static final String PARAM_REG_PASSWORD = "regPassword";

    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";

    public static final int MIN_URL_LENGTH = 4;

    public static final String JS = "/js/";
    public static final String IMG = "/img/";
    public static final String CSS = "/css/";

    public static final int MAX_USER_NAME = 20;

    public static final String TEMPLATE_HTML = "template.html";
    public static final String LOGOUT = "/logout";

    enum Status {nothing, haveCookie, haveCookieAndPost, waiting, ready}

    String[] webUrls = {Site.INDEX.getUrl(), Site.WAIT.getUrl(), Site.GAME.getUrl(), Site.PROFILE.getUrl(),
            Site.ADMIN.getUrl(), Site.RULES.getUrl(), Site.REG.getUrl(), LOGOUT};

    private AtomicInteger creatorSessionId = new AtomicInteger();
    final private Address address;
    final private MessageSystem messageSystem;

    @Inject
    public FrontendImpl(MessageSystem msgSystem) {
        address = new Address();
        messageSystem = msgSystem;
        messageSystem.addService(this, SERVICE_NAME);
    }

    public Address getAddress() {
        return address;
    }

    private void getStatistic(HttpServletResponse response, UserDataSet userSession) {
        Map<String, String> data = new HashMap<String, String>();

        for (Metric metric : Metric.values()) {
            data.put(metric.getFileName(), SystemInfo.getMetricInfo(metric));
        }

        data.put(PAGE, Site.ADMIN.getHtmlPath());
        data.put(USER_ID, String.valueOf(userSession.getId()));
        data.put(USER_NICKNAME, String.valueOf(userSession.getNickName()));
        data.put(USER_RATING, String.valueOf(userSession.getRating()));
        try {
            TemplateHelper.renderTemplate(TEMPLATE_HTML, data, response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Status getStatus(HttpServletRequest request, String target, Status status, String sessionId) {
        if (status == status.haveCookie && request.getMethod().equals(HTTP_POST)) {
            status = Status.haveCookieAndPost;
        }

        if (status == Status.haveCookie && UserDataImpl.getUserSessionBySessionId(sessionId).getId() != 0) {
            status = Status.ready;
        }

        if (target.equals(Site.WAIT.getUrl())) {
            if (status != Status.haveCookie && status != Status.haveCookieAndPost
                    || UserDataImpl.getUserSessionBySessionId(sessionId).getPostStatus() == 0) {
                status = Status.nothing;
            } else {
                status = Status.waiting;
            }
        }
        return status;
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setContentType(TEXT_HTML_CHARSET_UTF_8);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VALUES);
        response.setHeader(EXPIRES, TimeHelper.getGMT());
    }

    private boolean isWebUrl(String url) {
        for (String accessUrl : webUrls) {
            if (accessUrl.equals(url)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUrlStatic(String url) {
        if (url.length() < MIN_URL_LENGTH) {
            return false;
        }

        if (url.length() == MIN_URL_LENGTH) {
            return url.substring(0, 4).equals(JS);
        }

        return url.substring(0, 5).equals(IMG) || url.substring(0, 5).equals(CSS);
    }

    private boolean isNewUser(String sessionId, String startServerTime) {
        return (sessionId == null || startServerTime == null
                || !UserDataImpl.checkServerTime(startServerTime)
                || !UserDataImpl.containsSessionId(sessionId));
    }

    private void sendPage(Site site, UserDataSet userSession, HttpServletResponse response) {
        sendPage(site.getHtmlPath(), userSession, response);
    }

    private void sendPage(String name, UserDataSet userSession, HttpServletResponse response) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put(PAGE, name);
            createUserSession(userSession, data);

            TemplateHelper.renderTemplate(TEMPLATE_HTML, data, response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUserSession(UserDataSet userSession, Map<String, String> data) {
        if (userSession != null) {
            data.put(USER_ID, String.valueOf(userSession.getId()));
            data.put(USER_NICKNAME, String.valueOf(userSession.getNickName()));
            data.put(USER_RATING, String.valueOf(userSession.getRating()));
        } else {
            data.put(USER_ID, DEFAULT_USER_ID);
            data.put(USER_NICKNAME, DEFAULT_USER_NICKNAME);
            data.put(USER_RATING, DEFAULT_USER_RATING);
        }
    }

    private void onNothingStatus(String target, String sessionId, UserDataSet userSession, String serverTime, HttpServletResponse response) {
        boolean moved = false;

        if (!target.equals(Site.INDEX.getUrl())) {
            moved = true;
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, Site.INDEX.getUrl());
        }

        Cookie cookieSessionId = new Cookie(SESSION_ID, sessionId);
        Cookie cookieServerTime = new Cookie(SERVER_TIME, serverTime);

        response.addCookie(cookieSessionId);
        response.addCookie(cookieServerTime);

        if (!moved) {
            sendPage(Site.INDEX, userSession, response);
        }
    }

    private void onHaveCookieStatus(String target, UserDataSet userSession, HttpServletResponse response) {
        Site site = Site.getSite(target);
        switch (site) {
            case INDEX:
                sendPage(Site.INDEX, userSession, response);
                break;
            case REG:
                sendPage(Site.REG, userSession, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.addHeader(LOCATION, Site.INDEX.getUrl());
                break;
        }
    }

    private void onHaveCookieAndPostStatus(String target, String sessionId, UserDataSet userSession, HttpServletRequest request, HttpServletResponse response) {
        final String nick = request.getParameter(USER_NICKNAME);
        final String password = request.getParameter(USER_PASSWORD);

        if (isLoginForm(nick, password)) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, Site.WAIT.getUrl());

            userSession.setPostStatus(1);
            sendDBMsgToVerifyUser(sessionId, nick, SHA2.getSHA2(password));
        } else {
            final String regNick = request.getParameter(PARAM_REG_NICKNAME);
            final String regPassword = request.getParameter(PARAM_REG_PASSWORD);

            if (isRegistrationForm(regNick, regPassword)) {
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.addHeader(LOCATION, Site.WAIT.getUrl());
                userSession.setPostStatus(1);

                addUserToDB(sessionId, regNick, SHA2.getSHA2(regPassword));
            } else {
                sendPage(target + ".html", userSession, response);
            }
        }
    }

    private boolean isRegistrationForm(String regNick, String regPassword) {
        return !TextUtils.isEmpty(regNick) && !TextUtils.isEmpty(regPassword) && regNick.length() <= MAX_USER_NAME;
    }

    private boolean isLoginForm(String nick, String password) {
        return !TextUtils.isEmpty(nick) && !TextUtils.isEmpty(password);
    }

    private void sendDBMsgToVerifyUser(String sessionId, String nick, String password) {
        Address to = messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME);
        Address from = messageSystem.getAddressByName(UserDataImpl.SERVICE_NAME);

        MsgGetUser msgUserGet = new MsgGetUser(from, to, sessionId, nick, password);
        messageSystem.putMsg(to, msgUserGet);
    }

    private void addUserToDB(String sessionId, String nick, String password) {
        Address to = messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME);
        Address from = messageSystem.getAddressByName(UserDataImpl.SERVICE_NAME);
        MsgAddUser msgAddUser = new MsgAddUser(from, to, sessionId, nick, password);

        messageSystem.putMsg(to, msgAddUser);
    }

    private void onWaitingStatus(HttpServletResponse response) {
        sendPage(Site.WAIT, null, response);
    }

    private void onReadyStatus(String target, String sessionId, UserDataSet userSession, HttpServletResponse response) {
        if (target.equals(LOGOUT)) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, Site.INDEX.getUrl());
            sessionId = generateSessionId();

            Cookie cookie = new Cookie(SESSION_ID,  generateSessionId());
            response.addCookie(cookie);

            UserDataImpl.putSessionIdAndUserSession(sessionId, new UserDataSet());
        }

        Site site = Site.getSite(target);

        if (site == Site.DEFAULT) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, Site.INDEX.getUrl());
            return;
        }

        switch (site) {
            case INDEX:
                UserDataImpl.putLogInUser(sessionId, userSession);
                sendPage(Site.INDEX, userSession, response);
                break;
            case GAME:
                UserDataImpl.putLogInUser(sessionId, userSession);
                UserDataImpl.playerWantToPlay(sessionId, userSession);
                sendPage(Site.GAME, userSession, response);
                break;
            case PROFILE:
                sendPage(Site.PROFILE, userSession, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.addHeader(LOCATION, Site.INDEX.getUrl());
                break;
        }
    }

    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response) {
        UserDataSet userDataSet;
        Status status = Status.nothing;

        StoreCookie storeCookie = new StoreCookie(request.getCookies());

        String sessionId = storeCookie.getCookieByName(SESSION_ID);
        String serverTime = storeCookie.getCookieByName(SERVER_TIME);

        prepareResponse(response);
        baseRequest.setHandled(true);

        if (isNewUser(sessionId, serverTime)) {
            userDataSet = new UserDataSet();
            sessionId = generateSessionId();
            serverTime = UserDataImpl.getStartServerTime();

            UserDataImpl.putSessionIdAndUserSession(sessionId, userDataSet);
        } else {
            status = Status.haveCookie;
            userDataSet = UserDataImpl.getUserSessionBySessionId(sessionId);
        }

        if (checkWebUrlConformity(target, response, userDataSet)) return;

        userDataSet.markLatestVisitTime();

        status = getStatus(request, target, status, sessionId);

        if (status != Status.haveCookieAndPost) {
            if (target.equals(Site.ADMIN.getUrl())) {
                getStatistic(response, userDataSet);
                return;
            } else if (target.equals(Site.RULES.getUrl())) {
                sendPage(Site.RULES, userDataSet, response);
                return;
            }
        }

        switch (status) {
            case nothing:
                onNothingStatus(target, sessionId, userDataSet, serverTime, response);
                break;
            case haveCookie:
                onHaveCookieStatus(target, userDataSet, response);
                break;
            case haveCookieAndPost:
                onHaveCookieAndPostStatus(target, sessionId, userDataSet, request, response);
                break;
            case waiting:
                onWaitingStatus(response);
                break;
            case ready:
                onReadyStatus(target, sessionId, userDataSet, response);
                break;
        }
    }

    private boolean checkWebUrlConformity(String target, HttpServletResponse response, UserDataSet userDataSet) {
        if (!isWebUrl(target)) {
            if (!isUrlStatic(target)) {
                sendPage(Site.ERROR, userDataSet, response);
            }
            return true;
        }
        return false;
    }

    private String generateSessionId() {
        return SHA2.getSHA2(String.valueOf(creatorSessionId.incrementAndGet()));
    }
}