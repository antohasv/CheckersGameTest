package frontend;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbService.DBServiceImpl;
import dbService.UserDataSet;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import frontend.msg.*;

import system.Metric;
import utils.*;
import system.SystemInfo;

import base.Address;
import base.Frontend;
import base.MessageSystem;


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

    private AtomicInteger creatorSessionId = new AtomicInteger();
    final private Address address;
    final private MessageSystem messageSystem;

    enum Status {nothing, haveCookie, haveCookieAndPost, waiting, ready}

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

        data.put(PAGE, "admin.html");
        data.put(USER_ID, String.valueOf(userSession.getId()));
        data.put(USER_NICKNAME, String.valueOf(userSession.getNickName()));
        data.put(USER_RATING, String.valueOf(userSession.getRating()));
        try {
            TemplateHelper.renderTemplate("template.html", data, response.getWriter());
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

        if (target.equals("/wait")) {
            if ((status != Status.haveCookie && status != Status.haveCookieAndPost)
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

    private boolean isWebUrl(String target) {
        return (target.equals("/") || target.equals("/wait") || target.equals("/game") || target.equals("/profile")
                || target.equals("/admin") || target.equals("/rules") || target.equals("/logout") || target.equals("/reg"));
    }

    private boolean isUrlStatic(String target) {
        if (target.length() < 4)
            return false;
        else if (target.length() == 4)
            return target.substring(0, 4).equals("/js/");
        else return (((target.substring(0, 5)).equals("/img/")) || ((target.substring(0, 5)).equals("/css/")));
    }

    private boolean isNewUser(String strSessionId, String strStartServerTime) {
        return ((strSessionId == null) || (strStartServerTime == null)
                || (!UserDataImpl.checkServerTime(strStartServerTime))
                || (!UserDataImpl.containsSessionId(strSessionId)));
    }

    private void sendPage(String name, UserDataSet userSession, HttpServletResponse response) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put(PAGE, name);

            if (userSession != null) {
                data.put(USER_ID, String.valueOf(userSession.getId()));
                data.put(USER_NICKNAME, String.valueOf(userSession.getNickName()));
                data.put(USER_RATING, String.valueOf(userSession.getRating()));
            } else {
                data.put(USER_ID, DEFAULT_USER_ID);
                data.put(USER_NICKNAME, DEFAULT_USER_NICKNAME);
                data.put(USER_RATING, DEFAULT_USER_RATING);
            }

            TemplateHelper.renderTemplate("template.html", data, response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onNothingStatus(String target, String sessionId, UserDataSet userSession, String serverTime, HttpServletResponse response) {
        boolean moved = false;

        if (!target.equals("/")) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, "/");
            moved = true;
        }

        Cookie cookieSessionId = new Cookie(SESSION_ID, sessionId);
        Cookie cookieServerTime = new Cookie(SERVER_TIME, serverTime);

        response.addCookie(cookieSessionId);
        response.addCookie(cookieServerTime);

        if (!moved) {
            sendPage("index.html", userSession, response);
        }
    }

    private void onHaveCookieStatus(String target, UserDataSet userSession, HttpServletResponse response) {
        if (target.equals("/")) {
            sendPage("index.html", userSession, response);
        } else if (target.equals("/reg")) {
            sendPage("reg.html", userSession, response);
        } else {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, "/");
        }
    }

    private void onHaveCookieAndPostStatus(String target, String sessionId, UserDataSet userSession, HttpServletRequest request, HttpServletResponse response) {
        String nick = request.getParameter(USER_NICKNAME);
        String password = request.getParameter(USER_PASSWORD);

        if (nick == null || password == null) {
            nick = request.getParameter(PARAM_REG_NICKNAME);
            password = request.getParameter(PARAM_REG_PASSWORD);

            if ((nick == null) || (password == null) || (nick.equals("")) || (password.equals("")) || (nick.length() > 20)) {
                sendPage(target + ".html", userSession, response);
            } else {
                password = SHA2.getSHA2(password);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.addHeader(LOCATION, "/wait");
                userSession.setPostStatus(1);

                Address to = messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME);
                Address from = messageSystem.getAddressByName(UserDataImpl.SERVICE_NAME);
                MsgAddUser msg = new MsgAddUser(from, to, sessionId, nick, password);

                messageSystem.putMsg(to, msg);
            }
        } else {
            password = SHA2.getSHA2(password);
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, "/wait");

            userSession.setPostStatus(1);

            Address to = messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME);
            Address from = messageSystem.getAddressByName(UserDataImpl.SERVICE_NAME);

            MsgGetUser msg = new MsgGetUser(from, to, sessionId, nick, password);
            messageSystem.putMsg(to, msg);
        }
    }

    private void onWaitingStatus(HttpServletResponse response) {
        sendPage("wait.html", null, response);
    }

    private void onReadyStatus(String target, String sessionId, UserDataSet userSession, HttpServletResponse response) {
        if (target.equals("/")) {
            UserDataImpl.putLogInUser(sessionId, userSession);
            sendPage("index.html", userSession, response);
        } else if (target.equals("/game")) {
            UserDataImpl.putLogInUser(sessionId, userSession);
            UserDataImpl.playerWantToPlay(sessionId, userSession);
            sendPage("game.html", userSession, response);
        } else if (target.equals("/logout")) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, "/");
            String strSessionId = sessionId = SHA2.getSHA2(String.valueOf(creatorSessionId.incrementAndGet()));
            Cookie cookie = new Cookie(SESSION_ID, strSessionId);
            response.addCookie(cookie);
            UserDataImpl.putSessionIdAndUserSession(sessionId, new UserDataSet());
        } else if (target.equals("/profile")) {
            sendPage("profile.html", userSession, response);
        } else {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader(LOCATION, "/");
        }
    }

    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response) {
        UserDataSet userDataSet;

        prepareResponse(response);
        Status status = Status.nothing;

        StoreCookie storeCookie = new StoreCookie(request.getCookies());
        String sessionId = storeCookie.getCookieByName(SESSION_ID);
        String serverTime = storeCookie.getCookieByName(SERVER_TIME);

        //baseRequest.setHandled(true);

        if (isNewUser(sessionId, serverTime)) {
            userDataSet = new UserDataSet();

            sessionId = SHA2.getSHA2(String.valueOf(creatorSessionId.incrementAndGet()));
            serverTime = UserDataImpl.getStartServerTime();

            UserDataImpl.putSessionIdAndUserSession(sessionId, userDataSet);
        } else {
            status = Status.haveCookie;
            userDataSet = UserDataImpl.getUserSessionBySessionId(sessionId);
        }

        if (!isWebUrl(target)) {
            if (!isUrlStatic(target)) {
                sendPage("404.html", userDataSet, response);
            }
            return;
        }

        userDataSet.markLatestVisitTime();

        status = getStatus(request, target, status, sessionId);
        if (status != Status.haveCookieAndPost) {
            if (target.equals("/admin")) {
                getStatistic(response, userDataSet);
                return;
            } else if (target.equals("/rules")) {
                sendPage("rules.html", userDataSet, response);
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
}