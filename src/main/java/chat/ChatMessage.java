package chat;

public class ChatMessage {
    String sender, text;

    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getJson() {
        StringBuilder json = new StringBuilder("{\"sender\":\"").append(sender).append("\"");
        json.append(",").append("\"text\":").append("\"").append(text).append("\"}");
        return json.toString();
    }
}
