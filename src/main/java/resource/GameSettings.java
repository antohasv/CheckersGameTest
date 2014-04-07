package resource;

import java.io.Serializable;

public class GameSettings implements Serializable, Resource {
    private static final long serialVersionUID = 8077975326166601597L;
    private int fieldSize;
    private int playerSize;
    private int strokeTime;

    public GameSettings() {
    }

    public GameSettings(int fieldSize, int playerSize) {
        this.fieldSize = fieldSize;
        this.playerSize = playerSize;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public int getPlayerFieldSize() {
        return playerSize;
    }

    public int getStrokeTime() {
        return strokeTime;
    }
}