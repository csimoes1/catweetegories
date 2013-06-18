package org.simoes.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

/**
 * 
 * Utility class for manipulating Dates in Java
 * that I have found useful.
 * 
 * @author Chris Simoes
 */
public class DateUtil {
	static Logger log = Logger.getLogger(DateUtil.class);
    
    /**
     * Creates a Timestamp object for us.
     * All values are what are expected.  This method converts month to the zero based
     * format used by java. 
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @param sec
     * @return a Timestamp representing the values passed in
     */
    public static Timestamp createTimestamp(int year, int month, int day, int hour, int min, int sec) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month-1, day, hour, min, sec);
        return new Timestamp(calendar.getTimeInMillis());
    }
    
    public static Timestamp createTimestamp(
        String year,
        String month,
        String day,
        String hour,
        String min,
        String sec) 
    {
        try {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);
            int h = Integer.parseInt(hour);
            int n = Integer.parseInt(min);
            int s = Integer.parseInt(sec);

            Calendar calendar = new GregorianCalendar();
            calendar.set(y, m-1, d, h, n, s);
            return new Timestamp(calendar.getTimeInMillis());
        } catch(Exception e) { // catch even runtime exceptions
            // return null
        }
        return null;
    }

	public static java.sql.Date utilDate2SQLDate(java.util.Date d) {
		java.sql.Date result = null;
		if(null != d) {
			result = new java.sql.Date(d.getTime());
		}
		return result;
	}

	public static java.sql.Timestamp utilDate2Timestamp(java.util.Date d) {
		java.sql.Timestamp result = null;
		if(null != d) {
			result = new java.sql.Timestamp(d.getTime());
		}
		return result;
	}

	/**
	 * Creates a String that represents the current Date and Time.
	 */ 
	public static String createDateString() {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		return df.format(new Date());
	}

	/**
	 * Creates a String that represents the Date passed in.
	 * If the date passed in is null the null is returned.
	 */ 
	public static String createDateString(Date d) {
		String result = null;
		if(null != d) {
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			result = df.format(d);
		}
		return result;
	}

    /**
     * Creates a String that represents the Date passed in.
     * If the date passed in is null the null is returned.
     */ 
    public static String createMySQLDateString(long date) {
        return createMySQLDateString(new Date(date));
    }
    
    public static String createMySQLDateString(Date date) {
        String result = null;
        if(null != date) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = df.format(date);
        }
        return result;
    }

    /**
     * Creates a filename based on the long passed in.
     * If the long is less than 0 then null is returned.
     */ 
    public static String createDateFileName(long time) {
        String result = null;
        if(time >= 0) {
            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
            result = df.format(new Date(time));
        }
        return result;
    }

    /**
	 * Creates a String formatted properly based on the Date passed in.
	 * Is expecting a string of the following form: "year-month-day hour:minute:second"
	 * This is the date format returned from MSSQL server via JDBC
	 * Note: hour is from 0-23
	 * If the date passed in is null or a format not supported then null is returned.
	 */ 
	public static String createDateString(String d) {
		final String METHOD_NAME = "createDateString(): ";
		String result = null;
		if(!Strings.isNullOrEmpty(d)) {
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date myDate = df.parse(d);
                result = createDateString(myDate);
			} catch(ParseException e) {
				log.error(METHOD_NAME + "String = " + d+ ", was not a valid date.");
			}
		}
		return result;
	}

	/**
	 * Creates a string representation of the Date passed in using the format
	 * passed in.  Example format string: MM/dd/yyyy 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String createDateString(Date d, String format) {
		String result = null;
		if(null != d) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			result = df.format(d);
		}
		return result;
	}
	
    /**
     * Creates a Calendar based on the Date passed in.
     * Is expecting a string of the following form: "year-month-day hour:minute:second"
     * This is the date format returned from MSSQL server via JDBC
     * Note: hour is from 0-23
     * Alternative format accepted: "month/day/year"
     * If the date passed in is null or a format not supported then null is returned.
     */ 
    public static Calendar parseDate(String d) {
        final String METHOD_NAME = "parseDate(): ";
        Calendar result = null;
        if(!Strings.isNullOrEmpty(d)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat df3 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            SimpleDateFormat df4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            SimpleDateFormat df5 = new SimpleDateFormat("yyyyMMdd");
            Date myDate = df.parse(d, new ParsePosition(0));
            if(null == myDate) {
                myDate = df2.parse(d, new ParsePosition(0));
            }
            if(null == myDate) {
                myDate = df3.parse(d, new ParsePosition(0));
            }
            if(null == myDate) {
                myDate = df4.parse(d, new ParsePosition(0));
            }
            if(null == myDate) {
                myDate = df5.parse(d, new ParsePosition(0));
            }
            
            if(null != myDate) {
                result = Calendar.getInstance();
                result.setTime(myDate);
            }
        }
        return result;
    }

    /**
     * Parses the date using the pattern passed in such as: MMddyyyy
     * @param d
     * @param format
     * @return
     */
    public static Calendar parseDate(String d, String format) {
        final String METHOD_NAME = "parseDate(): ";
        Calendar result = null;
        if(!Strings.isNullOrEmpty(d) && !Strings.isNullOrEmpty(format)) {
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date myDate = df.parse(d, new ParsePosition(0));
            
            if(null != myDate) {
                result = Calendar.getInstance();
                result.setTime(myDate);
            }
        }
        return result;
    }
    
    /**
	 * Creates a String that represents the Date passed in, using the format
	 * passed in.  For formatting options check out the SimpleDateFormat object.
	 * If the date passed in is null the null is returned.
	 */ 
	public static String createDateOnlyString(Date d) {
		String result = null;
		if(null != d) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			result = df.format(d);
		}
		return result;
	}

    public static String createTimeOnlyString(Date d) {
        String result = null;
        if(null != d) {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            result = df.format(d);
        }
        return result;
    }

    /**
	 * Creates a String that represents the Date passed in.
	 * Formats the Date so that it can be used as a filename.  
	 * For formatting options check out the SimpleDateFormat object.
	 * If the date passed in is null the null is returned.
	 */ 
	public static String createDateOnlyFilename(Date d) {
		String result = null;
		if(null != d) {
			SimpleDateFormat df = new SimpleDateFormat("MM_dd_yyyy");
			result = df.format(d);
		}
		return result;
	}

	public static Calendar createCalendar(java.util.Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * Returns a true or false based on
	 * the whether or not the date is valid.
	 */
	public static boolean checkDate(int day, int month, int year) {
		int[] numDays = {31,28,31,30,31,30,31,31,30,31,30,31};

		//if february
		if(month == 1) {
			// if leap year
			if((((year % 4) == 0) && ((year % 100) != 0))
			   || (((year % 100) == 0) && ((year % 400) == 0))) {
				return(day <= 29);
			} else {
				return(day <= 28);
			}
		} else {
			// if day is greater than possible days of the month passed
			return(day <= numDays[month]);
		}
	} // END checkDate
    
    /**
     * Returns true if the time passed in  past the hour,min, and sec passed in.
     * @param time the point in time (and the day) we will be comparing against
     * @param hour
     * @param min
     * @param sec
     * @return
     */
    public static boolean isPastTime(long time, int hour, int min, int sec) {
        boolean result = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        // if hour is greater we are done
        if(hour > calendar.get(Calendar.HOUR_OF_DAY)) {
            result = false;
        } else if(hour == calendar.get(Calendar.HOUR_OF_DAY)) {
            // if minute is greater we are done
            if(min > calendar.get(Calendar.MINUTE)) {
                result = false;
            } else if(min == calendar.get(Calendar.MINUTE)) {
                // if second is greater we are done
                if(sec > calendar.get(Calendar.SECOND)) {
                    result = false;
                }                
            }
        }        
        return result;
    }

    public static boolean isPastTime(Timestamp time, int hour, int min, int sec) {
        final String METHOD_NAME = "isPastTime(): "; 
        if(null == time) {
            throw new IllegalArgumentException(METHOD_NAME + "Timestamp passed in was null");
        }
        return DateUtil.isPastTime(time.getTime(), hour, min, sec);
    }
    
    public static boolean isSameDay(Date d1, Date d2) {
        final String METHOD_NAME = "isSameDay(): ";
        boolean result = false;
        if(null != d1 && null != d2) {
            Calendar cal1 = DateUtil.createCalendar(d1);
            Calendar cal2 = DateUtil.createCalendar(d2);
            if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) 
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
            {
            	result = true;
            }
        }
        return result;
    }
    
}
