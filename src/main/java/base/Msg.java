package base;


public abstract class Msg {
    final private Address from, to;

    public Msg(Address from, Address to) {
        this.from = from;
        this.to = to;
    }

    protected Address getFrom() {
        return from;
    }

    protected Address getTo() {
        return to;
    }

    public abstract boolean exec(Abonent abonent);
}