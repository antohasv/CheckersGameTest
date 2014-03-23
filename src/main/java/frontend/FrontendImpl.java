package frontend;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbService.UserDataSet;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import frontend.newOrLoginUser.*;

import system.Metric;
import utils.*;
import system.SystemInfo;

import base.Address;
import base.Frontend;
import base.MessageSystem;


public class FrontendImpl extends AbstractHandler implements Frontend {
    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String EXPIRES = "Expires";
    public static final String CACHE_CONTROL_VALUES = "no-store, no-cache, must-revalidate";
    public static final String FRONTEND = "Frontend";

    private AtomicInteger creatorSessionId = new AtomicInteger();
    final private Address address;
    final private MessageSystem messageSystem;

    enum status {nothing, haveCookie, haveCookieAndPost, waiting, ready}

    public FrontendImpl(MessageSystem msgSystem) {
        address = new Address();
        messageSystem = msgSystem;
        messageSystem.addService(this, FRONTEND);
    }

    public Address getAddress() {
        return address;
    }

    private void getStatistic(HttpServletResponse response, UserDataSet userSession) {
        Map<String, String> data = new HashMap<String, String>();

        for (Metric metric : Metric.values()) {
            data.put(metric.name(), SystemInfo.getMetricInfo(metric));
        }

        data.put("page", "admin.html");
        data.put("id", String.valueOf(userSession.getId()));
        data.put("nick", String.valueOf(userSession.getNick()));
        data.put("rating", String.valueOf(userSession.getRating()));
        try {
            TemplateHelper.renderTemplate("template.html", data, response.getWriter());
        } catch (IOException ignor) {
        }
    }

