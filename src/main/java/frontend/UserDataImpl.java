package frontend;

import chat.ChatWebSocketImpl;
import dbService.UserDataSet;
import gameMechanic.gameCreating.MsgCreateGames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import dbService.MsgUpdateUsers;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;

import resource.Rating;
import resource.TimeSettings;

import base.Address;
import base.MessageSystem;

import utils.Caster;
import utils.SHA2;
import utils.TimeHelper;

public class UserDataImpl implements base.UserData {
    final private static String startServerTime = SHA2.getSHA2(TimeHelper.getCurrentTime());
    final private static Map<String, UserDataSet> sessionIdToUserSession =
            new ConcurrentHashMap<String, UserDataSet>();
    final private static Map<String, UserDataSet> logInUsers =
            new ConcurrentHashMap<String, UserDataSet>();
    final private static Map<String, UserDataSet> wantToPlay =
            new ConcurrentHashMap<String, UserDataSet>();
    final private static Map<String, WebSocketImpl> sessionIdToWS =
            new HashMap<String, WebSocketImpl>();
    final private static Map<String, ChatWebSocketImpl> sessionIdToChatWS =
            new HashMap<String, ChatWebSocketImpl>();
    static private MessageSystem messageSystem;
    final private Address address;

    public UserDataImpl(MessageSystem msgSystem) {
        messageSystem = msgSystem;
        address = new Address();
        messageSystem.addService(this, "UserDataSet");
    }

    public Address getAddress() {
        return address;
    }

    public static boolean checkServerTime(String value) {
        return (value.equals(startServerTime));
    }

    public static String getStartServerTime() {
        return startServerTime;
    }

    public static UserDataSet getUserSessionBySessionId(String sessionId) {
        return sessionIdToUserSession.get(sessionId);
    }

    public static boolean containsSessionId(String sessionId) {
        return sessionIdToUserSession.containsKey(sessionId);
    }

    public static int ccu() {
        return sessionIdToUserSession.size();
    }

    public static void putSessionIdAndUserSession(String sessionId, UserDataSet userSession) {
        sessionIdToUserSession.put(sessionId, userSession);
    }

    public static UserDataSet getLogInUserBySessionId(String sessionId) {
        if (logInUsers.get(sessionId) != null) {
            logInUsers.get(sessionId).visit();
        }
        return logInUsers.get(sessionId);
    }

    public static void playerWantToPlay(String sessionId, UserDataSet userSession) {
        wantToPlay.put(sessionId, userSession);
    }

    public static void putLogInUser(String sessionId, UserDataSet userSession) {
        logInUsers.put(sessionId, userSession);
    }

    public static String getSessionIdByUserId(int userId) {
        for (String sessionId : logInUsers.keySet()) {
            if ((logInUsers.get(sessionId) != null) && (logInUsers.get(sessionId).getId() == userId)) {
                return sessionId;
            }
        }
        return null;
    }

    public static void putSessionIdAndWS(String sessionId, WebSocketImpl WS) {
        sessionIdToWS.put(sessionId, WS);
    }

    public static void putSessionIdAndChatWS(String sessionId, ChatWebSocketImpl chatWS) {
        sessionIdToChatWS.put(sessionId, chatWS);
        if (logInUsers.get(sessionId) != null) {
            logInUsers.get(sessionId).visit();
        }
    }

    public static RemoteEndpoint getWSBySessionId(String sessionId) {
        if (sessionIdToWS.get(sessionId) == null)
            return null;
        else
            return sessionIdToWS.get(sessionId).getSession().getRemote();
    }

    public static RemoteEndpoint getChatWSBySessionId(String sessionId) {
        if (sessionIdToChatWS.get(sessionId) == null)
            return null;
        else
            return sessionIdToChatWS.get(sessionId).getSession().getRemote();
    }

    private String getOldUserSessionId(int id) {
        for (String sessionId : logInUsers.keySet()) {
            if (logInUsers.get(sessionId).getId() == id)
                return sessionId;
        }
        return null;
    }

