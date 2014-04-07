package gameMechanic;

import java.util.HashMap;
import java.util.Map;

import base.Address;
import base.GameMechanic;
import base.MessageSystem;

import chat.GameChatImpl;
import dbService.UserDataSet;
import frontend.UserDataImpl;
import frontend.WebSocketImpl;
import gameClasses.Snapshot;
import gameClasses.Stroke;
import gameMechanic.Stroke.*;
import gameMechanic.gameCreating.MsgCreateChat;
import utils.Caster;
import utils.TimeHelper;

public class GameMechanicImpl implements GameMechanic {
    public static final String SERVICE_NAME = "GameMechanic";

    final public Map<Integer, GameSession> userIdToSession =
            new HashMap<Integer, GameSession>();
    final private Map<String, UserDataSet> wantToPlay =
            new HashMap<String, UserDataSet>();
    final private Address address;
    final private MessageSystem messageSystem;

    public GameMechanicImpl(MessageSystem msgSystem) {
        address = new Address();
        messageSystem = msgSystem;
        messageSystem.addService(this, SERVICE_NAME);
    }

    public Address getAddress() {
        return address;
    }

    public MessageSystem getMessageSystem() {
        return messageSystem;
    }


    public Map<String, UserDataSet> getWantToPlay() {
        return wantToPlay;
    }


    public Snapshot sendSnapshot(int userId) {
        Snapshot snapshot = getSnapshot(userId);
        Address to = getMessageSystem().getAddressByName(WebSocketImpl.SERVICE_NAME);
        MsgDoneSnapshot msg = new MsgDoneSnapshot(getAddress(), to, userId, snapshot);
        getMessageSystem().putMsg(to, msg);
        return snapshot;
    }

    public int randomMod2() {
        return (((int) (Math.random() * 1000)) % 2);
    }

    private void removeRepeatUsers(Map<String, UserDataSet> users) {
        String[] wantToPlayKeys = Caster.castKeysToStrings(wantToPlay);
        if (wantToPlayKeys.length > 0) {
            users.put(wantToPlayKeys[0], wantToPlay.get(wantToPlayKeys[0]));
            wantToPlay.clear();
        }
    }

    private void removeAlreadyInGameUsers(Map<String, UserDataSet> users) {
        String sessionId;
        int userId;
        String[] keys = Caster.castKeysToStrings(users);
        for (int count = 0; count < keys.length; count++) {
            sessionId = keys[count];
            userId = users.get(sessionId).getId();
            if (userIdToSession.containsKey(userId)) {
                users.remove(sessionId);
                sendSnapshot(userId);
            }
        }
    }

    private void createGame(String sessionIdWhite, String sessionIdBlack,
                            Map<String, String> sessionIdToColor, Map<String, UserDataSet> users) {
        int userIdWhite = users.get(sessionIdWhite).getId();
        int userIdBlack = users.get(sessionIdBlack).getId();
        GameSession gameSession = new GameSession(userIdWhite, userIdBlack);
        sessionIdToColor.put(sessionIdBlack, "black");
        sessionIdToColor.put(sessionIdWhite, "white");
        userIdToSession.put(userIdWhite, gameSession);
        userIdToSession.put(userIdBlack, gameSession);
        users.remove(sessionIdBlack);
        users.remove(sessionIdWhite);
        createChat(sessionIdWhite, sessionIdBlack);
    }

    private void createChat(String sessionIdWhite, String sessionIdBlack) {
        Address to = messageSystem.getAddressByName(GameChatImpl.SERVICE_NAME);
        MsgCreateChat msg = new MsgCreateChat(address, to, sessionIdWhite, sessionIdBlack);
        messageSystem.putMsg(to, msg);
    }

    private void moveUser(Map<String, UserDataSet> users) {
        String[] keys = Caster.castKeysToStrings(users);
        int number = ((int) Math.random() * 10000) % keys.length;
        String sessionId = keys[number];
        UserDataSet userSession = users.get(sessionId);
        users.remove(sessionId);
        wantToPlay.put(sessionId, userSession);
    }

