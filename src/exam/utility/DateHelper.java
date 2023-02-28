package exam.utility;

import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.stream.Collectors;

public class DateHelper {
    /**
     * @return Timestamp created from current system time.
     */
    public static Timestamp localNow() {
        return Timestamp.from(Instant.now());
    }

    /**
     * @param date LocalDate object to parse to a timestamp in UTC
     * @param time LocalTime object to parse to a timestamp in UTC
     * @return
     */
    public static Timestamp localToUTC(LocalDate date, LocalTime time) {
        var zdt = ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant().atOffset(ZoneOffset.UTC).toInstant();
        return Timestamp.from(zdt);
    }

    /**
     * @param i
     * @return
     */
    public static java.sql.Timestamp utctoLocal(Instant i) {
        var timeZone = Calendar.getInstance().getTimeZone().getID();
        return Timestamp.from(i.atZone(ZoneId.of(timeZone)).toInstant());
    }

    public static Timestamp utcToEST(Instant i) {
        return Timestamp.from(i.atZone(ZoneId.of("America/New_York")).toInstant());
    }

    public static ZonedDateTime timeToEST(LocalTime t) {
        var zonedStart = ZonedDateTime.of(t.atDate(LocalDate.of(1970,1,1)), ZoneId.systemDefault());
        return zonedStart.withZoneSameInstant(ZoneId.of("America/New_York"));
    }

    /**
     * This method takes a start and end LocalTime to determine if the times are between 8AM and 10PM EST.
     * @param start
     * @param end
     * @return Boolean result if the time is between operating hours
     */
    public static boolean isBetweenBusinessHoursEST(LocalTime start, LocalTime end) {
        var zid = ZoneId.of("America/New_York");
        var zonedStart = ZonedDateTime.of(start.atDate(LocalDate.of(1970,1,1)), ZoneId.systemDefault());
        var estStart = ZonedDateTime.of(LocalTime.of(8,0).atDate(LocalDate.of(1970,1,1)), zid);
        var zonedEnd = ZonedDateTime.of(end.atDate(LocalDate.of(1970,1,1)), ZoneId.systemDefault());
        var estEnd = ZonedDateTime.of(LocalTime.of(22,0).atDate(LocalDate.of(1970,1,1)), zid);
        return (zonedStart.isBefore(estEnd) && zonedStart.isAfter(estStart)) && (zonedEnd.isBefore(estEnd) && zonedEnd.isAfter(estStart));
    }

    /**
     * @param d LocalDate to compare against
     * @return Boolean value if the parameter is a weekday
     */
    public static boolean isWeekday(LocalDate d) {
        return !(d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    /**
     * @param start
     * @param end
     * @return boolean value determined by if the start and end time fall within a weekend
     */
    public static boolean isNotDuringWeekend(LocalDate start, LocalDate end) {
        if(isWeekday(start) && isWeekday(end)){
           for(var d : start.datesUntil(end).collect(Collectors.toList())) {
               if(!isWeekday(d)){ return false; }
           }
            return true;
        }
        return false;
    }
}
