package dbService;

import utils.TimeHelper;

public class UserDataSet {
    private int id;
    private String nick;

    private int rating;
    private int winQuantity;
    private int loseQuantity;

    private String color;
    private long lastVisit;
    private int postStatus;

    public UserDataSet(int id, String nick, int rating, int winQuantity, int loseQuantity) {
        this.id = id;
        this.rating = rating;
        this.winQuantity = winQuantity;
        this.loseQuantity = loseQuantity;
        this.nick = nick;
        this.lastVisit = TimeHelper.getCurrentTime();
        this.postStatus = 0;
        this.color = null;
    }

    public UserDataSet() {
        id = 0;
        nick = "";

        postStatus = 0;
        lastVisit = TimeHelper.getCurrentTime();
        color = null;
    }

    public void makeLike(UserDataSet userDataSet) {
        id = userDataSet.id;
        nick = userDataSet.nick;

        rating = userDataSet.rating;
        winQuantity = userDataSet.winQuantity;
        loseQuantity = userDataSet.loseQuantity;

        lastVisit = TimeHelper.getCurrentTime();
        postStatus = userDataSet.postStatus;
        color = userDataSet.color;
    }

    public String getNickName() {
        return nick;
    }

    public int getId() {
        return id;
    }

    public void markLatestVisitTime() {
        lastVisit = TimeHelper.getCurrentTime();
    }

    public long getLastVisit() {
        return lastVisit;
    }

    public void setPostStatus(int quantity) {
        postStatus = quantity;
    }

    public int getPostStatus() {
        return postStatus;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public int getRating() {
        return rating;
    }

    public int getWinQuantity() {
        return winQuantity;
    }

    public int getLoseQuantity() {
        return loseQuantity;
    }

    public void lose(int diff) {
        loseQuantity++;
        rating -= diff;
    }

    public void win(int diff) {
        winQuantity++;
        rating += diff;
    }
}