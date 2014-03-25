package dbService;

import utils.TimeHelper;

public class UserDataSet {
    private int id;
    private int rating, winQuantity, loseQuantity;
    private String nick;
    private long lastVisit;
    private int postStatus;
    private String color;

    public UserDataSet(int id, String nick, int rating, int winQuantity, int loseQuantity) {
        this.id = id;
        this.rating = rating;
        this.winQuantity = winQuantity;
        this.loseQuantity = loseQuantity;
        this.nick = nick;
        lastVisit = TimeHelper.getCurrentTime();
        postStatus = 0;
        color = null;
    }

    public UserDataSet() {
        id = 0;
        postStatus = 0;
        lastVisit = TimeHelper.getCurrentTime();
        nick = "";
        color = null;
    }

    public void makeLike(UserDataSet userDataSet) {
        this.id = userDataSet.id;
        this.rating = userDataSet.rating;
        this.winQuantity = userDataSet.winQuantity;
        this.loseQuantity = userDataSet.loseQuantity;
        this.nick = userDataSet.nick;
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