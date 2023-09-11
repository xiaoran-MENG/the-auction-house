package comp3350.group9.theauctionhouse.business.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Using Date with days, months, years is deprecated in our current Java version. Instead you must use Calendar, which can be converted to Date.<br/>
 * This results in many additional lines of code to instantiate Date for simple use cases.<br/>
 * This class is simply a wrapper for simple use case of Date and Calendar (default timezone and locale).
 * Should ideally be using LocaleDate library, but too late now.
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Date.html">Date documention</a>
 */
public class DateTime {
    public enum Months {
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER,
        DECEMBER
    }

    private Date date;

    private DateTime(int year, Months month, int day, int hour, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month.ordinal()); // Enum Calendar.JANUARY starts at 0 in docs
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        date = cal.getTime();
    }

    private DateTime(int year, Months month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month.ordinal()); // Enum Calendar.JANUARY starts at 0 in docs
        cal.set(Calendar.DAY_OF_MONTH, day);
        date = cal.getTime();
    }

    public boolean beforeOrAt(DateTime other) {
        return compare(other) <= 0;
    }

    public int compare(DateTime other) {
        Calendar cal1 = Calendar.getInstance(); Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.date); cal2.setTime(other.getDate());

        // Don't compare on seconds and milliseconds
        if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                && cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
                && cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE)) {
            return 0;
        }else{
            return this.date.compareTo(other.getDate());
        }
    }

    public DateTime addDays(int days){
        Calendar c = Calendar.getInstance();
        c.setTime(this.date);
        c.add(Calendar.DATE, days);
        this.date = c.getTime();
        return DateTime.of(c.getTime());
    }

    public DateTime addDate(DateTime otherDt){
        Calendar c = Calendar.getInstance();
        Calendar o = Calendar.getInstance();

        Date other =otherDt.date;
        c.setTime(this.date);
        o.setTime(other);

        c.add(Calendar.YEAR, o.get(Calendar.YEAR));
        c.add(Calendar.DATE, o.get(Calendar.DAY_OF_YEAR));
        c.add(Calendar.HOUR_OF_DAY, o.get(Calendar.HOUR_OF_DAY));

        this.date = c.getTime();
        return DateTime.of(c.getTime());
    }

    public Date getDate(){
        return this.date;
    }
    public int getYear() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.date);
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.date);
        return c.get(Calendar.MONTH);
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public String toString(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.date);
        Date dateTime = cal.getTime();
        return df.format(dateTime);
    }

    public String toString(String format){
        DateFormat df = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getDate());
        Date dateTime = cal.getTime();
        return df.format(dateTime);
    }

    public static DateTime now(){
        return DateTime.of(new Date());
    }

    public static DateTime of(int year, Months month, int day, int hour, int min) {
        return new DateTime(year, month, day, hour, min);
    }

    public static DateTime of(int year, Months month, int day) {
        return new DateTime(year, month, day);
    }

    public static DateTime of(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Months month = Months.values()[c.get(Calendar.MONTH)];
        return new DateTime(c.get(Calendar.YEAR),month,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
    }

    public static DateTime of(String dateString, String pattern, DateTime defaultReturn){
        try {
            Date date = new SimpleDateFormat(pattern).parse(dateString);
            return DateTime.of(date);
        } catch (ParseException e) {
            return defaultReturn;
        }

    }
}
