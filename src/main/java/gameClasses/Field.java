package gameClasses;

public class Field {
    public static enum checker {nothing, black, white}

    ;
    private boolean king = false;
    private checker type;

    public Field(checker t) {
        type = t;
    }

    public Field() {
        type = checker.nothing;
    }

    public Field(Field field) {
        type = field.type;
        king = field.king;
    }

    public void make(Field field) {
        type = field.type;
        king = field.king;
    }

    public void setType(checker t) {
        type = t;
    }

    public boolean isKing() {
        return king;
    }

    public void makeKing() {
        king = true;
    }

    public void makeNotKing() {
        king = false;
    }

    public checker getType() {
        return type;
    }

    public void clear() {
        type = checker.nothing;
        king = false;
    }

    public boolean isEmpty() {
        if (type == checker.nothing)
            return true;
        else
            return false;
    }
}