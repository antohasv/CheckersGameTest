package frontend;

import java.util.Map;

import dbService.UserDataSet;
import gameMechanic.GameMechanicImpl;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import base.Address;
import base.MessageSystem;
import base.WebSocket;

import gameMechanic.Stroke.MsgCheckStroke;

import gameClasses.Snapshot;
import gameClasses.Stroke;
import utils.TimeHelper;

public class WebSocketImpl extends WebSocketAdapter implements WebSocket {
    public static final String SERVICE_NAME = "WebSocket";
    public static final int TICK_TIME = 200;
    public static final String COLOR_BLACK = "black";
    public static final String COLOR_WHITE = "white";
    public static final String FROM_X = "from_x";
    public static final String FROM_Y = "from_y";
    public static final String TO_X = "to_x";
    public static final String TO_Y = "to_y";
    public static final String STATUS = "status";
    public static final String COLOR = "color";
    public static final String SESSION_ID = "sessionId";
    final private Address address;
    private static MessageSystem messageSystem = null;

    public static final String JSON_COLOR_BLACK = getJSON(COLOR, COLOR_BLACK);
    public static final String JSON_COLOR_WHITE = getJSON(COLOR, COLOR_WHITE);

    public WebSocketImpl() {
        this.address = new Address();
    }

    public WebSocketImpl(MessageSystem messageSystem) {
        this.address = new Address();
        this.messageSystem = messageSystem;
        messageSystem.addService(this, SERVICE_NAME);
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public void onWebSocketText(String message) {
        if (isNotConnected()) {
            return;
        }

        int fromX = -1;
        int fromY = -1;
        int toX = -1;
        int toY = -1;
        String sessionId = null;
        String startServerTime = null;

        String status = null;
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(message);
            sessionId = json.get(SESSION_ID).toString();
            startServerTime = json.get(FrontendImpl.SERVER_TIME).toString();

            if (isLocationExist(json)) {
                fromX = Integer.parseInt(json.get(FROM_X).toString());
                fromY = Integer.parseInt(json.get(FROM_Y).toString());
                toX = Integer.parseInt(json.get(TO_X).toString());
                toY = Integer.parseInt(json.get(TO_Y).toString());
            }

            if (json.get(STATUS) != null) {
                status = json.get(STATUS).toString();
            }
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        if (fromX != -1 && fromY != -1 && toX != -1 && toY != -1 && sessionId != null && UserDataImpl.checkServerTime(startServerTime)) {
            checkStroke(sessionId, toX, toY, fromX, fromY, status);
        } else if (sessionId != null && UserDataImpl.checkServerTime(startServerTime)) {
            addNewWS(sessionId);
        }
    }

    private boolean isLocationExist(JSONObject json) {
        return json.get(FROM_X) != null && json.get(FROM_Y) != null && json.get(TO_X) != null && json.get(TO_Y) != null;
    }

    private void addNewWS(String sessionId) {
        UserDataSet userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
        if (userSession != null) {
            userSession.markLatestVisitTime();
            UserDataImpl.putSessionIdAndWS(sessionId, this);
        }
    }

    private void checkStroke(String sessionId, int toX, int toY, int fromX, int fromY, String status) {
        Stroke stroke = new Stroke(toX, toY, fromX, fromY, status);

        UserDataSet userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
        userSession.markLatestVisitTime();

        Address to = messageSystem.getAddressByName(GameMechanicImpl.SERVICE_NAME);
        MsgCheckStroke msg = new MsgCheckStroke(address, to, userSession.getId(), stroke);
        messageSystem.putMsg(to, msg);
    }

    public void sendStroke(Map<Integer, Stroke> userIdToStroke) {
        String sessionId;
        UserDataSet userSession;
        Stroke stroke;
        try {
            for (Integer userId : userIdToStroke.keySet()) {
                Stroke userIdStroke = userIdToStroke.get(userId);

                sessionId = UserDataImpl.getSessionIdByUserId(userId);

                userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
                userSession.markLatestVisitTime();

                stroke = new Stroke(userIdStroke);
                stroke.setColor(userSession.getColor());

                UserDataImpl.getWSBySessionId(sessionId).sendString(stroke.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getJSON(String key, String value) {
        JSONObject obj = new JSONObject();
        obj.put(key, value);
        return obj.toJSONString();
    }

    public void updateUsersColor(Map<String, String> usersToColors) {
        UserDataSet userSession;
        String color;

        for (String sessionId : usersToColors.keySet()) {
            try {
                userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
                color = usersToColors.get(sessionId);
                if (color.equals(COLOR_BLACK)) {
                    userSession.setColor("b");
                    UserDataImpl.getWSBySessionId(sessionId).sendString(JSON_COLOR_BLACK);
                } else if (color.equals(COLOR_WHITE)) {
                    userSession.setColor("w");
                    UserDataImpl.getWSBySessionId(sessionId).sendString(JSON_COLOR_WHITE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doneSnapshot(int userId, Snapshot snapshot) {
        String sessionId = UserDataImpl.getSessionIdByUserId(userId);
        try {
            UserDataImpl.getWSBySessionId(sessionId).sendString(snapshot.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            TimeHelper.sleep(TICK_TIME);
        }

    }
}