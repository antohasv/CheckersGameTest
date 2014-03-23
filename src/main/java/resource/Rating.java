package resource;

import java.io.Serializable;

public class Rating implements Serializable, Resource {
    private static final long serialVersionUID = -7240573567084382721L;
    static int maxDiff;
    static int decreaseThreshold;
    static int avgDiff;
    static int minDiff;

    public static int getAvgDiff() {
        return avgDiff;
    }

    public static int getDiff(int winRating, int loseRating) {
        if (decreaseThreshold == 0) {
            System.err.println("Ratings decreaseThreshold is 0!");
            System.err.println("You must reload this resource, now diffRating is avg");
            return avgDiff;
        } else {
            return Math.min(maxDiff,
                    Math.max(minDiff,
                            avgDiff - (winRating - loseRating) / decreaseThreshold));
        }
    }
}
