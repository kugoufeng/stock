package cn.jeremy.stock.tools;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日期处理工具类
 *
 * @author kugoufeng
 * @date 2017/12/21 上午 11:46
 */
public class DateTools
{
    /**
     * 日志信息
     */
    private static Logger LOGGER = LogManager.getLogger(DateTools.class.getName());

    /**
     * Automatically generated javadoc for: ONE_MINUTE
     */
    private static final int ONE_MINUTE = 60;

    /**
     * Automatically generated javadoc for: ONE_THOUSAND
     */
    private static final int ONE_THOUSAND = 1000;

    /**
     * Automatically generated javadoc for: ONE_HOUR
     */
    private static final int ONE_HOUR = 60;

    /**
     * 默认时间
     */
    private static final FastDateFormat LOCAL_DATEFORMAT =
        FastDateFormat.getInstance("yyyyMMddHHmmss", TimeZone.getDefault());

    /**
     * 时间格式 : yyyy/MM/dd HH:mm
     */
    public static final String PATTERN = "yyyy/MM/dd HH:mm";

    /**
     * 时间格式 : yyyyMMddHHmm
     */
    public static final String DATE_FORMAT_12 = "yyyyMMddHHmm";

    /**
     * 时间格式 : yyyyMMddHHmmss
     */
    public static final String DATE_FORMAT_14 = "yyyyMMddHHmmss";

    /**
     * 时间格式 : yyyy-MM-dd hh:mm:ss.0
     */
    public static final String DATE_FORMAT_21_0 = "yyyy-MM-dd HH:mm:ss.0";

    /**
     * 时间格式 : yyyyMM
     */
    public static final String DATE_FORMAT_6 = "yyyyMM";

    /**
     * 时间格式 : yyyy-MM
     */
    public static final String DATE_FORMAT_7 = "yyyy-MM";

    /**
     * 时间格式 : yyyyMMdd
     */
    public static final String DATE_FORMAT2_8 = "yyyyMMdd";

    /**
     * 时间格式 : yy.MM.dd
     */
    public static final String DATE_FORMAT_8 = "yy.MM.dd";

    /**
     * 时间格式 : yy.MM.dd
     */
    public static final String DATE_FORMAT_16_DOT = "yyyy.MM.dd hh:mm";

    /**
     * 时间格式 : [yyyy-MM-dd hh:mm:ss]
     */
    public static final String DATE_FORMAT_21 = "[yyyy-MM-dd hh:mm:ss]";

    /**
     * 时间格式 : [yyyy-MM-dd HH:mm:ss]
     */
    public static final String DATE_FORMAT_24HOUR_21 = "[yyyy-MM-dd HH:mm:ss]";

    /**
     * 时间格式 ：yyyy-MM-dd hh:mm
     */
    public static final String DATE_FORMAT_16 = "yyyy-MM-dd hh:mm";

    /**
     * 时间格式 : yy-MM-dd hh:mm
     */
    public static final String DATE_PATTERN_14 = "yy-MM-dd hh:mm";

    /**
     * 时间格式 : yy-MM-dd hh:mm
     */
    public static final String DATE_FORMAT_24HOUR_16 = "yyyy-MM-dd HH:mm";

    /**
     * 时间格式 : yy-MM-dd HH:mm
     */
    public static final String DATE_PATTERN_24HOUR_14 = "yy-MM-dd HH:mm";

    /**
     * 时间格式 ：yyyy-MM-dd
     */
    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";

    /**
     * 时间格式 ：yyyy年MM月
     */
    public static final String DATE_FORMAT_18 = "yyyy年MM月";

    /**
     * 时间格式 ：HH:mm
     */
    public static final String DATE_FORMAT_19 = "HH:mm";

    /**
     * 时间格式 ：MM-dd
     */
    public static final String DATE_FORMAT_20 = "MM-dd";

    /**
     * 时间格式 ：yyyy
     */
    public static final String DATE_FORMAT_YEAR = "yyyy";

    /**
     * 时间格式 ：MM
     */
    public static final String DATE_FORMAT_MONTH = "MM";

    /**
     * 时间格式 ：dd
     */
    public static final String DATE_FORMAT_DAY = "dd";

    /**
     * 时间格式 ：yyyy/MM/dd HH:mm:SS
     */
    public static final String DATE_FORMAT_30 = "yyyy/MM/dd HH:mm:SS";

    /**
     * 时间格式 : yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_24HOUR_19 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式 : MM-dd hh:mm
     */
    public static final String DATE_PATTERN_8 = "MM-dd hh:mm";

