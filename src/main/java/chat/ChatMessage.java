package chat;

import org.json.simple.JSONObject;

public class ChatMessage {
    public static final String SENDER = "sender";
    public static final String TEXT = "text";

    private String sender;
    private String text;

    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getJson() {
        JSONObject message = new JSONObject();
        message.put(SENDER, sender);
        message.put(TEXT, text);
        return message.toString();
    }
}