    public Map<String, String> createGames(Map<String, UserDataSet> users) {
        Map<String, String> sessionIdToColor = new HashMap<String, String>();
        if (users.size() == 0) return sessionIdToColor;
        String sessionIdWhite, sessionIdBlack;
        removeRepeatUsers(users);
        removeAlreadyInGameUsers(users);
        if (users.size() % 2 == 1)
            moveUser(users);
        String[] keys = Caster.castKeysToStrings(users);
        for (int count = 0; count < users.size() / 2; count++) {
            if (randomMod2() == 1) {
                sessionIdBlack = keys[count * 2];
                sessionIdWhite = keys[count * 2 + 1];
            } else {
                sessionIdBlack = keys[count * 2 + 1];
                sessionIdWhite = keys[count * 2];
            }
            createGame(sessionIdWhite, sessionIdBlack, sessionIdToColor, users);
        }
        return sessionIdToColor;
    }

    public Map<Integer, Stroke> checkStroke(int id, Stroke stroke) {
        GameSession gameSession = userIdToSession.get(id);
        int from_x = stroke.getFrom_X();
        int from_y = stroke.getFrom_Y();
        int to_x = stroke.getTo_X();
        int to_y = stroke.getTo_Y();
        String status = stroke.getStatus();
        Map<Integer, Stroke> resp = new HashMap<Integer, Stroke>();
        if (gameSession == null) return resp;
        if (status.equals("lose")) {
            sendResultStroke(gameSession, gameSession.getAnotherId(id));
            return resp;
        }
        if (gameSession.checkStroke(id, from_x, from_y, to_x, to_y)) {
            stroke.setStatus("true");
            stroke.setNext(gameSession.getNext());
            resp.put(id, stroke);
            resp.put(gameSession.getAnotherId(id), stroke.getInverse());
        } else {
            stroke = new Stroke(from_x, from_y, to_x, to_y, "false", stroke.getColor(), gameSession.getNext());
            resp.put(id, stroke);
        }
        int winnerId = gameSession.getWinnerId();
        if (winnerId != 0) {
            //gameSession.saveLog(winnerId);
            updateUsersRating(winnerId, gameSession.getAnotherId(winnerId));
            resp.get(winnerId).setStatus("win");
            resp.get(gameSession.getAnotherId(winnerId)).setStatus("lose");
            userIdToSession.remove(id);
            userIdToSession.remove(gameSession.getAnotherId(id));
        }
        return resp;
    }

    public void sendResultStroke(GameSession gameSession, int winnerId) {
        Stroke winStroke = new Stroke("win");
        Stroke loseStroke = new Stroke("lose");
        Map<Integer, Stroke> resp = new HashMap<Integer, Stroke>();
//        gameSession.saveLog(winnerId);
        resp.clear();
        updateUsersRating(winnerId, gameSession.getAnotherId(winnerId));
        resp.put(winnerId, winStroke);
        resp.put(gameSession.getAnotherId(winnerId), loseStroke);
        Address to = messageSystem.getAddressByName(WebSocketImpl.SERVICE_NAME);
        MsgDoneStroke msg = new MsgDoneStroke(address, to, resp);
        messageSystem.putMsg(to, msg);
        userIdToSession.remove(winnerId);
        userIdToSession.remove(gameSession.getAnotherId(winnerId));
    }

    private void updateUsersRating(int winnerId, int loseId) {
        Address to = messageSystem.getAddressByName(UserDataImpl.SERVICE_NAME);
        MsgPartyEnd msg = new MsgPartyEnd(address, to, winnerId, loseId);
        messageSystem.putMsg(to, msg);
    }

    public  void removeDeadGames() {
        Object[] keys = userIdToSession.keySet().toArray();
        int count, winnerId;
        GameSession gameSession;
        for (count = 0; count < keys.length; count++) {
            gameSession = userIdToSession.get(keys[count]);
            if (gameSession == null) continue;
            winnerId = gameSession.getWinnerId();
            if (winnerId != 0) {
                sendResultStroke(gameSession, winnerId);
            }
        }
    }

    public Snapshot getSnapshot(int id) {
        GameSession gameSession = userIdToSession.get(id);
        return gameSession.getSnapshot(id);
    }

    public void removeUser(String sessionId) {
        wantToPlay.remove(sessionId);
    }

    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            removeDeadGames();
            TimeHelper.sleep(200);
        }
    }
}