    /**
     * 时间格式：MM-dd HH:mm
     */
    public static final String DATE_PATTERN_10 = "MM-dd HH:mm";

    /**
     * 时间格式：HH:mm:ss
     */
    public static final String DATE_PATTERN_11 = "HH:mm:ss";

    /**
     * 时间格式 : yyyyMMddHHmmss
     */
    public static final String DATE_PATTERN_24HOUR_16 = "yyyyMMddHHmmss";

    /**
     * 时间格式化格式
     */
    public static final String DATA_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 时间格式化格式.sss
     */
    public static final String DATA_FORMAT_11 = "yyMMddHHmmss.SSS";

    /**
     * yyyy-MM-dd HH:mm:ss格式化对象
     */
    private static final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT_24HOUR_19);

    /**
     * 格式化语言
     */
    private static final Locale DEFAULT_LOCALE = new Locale("EN");

    /**
     * yyyyMMddHHmmss格式化对象
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_24HOUR_16, DEFAULT_LOCALE);

    /**
     * 把不同FormatPattern的对象缓存到本地线程变量里
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> dateFormatCache =
        new ThreadLocal<Map<String, SimpleDateFormat>>();

    public static final int YEAR = 0;

    public static final int MONTH = 1;

    public static final int WEEK = 2;

    public static final int DAY = 3;

    public static final int HOUR = 4;

    public static final int MINUTE = 5;

    public static final int SECOND = 6;

    public static final int MILLISECOND = 7;

    public static final int MINUTEOFDAY = 8;

    public static enum CompareDateFormate
    {
        year, month, day, hour, minute, second,

        yyyyMMddhhmmss, yyyyMMddhhmm, yyyyMMddhh, yyyyMMdd, yyyyMM,

        MMddhhmmss, MMddhhmm, MMddhh, MMdd, ddhhmmss, ddhhmm, ddhh, hhmmss, hhmm, mmss
    }

    private final static HashMap<CompareDateFormate, int[]> map = new HashMap<CompareDateFormate, int[]>();

    static
    {
        map.put(CompareDateFormate.year, new int[] {Calendar.YEAR});
        map.put(CompareDateFormate.month, new int[] {Calendar.MONTH});
        map.put(CompareDateFormate.day, new int[] {Calendar.DATE});
        map.put(CompareDateFormate.hour, new int[] {Calendar.HOUR_OF_DAY});
        map.put(CompareDateFormate.minute, new int[] {Calendar.MINUTE});
        map.put(CompareDateFormate.second, new int[] {Calendar.SECOND});

        map.put(CompareDateFormate.yyyyMMddhhmmss,
            new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
                Calendar.SECOND});
        map.put(CompareDateFormate.yyyyMMddhhmm,
            new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE});
        map.put(CompareDateFormate.yyyyMMddhh,
            new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY});
        map.put(CompareDateFormate.yyyyMMdd, new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DATE});
        map.put(CompareDateFormate.yyyyMM, new int[] {Calendar.YEAR, Calendar.MONTH});

        map.put(CompareDateFormate.MMddhhmmss,
            new int[] {Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND});
        map.put(CompareDateFormate.MMddhhmm,
            new int[] {Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE});
        map.put(CompareDateFormate.MMddhh, new int[] {Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY});
        map.put(CompareDateFormate.MMdd, new int[] {Calendar.MONTH, Calendar.DATE});

        map.put(CompareDateFormate.ddhhmmss,
            new int[] {Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND});
        map.put(CompareDateFormate.ddhhmm, new int[] {Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE});
        map.put(CompareDateFormate.ddhh, new int[] {Calendar.DATE, Calendar.HOUR_OF_DAY});

        map.put(CompareDateFormate.hhmmss, new int[] {Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND});
        map.put(CompareDateFormate.hhmm, new int[] {Calendar.HOUR_OF_DAY, Calendar.MINUTE});
        map.put(CompareDateFormate.mmss, new int[] {Calendar.MINUTE, Calendar.SECOND});
    }


    /**
     * 获取线程里的变量map
     *
     * @return java.util.Map<java.lang.String,java.text.SimpleDateFormat>
     */
    private static Map<String, SimpleDateFormat> getSimpleDateFormatMap()
    {
        Map<String, SimpleDateFormat> simpleDateFormatMap = dateFormatCache.get();
        if (simpleDateFormatMap == null)
        {
            simpleDateFormatMap = new HashMap<String, SimpleDateFormat>(16);
            dateFormatCache.set(simpleDateFormatMap);
        }
        return simpleDateFormatMap;
    }

    /**
     * 获取一个SimpleDateFormat对象
     *
     * @param datePattern 格式化样式
     * @param currentLocale 环境语言
     * @return java.text.SimpleDateFormat
     */
    private static SimpleDateFormat getSimpleDateFormatFormCache(String datePattern, Locale currentLocale)
    {
        Map<String, SimpleDateFormat> simpleDateFormatMap = getSimpleDateFormatMap();

        SimpleDateFormat sf = simpleDateFormatMap.get(datePattern);
        if (sf != null)
        {
            return sf;
        }
        else
        {
            if (Util.isEmpty(datePattern))
            {
                sf = new SimpleDateFormat();
            }
            else
            {
                sf = new SimpleDateFormat(datePattern, currentLocale == null ? DEFAULT_LOCALE : currentLocale);
            }

            simpleDateFormatMap.put(datePattern, sf);
            return sf;
        }
    }

    /**
     * 获取一个SimpleDateFormat对象，注意，获取到的对象非线程安全，不要在多线程内传递该对象的引用
     *
     * @param datePattern 格式化样式
     * @return java.text.SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String datePattern)
    {
        return getSimpleDateFormatFormCache(datePattern, DEFAULT_LOCALE);
    }

    /**
     * 获取一个SimpleDateFormat对象，注意，获取到的对象非线程安全，不要在多线程内传递该对象的引用
     *
     * @return java.text.SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat()
    {
        return getSimpleDateFormatFormCache(null, DEFAULT_LOCALE);
    }

    /**
     * 获取一个SimpleDateFormat对象，注意，获取到的对象非线程安全，不要在多线程内传递该对象的引用
     *
     * @param datePattern 格式化样式
     * @param currentLocale 环境语言
     * @return java.text.SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String datePattern, Locale currentLocale)
    {
        return getSimpleDateFormatFormCache(datePattern, currentLocale);
    }

    /**
     * 在Locale下转换时间的格式为指定格式
     *
     * @param time 要格式化的时间
     * @param oldPattern 时间的格式
     * @param dateTimeStyle 要格式化的格式
     * @param currentLocale 语言
     * @return java.lang.String
     */
    public static String getTimeByLocale(String time, String oldPattern, int dateTimeStyle, Locale currentLocale)
    {
        String oldTime;

        // 参数有效性检查
        if (null == oldPattern)
        {
            throw new IllegalArgumentException("the old pattern is null");
        }

        // 检查传入时间和格式是否一致
        SimpleDateFormat oldDatePattern = getSimpleDateFormat(oldPattern, currentLocale);
        Date now;
        try
        {
            now = oldDatePattern.parse(time);

            // 用原来的pattern解析日期，再和原来的比较，由此来检查时间是否合法
            oldTime = oldDatePattern.format(now);
            if (!oldTime.equals(time))
            {
                throw new IllegalArgumentException("using Illegal time");
            }
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("using [" + oldPattern + "] parse [" + time + "] failed");
        }

        // 格式转换
        DateFormat newDatePattern = DateFormat.getDateTimeInstance(dateTimeStyle, dateTimeStyle, currentLocale);
        return newDatePattern.format(now);
    }

    /**
     * 按要求转化时间的显示格式 参数：oldpattern，旧日期格式，如:yyyyMMddhhmmss 格式描述符的含义参见JDK simpleDateFormat类 newpattern，新日期格式
     *
     * @param time 要转换的时间
     * @param oldPattern 旧的的时间格式
     * @param newPattern 新的时间格式
     * @return java.lang.String
     */
    public static String timeTransform(String time, String oldPattern, String newPattern)
    {
        String oldTime;
        if (Util.isEmpty(time) || null == oldPattern || null == newPattern)
        {
            return time;
        }

        SimpleDateFormat oldDatePattern = getSimpleDateFormat(oldPattern, DEFAULT_LOCALE);
        Date date;
        try
        {
            date = oldDatePattern.parse(time);

            // 用原来的pattern解析日期，再和原来的比较，由此来检查时间是否合法
            oldTime = oldDatePattern.format(date);
            if (!oldTime.equals(time))
            {
                return time;
            }
        }
        catch (ParseException e)
        {
            return time;
        }
        SimpleDateFormat newDatePattern = getSimpleDateFormat(newPattern, DEFAULT_LOCALE);

        return newDatePattern.format(date);
    }

    /**
     * 将时间转换为指定的格式(根据时间的长度，确定时间的旧格式)
     *
     * @param time 要转换的时间
     * @param newPattern 要转换的时间格式
     * @return java.lang.String
     */
    public static String timeTransform(String time, String newPattern)
    {
        if (Util.isEmpty(time) || null == newPattern)
        {
            return time;
        }
        time = time.trim();

        if (time.length() == DATE_FORMAT2_8.length())
        {
            return timeTransform(time, DATE_FORMAT2_8, newPattern);
        }
        else if (time.length() == DATE_FORMAT_10.length())
        {
            return timeTransform(time, DATE_FORMAT_10, newPattern);
        }
        else if (time.length() == DATE_FORMAT_14.length())
        {
            return timeTransform(time, DATE_FORMAT_14, newPattern);
        }
        else if (time.length() == DATE_FORMAT_24HOUR_19.length())
        {
            return timeTransform(time, DATE_FORMAT_24HOUR_19, newPattern);
        }
        else if (time.length() == DATE_FORMAT_21_0.length())
        {
            return timeTransform(time, DATE_FORMAT_21_0, newPattern);
        }

        return time;
    }

    /**
     * 获取指定格式的当前日期 参数：pattern，日期格式，如:yyyyMMddhhmmss 格式描述符的含义参见JDK simpleDateFormat类
     *
     * @param pattern 指定的格式
     * @return java.lang.String
     */
    public static String getCurrentDate(String pattern)
    {
        if (null == pattern)
        {
            throw new IllegalArgumentException("input string parameter is null");
        }
        SimpleDateFormat sdf = getSimpleDateFormat(pattern, DEFAULT_LOCALE);
        Date now = new Date();
        return sdf.format(now);
    }

    /**
     * 获取java.sql.Date类型的当前日期 返回：java.sql.Date 如2005-10-19
     *
     * @return java.sql.Date
     */
    public static java.sql.Date getCurrentDate()
    {
        Calendar cal = Calendar.getInstance();
        return new java.sql.Date(cal.getTimeInMillis());
    }

    /**
     * 根据时间格式,获取当前时间戳
     *
     * @param patten 当前时间戳
     * @return java.lang.String
     */
    public static String getCurrentTime(String patten)
    {
        Date date = new Date();
        try
        {
            return getSimpleDateFormat(patten, new Locale("EN")).format(date);
        }
        catch (Exception e)
        {
            // 之前版本残留的默认格式,留着
            return sdf.format(date);
        }
    }

    /**
     * 获取java.sql.Timestamp类型的当前日期。保护年月日时分秒 返回：java.sql.Timestamp 如2005-10-19 18:20:07
     *
     * @return java.sql.Timestamp
     */
    public static Timestamp getCurrentTimestamp()
    {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        return t;
    }

    /**
     * 返回当前时间的毫秒值
     *
     * @return long
     */
    public static long getCurrentTimeLong()
    {
        return System.currentTimeMillis();
    }

    /**
     * 将日期长整型转换成字符串 参数：time，long，从格林威治时间：1970年1月1日0点起的毫秒数 pattern, String, 转换的目标格式
     *
     * @param time 日期长整型
     * @param pattern 时间格式
     * @return java.lang.String
     */
    public static String long2TimeStr(long time, String pattern)
    {
        if (null == pattern)
        {
            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        Date dt = new Date(time);
        SimpleDateFormat sdf = getSimpleDateFormat(pattern, new Locale("EN"));
        return sdf.format(dt);
    }

    /**
     * 将日期型转换成字符串 参数：time，Date pattern, String, 转换的目标格式
     *
     * @param time 日期对象
     * @param pattern 转换的时间格式
     * @return java.lang.String
     */
    public static String date2TimeStr(Date time, String pattern)
    {
        if (null == pattern)
        {
            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        if (null == time)
        {
            throw new IllegalArgumentException("time parameter can not be null");
        }
        SimpleDateFormat sdf = getSimpleDateFormat(pattern, new Locale("EN"));
        return sdf.format(time);
    }

    /**
     * 将日期增加一个增量，目前只能是，年，月，周，日，时、分、秒、毫秒 参数：date, long，原始时间 delta，int，
     * 增量的大小 unit, int, 增量的单位，YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND
     * 返回：long，从格林威治时间：1970年1月1日0点起的毫秒数
     *
     * @param date 长整形日期
     * @param delta 增量的值
     * @param unit 增量的类型
     * @return long
     */
    public static long addDate(long date, int delta, int unit)
    {
        if ((unit < YEAR) || (unit > MILLISECOND))
        {
            throw new IllegalArgumentException(
                "time unit must in [YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND], others not support");
        }
        Date dt = new Date(date);
        Calendar calendar = getLocalCalendar(dt);

        // 增加增量
        switch (unit)
        {
            case YEAR:
                calendar.add(Calendar.YEAR, delta);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, delta);
                break;
            case WEEK:
                calendar.add(Calendar.DAY_OF_WEEK, delta);
                break;
            case DAY:
                calendar.add(Calendar.DAY_OF_MONTH, delta);
                break;
            case HOUR:
                calendar.add(Calendar.HOUR, delta);
                break;
            case MINUTE:
                calendar.add(Calendar.MINUTE, delta);
                break;
            case SECOND:
                calendar.add(Calendar.SECOND, delta);
                break;
            case MILLISECOND:
                calendar.add(Calendar.MILLISECOND, delta);
                /* falls through */
            default:
                break;
        }

        // 获取新的时间，并转换成长整形
        Date ndt = calendar.getTime();
        return ndt.getTime();
    }

    /**
     * 将日期增加一个增量，目前只能是，年，月，周，日，时，分，秒，毫秒 参数：date, long，原始时间 delta，int，
     * 增量的大小 unit, int, 增量的单位，YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND
     * pattern, String, 转换的目标格式 返回：String，指定格式的日期字符串
     *
     * @param date 长整形日期
     * @param delta 增量值
     * @param unit 增量单位
     * @param pattern 转换的目标格式
     * @return java.lang.String
     */
    public static String addDate(long date, int delta, int unit, String pattern)
    {
        if (null == pattern)
        {
            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        return long2TimeStr(addDate(date, delta, unit), pattern);
    }

    /**
     * 将字符串转换成日期长整形 参数：time，String，日期字符串 pattern, String, 解析的格式 返回：long，日期长整形
     *
     * @param time 日期形字符串
     * @param pattern 日期字符串格式
     * @return long
     */
    public static long timeStr2Long(String time, String pattern)
    {
        return timeStr2Date(time, pattern).getTime();
    }

    /**
     * 将字符串转换成日期形 参数：time，String，日期字符串 pattern, String, 解析的格式 返回：Date，日期形
     *
     * @param time 日期形字符串
     * @param pattern 日期形字符串格式
     * @return java.util.Date
     */
    public static Date timeStr2Date(String time, String pattern)
    {
        if (null == time)
        {
            throw new IllegalArgumentException("time parameter can not be null");
        }
        if (null == pattern)
        {
            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        SimpleDateFormat sdf = getSimpleDateFormat(pattern, new Locale("EN"));
        try
        {
            return sdf.parse(time);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("using [" + pattern + "] parse [" + time + "] failed");
        }
    }

    /**
     * 获取日期字符串的某一部分 参数：date，有效的日期字符串 pattern，日期格式字符串 part，时间部分的指示符
     * 只能是：YEAR,MONTH,WEEK,DAY,HOUR,MINUTE,SECOND，MILLISECOND
     *
     * @param date 日期形字符串
     * @param pattern 日期形字符串格式
     * @param part 获取的标识单位
     * @return int
     */
    public static int getDatePart(String date, String pattern, int part)
    {
        if (null == date)
        {
            throw new IllegalArgumentException("date parameter is null");
        }
        if (null == pattern)
        {
            throw new IllegalArgumentException("pattern parameter is null");
        }
        if ((part < YEAR) || (part > MINUTEOFDAY))
        {
            throw new IllegalArgumentException("the part parameter must be in [YEAR,MONTH, DAY, HOUR, MINUTE, SECOND]");
        }
        Date dt = timeStr2Date(date, pattern);
        return getDatePart(dt, part);
    }

    /**
     * 获取日期的某一部分 参数：date，有效的日期类型 part，时间部分的指示符，
     * 只能是：YEAR,MONTH,WEEK,DAY,HOUR,MINUTE,SECOND，MILLISECOND
     *
     * @param date 日期
     * @param part 日期标识
     * @return int
     */
    public static int getDatePart(Date date, int part)
    {
        if (null == date)
        {
            throw new IllegalArgumentException("date parameter is null");
        }
        if ((part < YEAR) || (part > MINUTEOFDAY))
        {
            throw new IllegalArgumentException("the part parameter must be in [YEAR,MONTH, DAY, HOUR, MINUTE, SECOND]");
        }
        Calendar calendar = getLocalCalendar(date);
        int result = 0;
        switch (part)
        {
            case YEAR:
                result = calendar.get(Calendar.YEAR);
                break;
            case MONTH:
                result = calendar.get(Calendar.MONTH);
                break;
            case WEEK:
                result = calendar.get(Calendar.DAY_OF_WEEK);
                break;
            case DAY:
                result = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case HOUR:
                result = calendar.get(Calendar.HOUR_OF_DAY);
                break;
            case MINUTE:
                result = calendar.get(Calendar.MINUTE);
                break;
            case SECOND:
                result = calendar.get(Calendar.SECOND);
                break;
            case MILLISECOND:
                result = calendar.get(Calendar.MILLISECOND);
                break;
            case MINUTEOFDAY:
                result = calendar.get(Calendar.HOUR_OF_DAY) * ONE_HOUR + calendar.get(Calendar.MINUTE);
                /* falls through */
            default:
                break;
        }
        return result;
    }

    /**
     * 获得东八时区的日历，并设置日历的当前日期 参数：date，Date，日期型
     *
     * @param date 日期
     * @return java.util.Calendar
     */
    public static Calendar getLocalCalendar(Date date)
    {
        // 设置为GMT+08:00时区
        String[] ids = TimeZone.getAvailableIDs(8 * ONE_HOUR * ONE_MINUTE * ONE_THOUSAND);
        if (0 == ids.length)
        {
            throw new IllegalArgumentException("get id of GMT+08:00 time zone failed");
        }
        // 创建Calendar对象，并设置为指定时间
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());

        // 设置成宽容方式
        if (!calendar.isLenient())
        {
            calendar.setLenient(true);
        }
        // 设置SUNDAY为每周的第一天
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        // 设置日历的当前时间
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 获得当月得开始时间
     *
     * @param date 日期
     * @return java.util.Date
     */
    public static Date getBeginTimeOfMonth(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 当月第1天日期
        Date firstDate = calendar.getTime();
        return firstDate;
    }

    /**
     * 获取当月第一天00时00分00秒
     *
     * @param date
     * @return java.util.Date
     */
    public static Date getBeginTimeOfMonth2(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // 当月第1天日期
        Date firstDate = calendar.getTime();
        return firstDate;
    }

    /**
     * 获取当月的结束时间
     *
     * @param date 日期
     * @return java.util.Date
     */
    public static Date getEndTimeOfMonth(Date date)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        // 当月最后一天日期
        Date lastDate = calendar.getTime();
        return lastDate;
    }


    /**
     * 根据CompareFields的格式（如只比较年月）比较两个日期先后
     * 在比较字段内，若返回1，表示date1在date2之后，返回-1，表示date1在date2之前，0表示两者相等
     *
     * @param date1 日期1
     * @param date2 日期2
     * @param cdf 枚举值
     * @return int
     */
    public static int compare(Date date1, Date date2, CompareDateFormate cdf)
    {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int[] form = map.get(cdf);
        for (int field : form)
        {
            int t1 = c1.get(field);
            int t2 = c2.get(field);
            if (t1 > t2)
            {
                return 1;
            }
            else if (t1 < t2)
            {
                return -1;
            }
        }

        return 0;
    }

    /**
     * 计算时间差
     *
     * @param startTime 开始时间长整形
     * @param endTime 结束时间长整形
     * @return long
     */
    public static long getTimeDifference(long startTime, long endTime)
    {
        if (endTime > startTime)
        {
            return endTime - startTime;
        }
        else
        {
            return 0;
        }
    }

    /**
     * 格式化成系统常用日期格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return java.lang.String
     */
    public static String format(Date date)
    {
        if (null != date)
        {
            return sf.format(date);
        }
        return null;
    }

    /**
     * 格式化日期
     *
     * @param date 日期
     * @param formatStr 格式化字符串
     * @return java.lang.String
     */
    public static String format(Date date, String formatStr)
    {
        if (date == null)
        {
            return null;
        }

        SimpleDateFormat sf = getSimpleDateFormat(formatStr);
        return sf.format(date);
    }

    /**
     * 获取下个月开始时刻
     *
     * @param date 时间
     * @return java.util.Date
     */
    public static Date getBeginOfNextMonth(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // 月份加1
        c.add(Calendar.MONTH, 1);

        return new Date(c.getTimeInMillis());
    }

    /**
     * 获取UTC时间
     *
     * @return java.util.Date
     */
    public static Date getUTCTime()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() - TimeZone.getDefault().getRawOffset());
        return new Date(cal.getTimeInMillis());
    }

    /**
     * 获取UTC时间
     *
     * @param pattern 指定的格式
     * @return java.lang.String
     */
    public static String getUTCTime(String pattern)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() - TimeZone.getDefault().getRawOffset());
        Date date = new Date(cal.getTimeInMillis());
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(date);
    }

    /**
     * 获取指定日期的一周起始时间
     *
     * @param dt 日期
     * @return java.sql.Timestamp
     */
    public static Timestamp getBeginOfCurWeek(Date dt)
    {
        StringBuffer beginDay = new StringBuffer();

        Calendar calendar = getCalForWeek(dt);

        int weekx = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekx == 1)
        {
            weekx = 8;
        }

        calendar.add(Calendar.DAY_OF_WEEK, 2 - weekx);

        beginDay.append(String.valueOf(calendar.get(Calendar.YEAR)));
        // 小于10在月份前添0
        if ((calendar.get(Calendar.MONTH) + 1) < 10)
        {
            beginDay.append("0").append((calendar.get(Calendar.MONTH) + 1));
        }
        else
        {
            beginDay.append((calendar.get(Calendar.MONTH) + 1));
        }
        // 小于10在日期前添0
        if ((calendar.get(Calendar.DAY_OF_MONTH) - 1) < 10)
        {
            beginDay.append("0").append(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        else
        {
            beginDay.append(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        beginDay.append("000000");

        return parse(beginDay.toString(), "yyyyMMddHHmmss");

    }

    /**
     * 获取指定日期的一周结束时间
     *
     * @param dt 日期
     * @return java.sql.Timestamp
     */
    public static Timestamp getEndOfCurWeek(Date dt)
    {
        Calendar calendar = getCalForWeek(dt);

        StringBuffer endDay = new StringBuffer();

        int weekx = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekx == 1)
        {
            weekx = 8;
        }

        calendar.add(Calendar.DAY_OF_WEEK, 2 - weekx);
        calendar.add(Calendar.DAY_OF_MONTH, 6);

        endDay.append(String.valueOf(calendar.get(Calendar.YEAR)));
        // 小于10在月份前添0
        if ((calendar.get(Calendar.MONTH) + 1) < 10)
        {
            endDay.append("0").append((calendar.get(Calendar.MONTH) + 1));
        }
        else
        {
            endDay.append((calendar.get(Calendar.MONTH) + 1));
        }
        // 小于10在日期前添0
        if ((calendar.get(Calendar.DAY_OF_MONTH) - 1) < 10)
        {
            endDay.append("0").append(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        else
        {
            endDay.append(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        endDay.append("235959");

        return parse(endDay.toString(), "yyyyMMddHHmmss");
    }

    /**
     * 获取本周开始时间
     *
     * @param dt 日期
     * @return java.util.Calendar
     */
    private static Calendar getCalForWeek(Date dt)
    {
        Calendar calendar = null;
        String dateStr = format(dt, "yyyyMMddHHmmss");
        Integer year = Integer.valueOf(dateStr.substring(0, 4));
        Integer month = Integer.valueOf(dateStr.substring(4, 6));
        Integer day = Integer.valueOf(dateStr.substring(6, 8));

        calendar = new GregorianCalendar(year.intValue(), (month.intValue() - 1), day.intValue() + 1);
        return calendar;
    }

    /**
     * 根据指定的格式将指定的日期字符串转换为Timestamp类型
     *
     * @param dateStr 日期字符串
     * @param formatStr 日期字符串类型
     * @return java.sql.Timestamp
     */
    public static Timestamp parse(String dateStr, String formatStr)
    {
        if (null == dateStr)
        {
            return null;
        }

        Date date = null;
        SimpleDateFormat sf = getSimpleDateFormat(formatStr);
        try
        {
            date = sf.parse(dateStr);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("date format error");
        }

        if (null != date)
        {
            return new Timestamp(date.getTime());
        }
        else
        {
            return null;
        }
    }

    /**
     * 获取当天开始时间
     *
     * @param date
     * @return java.sql.Timestamp
     */
    public static Timestamp getBeginDate(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * 获取当天结束时间
     *
     * @param date 日期
     * @return java.sql.Timestamp
     */
    public static Timestamp getEndDate(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * 格式化为 Timestamp
     *
     * @param dateStr 日期字符串
     * @param formater 日期字符串类型
     * @return java.sql.Timestamp
     */
    public static Timestamp formTimestamp(String dateStr, String formater)
    {
        return new Timestamp(formdate(dateStr, formater).getTime());
    }

    /**
     * 把字符串格式化日期
     *
     * @param dateStr 日期字符串
     * @param formater 日期字符串格式
     * @return java.util.Date
     */
    public static Date formdate(String dateStr, String formater)
    {
        formater = (Util.isEmpty(formater)) ? "yyyy-MM-dd HH:mm:ss" : formater;
        DateFormat formatter = getSimpleDateFormat(formater);
        Date date = null;
        try
        {
            date = formatter.parse(dateStr);
        }
        catch (ParseException e)
        {
            LOGGER.error("formatter date failed, dataStr:{}, formater:{}", dateStr, formater);
        }
        return date;
    }

    /**
     * 获取下个月开始时刻
     *
     * @param date 时间
     * @return java.sql.Timestamp
     */
    public static Timestamp getBeginOfNextMonthToTimestamp(Date date)
    {
        //每月的开始日期，默认为自然月开始，即每月的1号
        int beginDay = 1;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DATE, beginDay);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // 月份加1
        c.add(Calendar.MONTH, 1);

        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * 获取当月开始时间
     *
     * @param date 时间
     * @return java.sql.Timestamp
     */
    public static Timestamp getBeginOfCurrentMonth(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * 获取当月最后一天23时59分59秒 <
     *
     * @param date 日期
     * @return java.util.Date
     */
    public static Date getEndTimeOfMonth2(Date date)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    /**
     * 判断当前时间是否在有效期内
     *
     * @param beginTimeStr 开始时间字符串
     * @param endTimeStr 结束时间字符串
     * @param dateFormat 时间字符串类型
     * @param compareDateFormate 枚举类型
     * @return boolean
     */
    public static boolean isInEffectiveTime(String beginTimeStr, String endTimeStr, String dateFormat,
        CompareDateFormate compareDateFormate)
    {
        boolean compareResult = false;

        // 开始月份
        Date beginMonth = timeStr2Date(beginTimeStr, dateFormat);

        // 结束月份
        Date endMonth = timeStr2Date(endTimeStr, dateFormat);

        // 当前月份
        Date nowMonth = timeStr2Date(DateTools.getCurrentDate(dateFormat), dateFormat);

        int isLaterThanBeginMonth = compare(nowMonth, beginMonth, compareDateFormate);

        int isEarlierThanEndMonth = compare(endMonth, nowMonth, compareDateFormate);

        // 如果当前月份在开始月份和结束月份之间，返回有效。
        if (isLaterThanBeginMonth >= 0 && isEarlierThanEndMonth > 0)
        {
            compareResult = true;
        }

        return compareResult;
    }

    /**
     * 指定日期是在一周中的第几天
     *
     * @param date 日期
     * @return int
     */
    public static int getDayOfWeek(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 判断时间是否是今天
     *
     * @param date 日期
     * @return boolean
     */
    public static boolean isToday(Date date)
    {
        SimpleDateFormat fmt = getSimpleDateFormat(DATE_FORMAT_10);
        return fmt.format(date).equals(fmt.format(new Date()));
    }

    /**
     * 判断时间是否是指定的时间类型
     *
     * @param date 日期字符串
     * @param appointFormat 日期格式类型
     * @return boolean
     */
    public static boolean isAppointFormatDate(String date, String appointFormat)
    {
        if (Util.isEmpty(date) || Util.isEmpty(appointFormat) || date.length() != appointFormat.length())
        {
            return Boolean.FALSE;
        }
        try
        {
            SimpleDateFormat fmt = new SimpleDateFormat(appointFormat);
            fmt.parse(date);
            return Boolean.TRUE;
        }
        catch (Exception e)
        {
            LOGGER.error("DateTools.isAppointFormatDate has error, the date is: " + date + ", the appointFormat is: "
                + appointFormat + " the error message is: " + e.getMessage());
        }

        return Boolean.FALSE;
    }

    /**
     * 将类型为"yyyyMMddHHmmss"时间字符串转为"yyyy-MM-dd HH:mm:ss"的时间串
     *
     * @param date 日期字符串
     * @return java.lang.String
     */
    public static String strChangeTime(String date)
    {
        if (StringUtils.isEmpty(date))
        {
            return null;
        }
        try
        {
            StringBuffer sbf = new StringBuffer();
            String data1 = date.substring(0, 4) + ("-");
            sbf.append(data1);
            String data2 = date.substring(4, 6) + ("-");
            sbf.append(data2);
            String data3 = date.substring(6, 8) + (" ");
            sbf.append(data3);
            String data4 = date.substring(8, 10) + (":");
            sbf.append(data4);
            String data5 = date.substring(10, 12) + (":");
            sbf.append(data5);
            String data6 = date.substring(12);
            sbf.append(data6);
            return sbf + "";
        }
        catch (Exception e)
        {
            LOGGER.error(
                " DateTools strChangeTime has error the date is " + date + " the error message is: " + e.getMessage());
        }

        return getCurrentDate(DateTools.DATE_FORMAT_24HOUR_19);
    }
}
