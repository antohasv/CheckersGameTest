package resource;

public class TimeSettings implements Resource {
    private static int exitTime;

    public static void setExitTime(int exitTime) {
        TimeSettings.exitTime = exitTime;
    }

    public static int getExitTime() {
        return exitTime;
    }

}