    private status getStatus(HttpServletRequest request, String target, status stat, String sessionId) {
        if ((stat.equals(status.haveCookie)) && (request.getMethod().equals("POST")))
            stat = status.haveCookieAndPost;
        if ((stat.equals(status.haveCookie)) && (UserDataImpl.getUserSessionBySessionId(sessionId).getId() != 0))
            stat = status.ready;
        if (target.equals("/wait")) {
            if ((!stat.equals(status.haveCookie) && (!stat.equals(status.haveCookieAndPost)))
                    || (UserDataImpl.getUserSessionBySessionId(sessionId).getPostStatus() == 0))
                stat = status.nothing;
            else
                stat = status.waiting;
        }
        return stat;
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setContentType(TEXT_HTML_CHARSET_UTF_8);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VALUES);
        response.setHeader(EXPIRES, TimeHelper.getGMT());
    }

    private boolean inWeb(String target) {
        return ((target.equals("/")) || (target.equals("/wait")) || (target.equals("/game")) || (target.equals("/profile"))
                || (target.equals("/admin")) || (target.equals("/rules")) || (target.equals("/logout")) || (target.equals("/reg")));
    }

    private boolean isStatic(String target) {
        if (target.length() < 4)
            return false;
        else if (target.length() == 4)
            return target.substring(0, 4).equals("/js/");
        else return (((target.substring(0, 5)).equals("/img/")) || ((target.substring(0, 5)).equals("/css/")));
    }

    private boolean newUser(String strSessionId, String strStartServerTime) {
        return ((strSessionId == null) || (strStartServerTime == null)
                || (!UserDataImpl.checkServerTime(strStartServerTime))
                || (!UserDataImpl.containsSessionId(strSessionId)));
    }

    private void sendPage(String name, UserDataSet userSession, HttpServletResponse response) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            data.put("page", name);
            if (userSession != null) {
                data.put("id", String.valueOf(userSession.getId()));
                data.put("nick", String.valueOf(userSession.getNick()));
                data.put("rating", String.valueOf(userSession.getRating()));
            } else {
                data.put("id", "0");
                data.put("nick", "Noname");
                data.put("rating", "500");
            }
            TemplateHelper.renderTemplate("template.html", data, response.getWriter());
        } catch (IOException ignor) {
        }
    }

    private void onNothingStatus(String target, String strSessionId, UserDataSet userSession, String strStartServerTime, HttpServletResponse response) {
        boolean moved = false;
        if (!target.equals("/")) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader("Location", "/");
            moved = true;
        }
        Cookie cookie1 = new Cookie("sessionId", strSessionId);
        Cookie cookie2 = new Cookie("startServerTime", strStartServerTime);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
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
            response.addHeader("Location", "/");
        }
    }

    private void onHaveCookieAndPostStatus(String target, String sessionId, UserDataSet userSession, HttpServletRequest request, HttpServletResponse response) {
        String nick = request.getParameter("nick");
        String password = request.getParameter("password");
        if ((nick == null) || (password == null)) {
            nick = request.getParameter("regNick");
            password = request.getParameter("regPassword");
            if ((nick == null) || (password == null) || (nick.equals("")) || (password.equals("")) || (nick.length() > 20)) {
                sendPage(target + ".html", userSession, response);
            } else {
                password = SHA2.getSHA2(password);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.addHeader("Location", "/wait");
                userSession.setPostStatus(1);
                Address to = messageSystem.getAddressByName("DBService");
                Address from = messageSystem.getAddressByName("UserDataSet");
                MsgAddUser msg = new MsgAddUser(from, to, sessionId, nick, password);
                messageSystem.putMsg(to, msg);
            }
        } else {
            password = SHA2.getSHA2(password);
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader("Location", "/wait");
            userSession.setPostStatus(1);
            Address to = messageSystem.getAddressByName("DBService");
            Address from = messageSystem.getAddressByName("UserDataSet");
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
            response.addHeader("Location", "/");
            String strSessionId = sessionId = SHA2.getSHA2(String.valueOf(creatorSessionId.incrementAndGet()));
            Cookie cookie = new Cookie("sessionId", strSessionId);
            response.addCookie(cookie);
            UserDataImpl.putSessionIdAndUserSession(sessionId, new UserDataSet());
        } else if (target.equals("/profile")) {
            sendPage("profile.html", userSession, response);
        } else {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader("Location", "/");
        }
    }

    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response) {
        prepareResponse(response);
        status stat = status.nothing;
        CookieDescriptor cookie = new CookieDescriptor(request.getCookies());
        String sessionId = cookie.getCookieByName("sessionId");
        String strStartServerTime = cookie.getCookieByName("startServerTime");
        UserDataSet userSession;
        baseRequest.setHandled(true);
        if (newUser(sessionId, strStartServerTime)) {
            userSession = new UserDataSet();
            sessionId = SHA2.getSHA2(String.valueOf(creatorSessionId.incrementAndGet()));
            strStartServerTime = UserDataImpl.getStartServerTime();
            UserDataImpl.putSessionIdAndUserSession(sessionId, userSession);
        } else {
            stat = status.haveCookie;
            userSession = UserDataImpl.getUserSessionBySessionId(sessionId);
        }
        if (!inWeb(target)) {
            if (!isStatic(target)) {
                sendPage("404.html", userSession, response);
            }
            return;
        }
        userSession.visit();
        stat = getStatus(request, target, stat, sessionId);
        if (stat != status.haveCookieAndPost) {
            if (target.equals("/admin")) {
                getStatistic(response, userSession);
                return;
            } else if (target.equals("/rules")) {
                sendPage("rules.html", userSession, response);
                return;
            }
        }
        switch (stat) {
            case nothing:
                onNothingStatus(target, sessionId, userSession, strStartServerTime, response);
                break;
            case haveCookie:
                onHaveCookieStatus(target, userSession, response);
                break;
            case haveCookieAndPost:
                onHaveCookieAndPostStatus(target, sessionId, userSession, request, response);
                break;
            case waiting:
                onWaitingStatus(response);
                break;
            case ready:
                onReadyStatus(target, sessionId, userSession, response);
                break;
        }
    }
}