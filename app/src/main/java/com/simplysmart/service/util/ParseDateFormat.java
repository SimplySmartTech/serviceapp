package com.simplysmart.service.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shekhar on 10/8/15.
 */
public class ParseDateFormat {

    public static String changeDateFormat(String dateInput) throws ParseException {

        Date dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).parse(dateInput);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat requiredDateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        DateFormat requiredDateFormatWithDay = new SimpleDateFormat("EEE hh:mm aa", Locale.US);

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return requiredDateFormat.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday " + requiredDateFormat.format(dateTime);
        } else {
            return requiredDateFormatWithDay.format(dateTime);
        }
    }

    public static String getMonthFromDate(String dateInput) throws ParseException {
        Date dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).parse(dateInput);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat requiredDateFormat = new SimpleDateFormat("MMM", Locale.US);

        return requiredDateFormat.format(dateTime);
    }

    public static String getMonthDate(String dateInput) throws ParseException {
        Date dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).parse(dateInput);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat requiredDateFormat = new SimpleDateFormat("dd", Locale.US);

        return requiredDateFormat.format(dateTime);
    }

    public static String getLongDate(String dateInput) throws ParseException {
        Date dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(dateInput);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat requiredDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return requiredDateFormat.format(dateTime);
    }

    public static String getDateFromTimestamp(String timeStampStr, String outputFormat) {
        try {
            DateFormat sdf = new SimpleDateFormat(outputFormat, Locale.getDefault());
            Date netDate = (new Date(Long.parseLong(timeStampStr) * 1000L));
            return sdf.format(netDate);
        } catch (Exception ignored) {
            return "xx";
        }
    }

    public static String getTimeFromTimestamp(String timeStampStr, String outputFormat) {
        try {
            DateFormat sdf = new SimpleDateFormat(outputFormat, Locale.getDefault());
            Date netDate = (new Date(Long.parseLong(timeStampStr)));
            return sdf.format(netDate);
        } catch (Exception ignored) {
            return "xx";
        }
    }

    public static String getCurrentTimeStamp(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    public static String getYesterdayDateString(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }
}
