package frontend;

import java.util.Map;

import dbService.UserDataSet;
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
    final private Address address;
    private static MessageSystem messageSystem = null;

    public WebSocketImpl(MessageSystem messageSystem) {
        this.address = new Address();
        this.messageSystem = messageSystem;
        messageSystem.addService(this, "WebSocket");
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public void onWebSocketText(String message) {
        if (isNotConnected()) {
            return;
        }
        String sessionId = null, startServerTime = null;
        int from_x = -1, from_y = -1, to_x = -1, to_y = -1;
        String status = null;
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(message);
            sessionId = json.get("sessionId").toString();
            startServerTime = json.get("startServerTime").toString();
            from_x = Integer.parseInt(json.get("from_x").toString());
            from_y = Integer.parseInt(json.get("from_y").toString());
            to_x = Integer.parseInt(json.get("to_x").toString());
            to_y = Integer.parseInt(json.get("to_y").toString());
            status = json.get("status").toString();
        } catch (ParseException parseException) {
        } catch (Exception ignor) {
        }
        if ((from_x != -1) && (from_y != -1) && (to_x != -1) && (to_y != -1) && (sessionId != null) &&
                (UserDataImpl.checkServerTime(startServerTime))) {
            checkStroke(sessionId, to_x, to_y, from_x, from_y, status);
        } else if ((sessionId != null) && (UserDataImpl.checkServerTime(startServerTime))) {
            addNewWS(sessionId);
        }
    }

    private void addNewWS(String sessionId) {
        UserDataSet userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
        if (userSession != null) {
            userSession.visit();
            UserDataImpl.putSessionIdAndWS(sessionId, this);
        }
    }

    private void checkStroke(String sessionId, int to_x, int to_y, int from_x, int from_y, String status) {
        Stroke stroke = new Stroke(to_x, to_y, from_x, from_y, status);
        UserDataSet userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
        userSession.visit();
        Address to = messageSystem.getAddressByName("GameMechanic");
        MsgCheckStroke msg = new MsgCheckStroke(address, to, userSession.getId(), stroke);
        messageSystem.putMsg(to, msg);
    }

    public void sendStroke(Map<Integer, Stroke> userIdToStroke) {
        String sessionId;
        UserDataSet userSession;
        Stroke stroke;
        try {
            for (Integer userId : userIdToStroke.keySet()) {
                sessionId = UserDataImpl.getSessionIdByUserId(userId);
                userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
                userSession.visit();
                stroke = new Stroke(userIdToStroke.get(userId));
                stroke.setColor(userSession.getColor());
                UserDataImpl.getWSBySessionId(sessionId).sendString(stroke.toString());
            }
        } catch (Exception e) {
            System.err.println("\nError:");
            System.err.println("WebSocketImpl, doneStroke");
            System.err.println(e.getMessage());
        }
    }

    public void updateUsersColor(Map<String, String> usersToColors) {
        UserDataSet userSession;
        String color, black = "{\"color\":\"black\"}", white = "{\"color\":\"white\"}";
        for (String sessionId : usersToColors.keySet()) {
            try {
                color = usersToColors.get(sessionId);
                userSession = UserDataImpl.getLogInUserBySessionId(sessionId);
                color = usersToColors.get(sessionId);
                if (color == "black") {
                    userSession.setColor("b");
                    UserDataImpl.getWSBySessionId(sessionId).sendString(black);
                } else if (color == "white") {
                    userSession.setColor("w");
                    UserDataImpl.getWSBySessionId(sessionId).sendString(white);
                }
            } catch (Exception e) {
                System.err.println("\nError:");
                System.err.println("WebSocketImpl, updateUsersColor");
                System.err.println(e.getMessage());
            }
        }
    }

    public void doneSnapshot(int userId, Snapshot snapshot) {
        String sessionId = UserDataImpl.getSessionIdByUserId(userId);
        try {
            UserDataImpl.getWSBySessionId(sessionId).sendString(snapshot.toString());
        } catch (Exception e) {
            System.err.println("\nError:");
            System.err.println("WebSocketImpl, doneSnapshot");
            System.err.println(e.getMessage());
        }
    }

    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            TimeHelper.sleep(200);
        }

    }
}