    public void updateUserId(String sessionId, UserDataSet user) {
        if (user != null) {
            String sessiondIdOld = getOldUserSessionId(user.getId());
            if (sessiondIdOld != null) {
                removeUser(sessiondIdOld);
            }
            getUserSessionBySessionId(sessionId).makeLike(user);
        }
        getUserSessionBySessionId(sessionId).setPostStatus(0);
    }

    private void createGames() {
        Map<String, UserDataSet> sendMap =
                new ConcurrentHashMap<String, UserDataSet>();
        String[] keys = Caster.castKeysToStrings(wantToPlay);
        int count;
        String sessionId;
        UserDataSet userSession;
        for (count = 0; count < keys.length; count++) {
            sessionId = keys[count];
            userSession = wantToPlay.get(sessionId);
            wantToPlay.remove(sessionId);
            sendMap.put(sessionId, userSession);
        }
        if (sendMap.size() > 0) {
            Address to = messageSystem.getAddressByName("GameMechanic");
            MsgCreateGames msg = new MsgCreateGames(address, to, sendMap);
            messageSystem.putMsg(to, msg);
        }
    }

    private void removeUser(String sessionId) {
        sessionIdToUserSession.remove(sessionId);
        logInUsers.remove(sessionId);
        wantToPlay.remove(sessionId);
        sessionIdToWS.remove(sessionId);
        removeUserFromGM(sessionId);
    }

    private static void removeUserFromGM(String sessionId) {
        Address to = messageSystem.getAddressByName("GameMechanic");
        MsgRemoveUserFromGM msg = new MsgRemoveUserFromGM(null, to, sessionId);
        messageSystem.putMsg(to, msg);
    }

    private void keepAlive(String sessionId) {
        try {
            if (sessionIdToWS.get(sessionId) != null) {
                getWSBySessionId(sessionId).sendString("1");
                getLogInUserBySessionId(sessionId).visit();
            }
        } catch (Exception ignor) {
        }
    }

    private void checkUsers(int keepAlive) {
        for (String sessionId : sessionIdToUserSession.keySet()) {
            if (exitedUser(getUserSessionBySessionId(sessionId)))
                removeUser(sessionId);
            else if (keepAlive == 1)
                keepAlive(sessionId);
        }
    }

    private boolean exitedUser(UserDataSet userSession) {
        long curTime = TimeHelper.getCurrentTime();
        return (curTime - userSession.getLastVisit() > TimeSettings.getExitTime());
    }

    public void partyEnd(int winId, int loseId) {
        List<UserDataSet> updateUsers = new Vector<UserDataSet>();
        UserDataSet winUserSession = null, loseUserSession = null;
        String winSessionId = getSessionIdByUserId(winId);
        String loseSessionId = getSessionIdByUserId(loseId);
        sessionIdToChatWS.remove(winSessionId);
        sessionIdToChatWS.remove(loseSessionId);
        winUserSession = getUserSessionBySessionId(winSessionId);
        loseUserSession = getUserSessionBySessionId(loseSessionId);
        int diff = Rating.getAvgDiff();
        if ((loseUserSession != null) && (winUserSession != null)) {
            int winRating = winUserSession.getRating();
            int loseRating = loseUserSession.getRating();
            if (winRating != loseRating)
                diff = Rating.getDiff(winRating, loseRating);
        }
        if (loseUserSession != null) {
            loseUserSession.lose(diff);
            updateUsers.add(loseUserSession);
        }
        if (winUserSession != null) {
            winUserSession.win(diff);
            updateUsers.add(winUserSession);
        }
        if (updateUsers.size() != 0) {
            Address to = messageSystem.getAddressByName("DBService");
            MsgUpdateUsers msg = new MsgUpdateUsers(address, to, updateUsers);
            messageSystem.putMsg(to, msg);
        }
    }

    public void run() {
        int count = 0;
        while (true) {
            count = (count + 1) % 250;
            messageSystem.execForAbonent(this);
            checkUsers(count);
            createGames();
            TimeHelper.sleep(200);
        }
    }
}
