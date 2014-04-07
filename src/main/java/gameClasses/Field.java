package gameClasses;

public class Field {
    public static enum Checker {nothing, black, white}


    private boolean king = false;
    private Checker type;

    public Field(Checker t) {
        type = t;
    }

    public Field() {
        type = Checker.nothing;
    }

    public Field(Field field) {
        type = field.type;
        king = field.king;
    }

    public void make(Field field) {
        type = field.type;
        king = field.king;
    }

    public void setType(Checker t) {
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

    public Checker getType() {
        return type;
    }

    public void clear() {
        type = Checker.nothing;
        king = false;
    }

    public boolean isEmpty() {
        if (type == Checker.nothing)
            return true;
        else
            return false;
    }
}