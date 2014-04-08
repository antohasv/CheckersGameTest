package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeHelper {

    public static final String GMT = "GMT";
    public static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss zzz";

    public static void sleep(int TICK_TIME) {
        try {
            Thread.sleep(TICK_TIME);
        } catch (InterruptedException e) {}
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getGMT() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(GMT));
        return dateFormat.format(new Date());
    }

    public static String getTime() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        StringBuilder string = new StringBuilder();
        string.append("'").append(String.valueOf(hours)).append(":");

        if (minutes < 10)
            string.append("0");

        string.append(String.valueOf(minutes)).append(":");

        if (seconds < 10)
            string.append("0");

        string.append(String.valueOf(seconds)).append("'");
        return string.toString();
    }
}