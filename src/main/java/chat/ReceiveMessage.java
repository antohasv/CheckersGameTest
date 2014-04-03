package chat;


import frontend.FrontendImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ReceiveMessage {

    public static final String TEXT = "text";

    private String sessionId;
    private String startServerTime;
    private String text;

    public ReceiveMessage(String sessionId, String startServerTime, String text) {
        this.sessionId = sessionId;
        this.startServerTime = startServerTime;
        this.text = text;
    }

    public static ReceiveMessage getMessage(String message) {
        String text = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(message);

            String sessionId = json.get(FrontendImpl.SESSION_ID).toString();
            String startServerTime = json.get(FrontendImpl.SERVER_TIME).toString();

            if (json.get(TEXT) != null) {
                text = json.get(TEXT).toString();
            }
            return new ReceiveMessage(sessionId, startServerTime, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getStartServerTime() {
        return startServerTime;
    }

    public String getSessionId() {
        return sessionId;
    }
}
