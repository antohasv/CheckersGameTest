package gameClasses;

public class Stroke {
    private int toX;
    private int toY;
    private int fromX;
    private int fromY;

    private String status = "";
    private String color = "";
    private char next = '0';

    public Stroke(int x1, int y1, int x2, int y2, String st) {
        toX = x1;
        toY = y1;
        fromX = x2;
        fromY = y2;
        status = st;
    }

    public Stroke(int x1, int y1, int x2, int y2, String st, String col) {
        toX = x1;
        toY = y1;
        fromX = x2;
        fromY = y2;
        status = st;
        color = col;
    }

    public Stroke(int x1, int y1, int x2, int y2, String st, String col, char next) {
        toX = x1;
        toY = y1;
        fromX = x2;
        fromY = y2;
        status = st;
        color = col;
        this.next = next;
    }

    public Stroke() {
        toX = toY = fromX = fromY = -1;
    }

    public Stroke(String stat) {
        toX = toY = fromX = fromY = -1;
        status = stat;
    }

    public Stroke(Stroke stroke) {
        toX = stroke.getTo_X();
        toY = stroke.getTo_Y();
        fromX = stroke.getFrom_X();
        fromY = stroke.getFrom_Y();
        status = stroke.getStatus();
        color = stroke.getColor();
        next = stroke.getNext();
    }

    public Stroke getInverse() {
        if (color == "b") {
            return new Stroke(7 - toX, 7 - toY, 7 - fromX, 7 - fromY, status, "w", next);
        } else {
            return new Stroke(7 - toX, 7 - toY, 7 - fromX, 7 - fromY, status, "b", next);
        }
    }

    public void clear() {
        toX = toY = fromX = fromY = -1;
        status = color = "";
        next = '\0';
    }

    public boolean isEmpty() {
        if (toX != -1 || toY != -1 || fromX != -1 || fromY != -1) {
            return false;
        }
        return true;
    }

    public int getTo_X() {
        return toX;
    }

    public int getTo_Y() {
        return toY;
    }

    public int getFrom_X() {
        return fromX;
    }

    public int getFrom_Y() {
        return fromY;
    }

    public String getStatus() {
        return status;
    }

    public String getColor() {
        return color;
    }

    public char getNext() {
        return next;
    }

    public void setTo_X(int x) {
        toX = x;
    }

    public void setTo_Y(int y) {
        toY = y;
    }

    public void setFrom_X(int x) {
        fromX = x;
    }

    public void setFrom_Y(int y) {
        fromY = y;
    }

    public void setStatus(String st) {
        status = st;
    }

    public void setColor(String col) {
        color = col;
    }

    public void setNext(char next) {
        this.next = next;
    }

    public void fullSet(int x1, int y1, int x2, int y2) {
        toX = x1;
        toY = y1;
        fromX = x2;
        fromY = y2;
    }

    public String toString() {
        return "{\"color\":\"" + color + "\",\"to_x\":" + toX + ",\"to_y\":" + toY +
                ",\"from_x\":" + fromX + ",\"from_y\":" + fromY +
                ",\"status\":\"" + status + "\",\"next\":\"" + next + "\"}";
    }

}