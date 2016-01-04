package org.opus.beacon;

public class TimeFormatter {
    private static long minute = 60;
    private static long hour = minute * 60;
    private static long day = hour * 24;
    private static long week = day * 7;
    private static long month = day * 30;
    private static long year = day * 365;

    public static String timeAgo(long timestamp) {
        long now = System.currentTimeMillis() / 1000;
        long diff = now - timestamp;

        if (diff < minute) {
            return "Just now";
        } else if (diff < hour) {
            return minutesAgo(diff);
        } else if (diff < day) {
            return hoursAgo(diff);
        } else if (diff < week) {
            return daysAgo(diff);
        } else if (diff < month) {
            return weeksAgo(diff);
        } else if (diff < year) {
            return monthsAgo(diff);
        } else {
            return yearsAgo(diff);
        }
    }

    private static String periodAgo(long diff, long period) {
        long units = diff / period;
        return Long.toString(units);
    }

    private static String minutesAgo(long diff) {
        return periodAgo(diff, minute) + " min ago";
    }

    private static String hoursAgo(long diff) {
        return periodAgo(diff, hour) + "h ago";
    }

    private static String daysAgo(long diff) {
        return periodAgo(diff, day) + "d ago";
    }

    private static String weeksAgo(long diff) {
        return periodAgo(diff, week) + "w ago";
    }

    private static String monthsAgo(long diff) {
        String months = periodAgo(diff, month);
        if (months.equals("1")) {
            return periodAgo(diff, month) + " month ago";
        }
        return periodAgo(diff, month) + " months ago";
    }

    private static String yearsAgo(long diff) {
        return periodAgo(diff, year) + "y ago";
    }
}
