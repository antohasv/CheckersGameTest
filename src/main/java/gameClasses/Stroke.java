package gameClasses;

import org.json.simple.JSONObject;

public class Stroke {
    public static final String COLOR_WHITE = "w";
    public static final String COLOR_BLACK = "b";
    public static final String COLOR = "color";
    public static final String TO_X = "to_x";
    public static final String TO_Y = "to_y";
    public static final String FROM_X = "from_x";
    public static final String FROM_Y = "from_y";
    public static final String STATUS = "status";
    public static final String NEXT = "next";
    public static final char DEFAULT_NEXT = '0';
    public static final int LENGTH_CHESSBOARD = 7;

    private int toX;
    private int toY;
    private int fromX;
    private int fromY;

    private String status = "";
    private String color = "";
    private char next = DEFAULT_NEXT;

    public Stroke(int x1, int y1, int x2, int y2, String st) {
        this(x1, y1, x2, y2, st, "", DEFAULT_NEXT);
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
        if (color.equals(COLOR_BLACK)) {
            return new Stroke(LENGTH_CHESSBOARD - toX, LENGTH_CHESSBOARD - toY, LENGTH_CHESSBOARD - fromX, LENGTH_CHESSBOARD - fromY, status, COLOR_WHITE, next);
        } else {
            return new Stroke(LENGTH_CHESSBOARD - toX, LENGTH_CHESSBOARD - toY, LENGTH_CHESSBOARD - fromX, LENGTH_CHESSBOARD - fromY, status, COLOR_BLACK, next);
        }
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

    public void setStatus(String st) {
        status = st;
    }

    public void setColor(String col) {
        color = col;
    }

    public void setNext(char next) {
        this.next = next;
    }

    public String toString() {
        JSONObject stroke = new JSONObject();
        stroke.put(COLOR, color);
        stroke.put(TO_X, toX);
        stroke.put(TO_Y, toY);
        stroke.put(FROM_X, fromX);
        stroke.put(FROM_Y, fromY);
        stroke.put(STATUS, status);
        stroke.put(NEXT, next);
        return stroke.toJSONString();
    }
}