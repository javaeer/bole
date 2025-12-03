package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;

@Slf4j
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    // 时间格式常量
    public static final String PATTERN_Y = "yyyy";
    public static final String PATTERN_YM = "yyyy-MM";
    public static final String PATTERN_YMD = "yyyy-MM-dd";
    public static final String PATTERN_YMD_H = "yyyy-MM-dd HH";
    public static final String PATTERN_YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_YMD_HMS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_HMS = "HH:mm:ss";
    public static final String PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_STR_YM = "yyyyMM";
    public static final String PATTERN_STR_YMD = "yyyyMMdd";
    public static final String PATTERN_STR_YMDH = "yyyyMMddHH";
    public static final String PATTERN_STR_YMDHM = "yyyyMMddHHmm";
    public static final String PATTERN_STR_YMDHMS = "yyyyMMddHHmmss";
    public static final String PATTERN_STR_YMDHMSSSS = "yyyyMMddHHmmssSSS";
    public static final String GMT8 = "GMT+8";
    public static final int UNIX_TIMESTAMP_LENGTH = 10;
    public static final int TIMESTAMP_LENGTH = 13;
    private static final ZoneId DEFAULT_ZONE = ZoneId.of(GMT8);

    private static final DateTimeFormatter FORMATTER_Y =
            DateTimeFormatter.ofPattern(PATTERN_Y).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_YM =
            DateTimeFormatter.ofPattern(PATTERN_YMD).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_YMD =
            DateTimeFormatter.ofPattern(PATTERN_YMD).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_YMD_H =
            DateTimeFormatter.ofPattern(PATTERN_YMD_H).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_YMD_HM =
            DateTimeFormatter.ofPattern(PATTERN_YMD_HM).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_YMD_HMS =
            DateTimeFormatter.ofPattern(PATTERN_YMD_HMS).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_HMS =
            DateTimeFormatter.ofPattern(PATTERN_HMS).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_YMD_HMS_SSS =
            DateTimeFormatter.ofPattern(PATTERN_YMD_HMS_SSS).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_STR_YM =
            DateTimeFormatter.ofPattern(PATTERN_STR_YMD).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_STR_YMD =
            DateTimeFormatter.ofPattern(PATTERN_STR_YMD).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_STR_YMDH =
            DateTimeFormatter.ofPattern(PATTERN_STR_YMDH).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_STR_YMDHM =
            DateTimeFormatter.ofPattern(PATTERN_STR_YMDHM).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_STR_YMDHMS =
            DateTimeFormatter.ofPattern(PATTERN_STR_YMDHMS).withZone(DEFAULT_ZONE);
    private static final DateTimeFormatter FORMATTER_STR_YMDHMSSSS =
            DateTimeFormatter.ofPattern(PATTERN_STR_YMDHMSSSS).withZone(DEFAULT_ZONE);

    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new HashMap<>();

    static {
        FORMATTER_CACHE.put(PATTERN_Y, FORMATTER_Y);
        FORMATTER_CACHE.put(PATTERN_YM, FORMATTER_YM);
        FORMATTER_CACHE.put(PATTERN_YMD, FORMATTER_YMD);
        FORMATTER_CACHE.put(PATTERN_YMD_H, FORMATTER_YMD_H);
        FORMATTER_CACHE.put(PATTERN_YMD_HM, FORMATTER_YMD_HM);
        FORMATTER_CACHE.put(PATTERN_YMD_HMS, FORMATTER_YMD_HMS);
        FORMATTER_CACHE.put(PATTERN_HMS, FORMATTER_HMS);
        FORMATTER_CACHE.put(PATTERN_YMD_HMS_SSS, FORMATTER_YMD_HMS_SSS);
        FORMATTER_CACHE.put(PATTERN_STR_YM, FORMATTER_STR_YM);
        FORMATTER_CACHE.put(PATTERN_STR_YMD, FORMATTER_STR_YMD);
        FORMATTER_CACHE.put(PATTERN_STR_YMDH, FORMATTER_STR_YMDH);
        FORMATTER_CACHE.put(PATTERN_STR_YMDHM, FORMATTER_STR_YMDHM);
        FORMATTER_CACHE.put(PATTERN_STR_YMDHMS, FORMATTER_STR_YMDHMS);
        FORMATTER_CACHE.put(PATTERN_STR_YMDHMSSSS, FORMATTER_STR_YMDHMSSSS);
    }

    private DateUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 在当前时间之前 true 否则 false 比较者 被比较者 compared
     *
     * @param target
     * @return 是否 在前
     */
    public static boolean isBefore(Date target) {
        validateDates(target);
        return isBefore(target, now());
    }

    // 基础时间判断
    public static boolean isBefore(Date target, Date date2) {
        validateDates(target, date2);
        return target.before(date2);
    }

    /**
     * 在当前时间之后 true 否则 false
     *
     * @param date
     * @return
     */
    public static boolean isAfter(Date date) {
        validateDates(date);
        return date.after(now());
    }

    public static boolean isAfter(Date date1, Date date2) {
        validateDates(date1, date2);
        return date1.after(date2);
    }

    /**
     * 时间范围判断
     *
     * @param target
     * @param start
     * @param end
     * @return
     */
    public static boolean isBetween(Date target, Date start, Date end) {
        validateDates(target, start, end);
        long time = target.getTime();
        return time >= start.getTime() && time <= end.getTime();
    }

    /**
     * 计算两个日期之间的完整年数差异
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 年数差异（不足一年返回0）
     */
    public static long yearsBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.YEARS.between(toLocalDate(start), toLocalDate(end));
    }

    /**
     * 计算两个日期之间的完整月数差异
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 月数差异（不足一月返回0）
     */
    public static long monthsBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.MONTHS.between(toLocalDate(start), toLocalDate(end));
    }

    /**
     * 计算两个日期之间的天数差异
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 天数差异（精确到天）
     */
    public static long daysBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.DAYS.between(toLocalDate(start), toLocalDate(end));
    }

    /**
     * 计算两个日期之间的小时差异
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 小时差异（精确到小时）
     */
    public static long hoursBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.HOURS.between(toLocalDateTime(start), toLocalDateTime(end));
    }

    /**
     * 计算两个日期之间的分钟差异
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 分钟差异（精确到分钟）
     */
    public static long minutesBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.MINUTES.between(toLocalDateTime(start), toLocalDateTime(end));
    }

    /**
     * 计算两个日期之间的秒数差异
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 秒数差异（精确到秒）
     */
    public static long secondsBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.SECONDS.between(toLocalDateTime(start), toLocalDateTime(end));
    }

    /**
     * 计算两个日期之间的毫秒数差异
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 毫秒数差异（精确到毫秒）
     */
    public static long millisBetween(Date start, Date end) {
        validateDates(start, end);
        return ChronoUnit.MILLIS.between(toLocalDateTime(start), toLocalDateTime(end));
    }

    // 时间转换方法
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE);
    }

    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    public static Date fromLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE).toInstant());
    }

    // 日期操作（使用Java 8 API）
    public static Date addYears(Date date, int years) {
        return Date.from(toLocalDateTime(date).plusYears(years).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date addMonths(Date date, int months) {
        return Date.from(toLocalDateTime(date).plusMonths(months).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date addWeeks(Date date, int weeks) {
        return Date.from(toLocalDateTime(date).plusWeeks(weeks).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date addDays(Date date, int days) {
        return Date.from(toLocalDateTime(date).plusDays(days).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date addHours(Date date, int hours) {
        return Date.from(toLocalDateTime(date).plusHours(hours).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date addMinutes(Date date, int minutes) {
        return Date.from(
                toLocalDateTime(date).plusMinutes(minutes).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date addSeconds(Date date, int seconds) {
        return Date.from(
                toLocalDateTime(date).plusSeconds(seconds).atZone(DEFAULT_ZONE).toInstant());
    }

    public static Date firstDayOfMonth(Date date) {
        return fromLocalDate(toLocalDate(date).withDayOfMonth(1));
    }

    // 格式化与解析
    public static String format(Date date) {
        return format(date, PATTERN_YMD_HMS);
    }

    public static String format(Date date, String pattern) {
        DateTimeFormatter formatter =
                FORMATTER_CACHE.computeIfAbsent(
                        pattern, p -> DateTimeFormatter.ofPattern(p).withZone(DEFAULT_ZONE));
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE).format(formatter);
    }

    public static Date parse(String dateString) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(
                    dateString,
                    PATTERN_YMD_HMS_SSS,
                    PATTERN_YMD_HMS,
                    PATTERN_YMD_HM,
                    PATTERN_YMD_H,
                    PATTERN_YMD,
                    PATTERN_YM,
                    PATTERN_Y);
        } catch (ParseException e) {
            log.error(
                    "Failed to parse date: {} with patterns {}",
                    dateString,
                    Arrays.toString(
                            new String[] {
                                PATTERN_YMD_HMS_SSS,
                                PATTERN_YMD_HMS,
                                PATTERN_YMD_HM,
                                PATTERN_YMD_H,
                                PATTERN_YMD,
                                PATTERN_YM,
                                PATTERN_Y
                            }),
                    e);
            throw new IllegalArgumentException("Failed to parse date", e);
        }
    }

    // 验证工具方法
    private static void validateDates(Date... dates) {
        for (Date date : dates) {
            if (date == null) {
                throw new IllegalArgumentException("Date parameters cannot be null");
            }
        }
    }

    // 获取当前时间（带时区）
    public static Date now() {
        return now(DEFAULT_ZONE);
    }

    public static Date now(ZoneId zoneId) {
        return Date.from(LocalDateTime.now(zoneId).atZone(zoneId).toInstant());
    }

    // Unix时间戳处理
    public static long toTimestamp(Date date) {
        return date.getTime();
    }

    // Unix时间戳处理
    public static long toUnixTimestamp(Date date) {
        return date.getTime() / 1000;
    }

    public static Date fromUnixTimestamp(long timestamp) {
        return new Date(timestamp * 1000);
    }

    public static Date fromTimestamp(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * 判断两个日期是否是同一年
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 是否同一年
     */
    public static boolean isSameYear(Date date1, Date date2) {
        validateDates(date1, date2);
        LocalDate ld1 = toLocalDate(date1);
        LocalDate ld2 = toLocalDate(date2);
        return ld1.getYear() == ld2.getYear();
    }

    /**
     * 判断两个日期是否是同一月（同年同月）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 是否同一月
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        validateDates(date1, date2);
        LocalDate ld1 = toLocalDate(date1);
        LocalDate ld2 = toLocalDate(date2);
        return ld1.getYear() == ld2.getYear() && ld1.getMonth() == ld2.getMonth();
    }

    /**
     * 判断两个日期是否是同一周（同年同月同周）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 是否同一周
     */
    public static boolean isSameWeek(Date date1, Date date2) {
        validateDates(date1, date2);
        LocalDate ld1 = toLocalDate(date1);
        LocalDate ld2 = toLocalDate(date2);
        return ld1.getYear() == ld2.getYear()
                && ld1.get(WeekFields.ISO.weekOfYear()) == ld2.get(WeekFields.ISO.weekOfYear());
    }

    /**
     * 判断两个日期是否是同一天（同年同月同日）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 是否同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        validateDates(date1, date2);
        LocalDate ld1 = toLocalDate(date1);
        LocalDate ld2 = toLocalDate(date2);
        return ld1.isEqual(ld2);
    }

    /**
     * 判断两个时间是否是同一小时（同年同月同日同时）
     *
     * @param date1 第一个时间
     * @param date2 第二个时间
     * @return 是否同一小时
     */
    public static boolean isSameHour(Date date1, Date date2) {
        validateDates(date1, date2);
        LocalDateTime ldt1 = toLocalDateTime(date1);
        LocalDateTime ldt2 = toLocalDateTime(date2);
        return ldt1.getYear() == ldt2.getYear()
                && ldt1.getMonth() == ldt2.getMonth()
                && ldt1.getDayOfMonth() == ldt2.getDayOfMonth()
                && ldt1.getHour() == ldt2.getHour();
    }

    /**
     * 判断两个时间是否是同一分钟（精确到分钟）
     *
     * @param date1 第一个时间
     * @param date2 第二个时间
     * @return 是否同一分钟
     */
    public static boolean isSameMinute(Date date1, Date date2) {
        validateDates(date1, date2);
        LocalDateTime ldt1 = toLocalDateTime(date1);
        LocalDateTime ldt2 = toLocalDateTime(date2);
        return ldt1.getYear() == ldt2.getYear()
                && ldt1.getMonth() == ldt2.getMonth()
                && ldt1.getDayOfMonth() == ldt2.getDayOfMonth()
                && ldt1.getHour() == ldt2.getHour()
                && ldt1.getMinute() == ldt2.getMinute();
    }

    public static void main(String[] args) {

        System.out.println(truncate(DateUtils.now(), Calendar.YEAR));
    }
}
