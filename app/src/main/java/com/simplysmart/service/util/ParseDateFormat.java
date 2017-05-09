package com.simplysmart.service.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String dateFormat(Context context, String dateInput) throws ParseException {

        String timezoneName = TimeZone.getDefault().getDisplayName();

        SimpleDateFormat timeZoneSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        timeZoneSdf.setTimeZone(TimeZone.getTimeZone(timezoneName));

        Date dateTime = timeZoneSdf.parse(dateInput);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);

        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar overWeek = Calendar.getInstance();

        yesterday.add(Calendar.DATE, -1);
        overWeek.add(Calendar.DATE, -7);

        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);

        DateFormat requiredDateFormat_12_hours = new SimpleDateFormat("hh:mm aa", Locale.US);
        DateFormat requiredDateFormat_24_hours = new SimpleDateFormat("HH:mm", Locale.US);
        DateFormat requiredDateFormatWithDay = new SimpleDateFormat("EEEE", Locale.US);
        DateFormat requiredDateFormatOverWeek = new SimpleDateFormat("dd/MM/yy", Locale.US);

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            if (is24HourFormat) {
                return requiredDateFormat_24_hours.format(dateTime);
            } else {
                return requiredDateFormat_12_hours.format(dateTime);
            }
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday ";
        } else if (calendar.get(Calendar.YEAR) == overWeek.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) >= overWeek.get(Calendar.DAY_OF_YEAR)) {
            return requiredDateFormatWithDay.format(dateTime);
        } else {
            return requiredDateFormatOverWeek.format(dateTime);
        }
    }
}
