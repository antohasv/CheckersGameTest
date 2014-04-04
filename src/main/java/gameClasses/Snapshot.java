package gameClasses;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Snapshot {
    public static final String STATUS = "status";
    public static final String SNAPSHOT = "snapshot";
    public static final String NEXT = "next";
    public static final String COLOR = "color";
    public static final String FIELD = "field";
    public static final String KING = "king";
    private Field[][] field;
    char color;
    char next;
    int fieldSize;

    public Snapshot(Field[][] data, char col, int fieldSize, char next) {
        this.next = next;
        this.fieldSize = fieldSize;
        field = new Field[fieldSize][fieldSize];
        color = col;

        fillField(data, fieldSize);
    }

    private void fillField(Field[][] data, int fieldSize) {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                field[i][j] = data[i][j];
            }
        }
    }

    @Override
    public String toString() {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put(STATUS, SNAPSHOT);
        jsonResponse.put(NEXT, next);
        jsonResponse.put(COLOR, color);

        JSONArray fieldArray = new JSONArray();
        JSONArray kingArray = new JSONArray();

        JSONArray fieldInnerArray;
        JSONArray kingInnerArray;
        for (int i = 0; i < fieldSize; i++) {
            fieldInnerArray = new JSONArray();
            kingInnerArray = new JSONArray();
            for (int j = 0; j < fieldSize; j++) {
                fieldInnerArray.add(field[i][j].getType());
                kingInnerArray.add(field[i][j].isKing());
            }
            fieldArray.add(fieldInnerArray);
            kingArray.add(kingInnerArray);
        }

        jsonResponse.put(FIELD, fieldArray);
        jsonResponse.put(KING, kingArray);

        return jsonResponse.toJSONString();
    }

    public String toStringTest() {
        StringBuilder resp = new StringBuilder();
        resp.append("{'status':'snapshot',");
        resp.append("'next':'" + next + "',");
        if (color == 'w')
            resp.append("'color':'w',");
        else
            resp.append("'color':'b',");
        resp.append("'field':");
        int count1, count2;
        resp.append("[");
        for (count1 = 0; count1 < fieldSize; count1++) {
            if (count1 != 0)
                resp.append(", ");
            resp.append("[");
            for (count2 = 0; count2 < fieldSize; count2++) {
                if (count2 != 0)
                    resp.append(", ");
                resp.append("'" + field[count1][count2].getType() + "'");
            }
            resp.append("]");
        }
        resp.append("]");
        resp.append(",'king':");
        resp.append("[");
        for (count1 = 0; count1 < fieldSize; count1++) {
            if (count1 != 0)
                resp.append(", ");
            resp.append("[");
            for (count2 = 0; count2 < fieldSize; count2++) {
                if (count2 != 0)
                    resp.append(", ");
                resp.append("'" + field[count1][count2].isKing() + "'");
            }
            resp.append("]");
        }
        resp.append("]");
        resp.append("}");
        return resp.toString();
    }
}