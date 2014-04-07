package frontend;

import base.Address;
import base.MessageSystem;
import chat.ChatWebSocketImpl;
import dbService.DBServiceImpl;
import dbService.MsgUpdateUsers;
import dbService.UserDataSet;
import gameMechanic.GameMechanicImpl;
import gameMechanic.gameCreating.MsgCreateGames;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import resource.Rating;
import resource.TimeSettings;
import utils.Caster;
import utils.SHA2;
import utils.TimeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

    public class UserDataImpl implements base.UserData {
    public static final String SERVICE_NAME = "UserDataSet";
    public static final int TICK_TIME = 200;

    final private static String startServerTime = SHA2.getSHA2(TimeHelper.getCurrentTime());

    final private static Map<String, UserDataSet> sessionIdToUserSession =
            new ConcurrentHashMap<String, UserDataSet>();

    final private static Map<String, UserDataSet> logInUsers =
            new ConcurrentHashMap<String, UserDataSet>();

    private static Map<String, UserDataSet> wantToPlay =
            new ConcurrentHashMap<String, UserDataSet>();

    final public static Map<String, WebSocketImpl> sessionIdToWS =
            new HashMap<String, WebSocketImpl>();

    final private static Map<String, ChatWebSocketImpl> sessionIdToChatWS =
            new HashMap<String, ChatWebSocketImpl>();

    static private MessageSystem messageSystem;
    final private Address address;

    public UserDataImpl(MessageSystem msgSystem) {
        messageSystem = msgSystem;
        address = new Address();
        messageSystem.addService(this, SERVICE_NAME);
    }

    public Address getAddress() {
        return address;
    }

    public static boolean checkServerTime(String value) {
        return value.equals(startServerTime);
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

    public static int getCCU() {
        return sessionIdToUserSession.size();
    }


    public static void putSessionIdAndUserSession(String sessionId, UserDataSet userSession) {
        sessionIdToUserSession.put(sessionId, userSession);
    }

    public static UserDataSet getLogInUserBySessionId(String sessionId) {
        UserDataSet userDataSet = logInUsers.get(sessionId);

        if (userDataSet != null) {
            userDataSet.markLatestVisitTime();
        }
        return userDataSet;
    }

    public static void playerWantToPlay(String sessionId, UserDataSet userSession) {
        wantToPlay.put(sessionId, userSession);
    }

    public static void putLogInUser(String sessionId, UserDataSet userSession) {
        logInUsers.put(sessionId, userSession);
    }

    public static String getSessionIdByUserId(int userId) {
        for (String sessionId : logInUsers.keySet()) {
            if (logInUsers.get(sessionId) != null && logInUsers.get(sessionId).getId() == userId) {
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
            logInUsers.get(sessionId).markLatestVisitTime();
        }
    }

    public static RemoteEndpoint getWSBySessionId(String sessionId) {
        if (sessionIdToWS.get(sessionId) == null) {
            return null;
        }

        return sessionIdToWS.get(sessionId).getSession().getRemote();
    }

    public static RemoteEndpoint getChatWSBySessionId(String sessionId) {
        if (sessionIdToChatWS.get(sessionId) == null) {
            return null;
        }

        return sessionIdToChatWS.get(sessionId).getSession().getRemote();
    }

    private String getOldUserSessionId(int id) {
        for (String sessionId : logInUsers.keySet()) {
            if (logInUsers.get(sessionId).getId() == id) {
                return sessionId;
            }
        }
        return null;
    }

    public void updateUserId(String sessionId, UserDataSet user) {
        if (user != null) {
            String oldSessiondId = getOldUserSessionId(user.getId());
            if (oldSessiondId != null) {
                removeUser(oldSessiondId);
            }
            getUserSessionBySessionId(sessionId).makeLike(user);
        }
        getUserSessionBySessionId(sessionId).setPostStatus(0);
    }

    private void createGames() {
        Map<String, UserDataSet> sendMap =
                new ConcurrentHashMap<String, UserDataSet>();

        String[] keys = Caster.castKeysToStrings(wantToPlay);
        String sessionId;
        UserDataSet userSession;

        for (int i = 0; i < keys.length; i++) {
            sessionId = keys[i];
            userSession = wantToPlay.get(sessionId);
            wantToPlay.remove(sessionId);
            sendMap.put(sessionId, userSession);
        }

        if (sendMap.size() > 0) {
            Address to = messageSystem.getAddressByName(GameMechanicImpl.SERVICE_NAME);
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
        Address to = messageSystem.getAddressByName(GameMechanicImpl.SERVICE_NAME);
        MsgRemoveUserFromGM msg = new MsgRemoveUserFromGM(null, to, sessionId);
        messageSystem.putMsg(to, msg);
    }

    private void keepAlive(String sessionId) {
        try {
            if (sessionIdToWS.get(sessionId) != null) {
                getWSBySessionId(sessionId).sendString("1");
                getLogInUserBySessionId(sessionId).markLatestVisitTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUsers(int keepAlive) {
        for (String sessionId : sessionIdToUserSession.keySet()) {
            if (exitedUser(getUserSessionBySessionId(sessionId))) {
                removeUser(sessionId);
            } else if (keepAlive == 1) {
                keepAlive(sessionId);
            }
        }
    }

    private boolean exitedUser(UserDataSet userSession) {
        long curTime = TimeHelper.getCurrentTime();
        return curTime - userSession.getLastVisit() > TimeSettings.getExitTime();
    }

    public void partyEnd(int winId, int loseId) {
        List<UserDataSet> updateUsers = new Vector<UserDataSet>();

        String winSessionId = getSessionIdByUserId(winId);
        String loseSessionId = getSessionIdByUserId(loseId);

        sessionIdToChatWS.remove(winSessionId);
        sessionIdToChatWS.remove(loseSessionId);

        UserDataSet winUserSession = getUserSessionBySessionId(winSessionId);
        UserDataSet loseUserSession = getUserSessionBySessionId(loseSessionId);

        int diff = Rating.getAvgDiff();

        if (loseUserSession != null && winUserSession != null) {
            int winRating = winUserSession.getRating();
            int loseRating = loseUserSession.getRating();

            if (winRating != loseRating) {
                diff = Rating.getDiff(winRating, loseRating);
            }
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
            Address to = messageSystem.getAddressByName(DBServiceImpl.SERVICE_NAME);
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

            TimeHelper.sleep(TICK_TIME);
        }
    }
}
