/**
 * 日期处理工具类
 * 提供日期格式化、计算、比较等常用功能
 */

/**
 * // 基本使用
 * const now = new Date();
 *
 * // 格式化
 * console.log(DateUtils.format(now, 'yyyy-MM-dd HH:mm:ss')); // 2024-01-15 14:30:45
 * console.log(DateUtils.format(now, 'yyyy年MM月dd日 EEEE')); // 2024年01月15日 Monday
 *
 * // 日期计算
 * const tomorrow = DateUtils.add(now, 1, 'day');
 * const lastMonth = DateUtils.add(now, -1, 'month');
 *
 * // 日期比较
 * const isBefore = DateUtils.isBefore('2024-01-01', now);
 * const isSameDay = DateUtils.isEqual(now, tomorrow, 'day');
 *
 * // 获取日期部分
 * const year = DateUtils.get(now, 'year');
 * const month = DateUtils.get(now, 'month');
 * const day = DateUtils.get(now, 'day');
 *
 * // 相对时间
 * console.log(DateUtils.fromNow(DateUtils.add(now, -5, 'minute'))); // 5分钟前
 *
 * // 使用快捷方法
 * import { dateUtils } from './date-utils';
 *
 * console.log(dateUtils.format(now));
 * console.log(dateUtils.add(now, 1, 'week'));
 * console.log(dateUtils.startOf.day(now));
 */

export type DateInput = Date | string | number;
export type DateUnit = 'year' | 'month' | 'week' | 'day' | 'hour' | 'minute' | 'second' | 'millisecond';
export type WeekStartDay = 0 | 1; // 0 = 周日, 1 = 周一

/**
 * 日期格式化选项
 */
export interface DateFormatOptions {
  /** 年份格式：'yyyy' 完整年份, 'yy' 两位年份 */
  year?: 'yyyy' | 'yy';
  /** 月份格式：'MM' 两位月份, 'M' 一位月份, 'MMM' 英文缩写, 'MMMM' 英文全称 */
  month?: 'MM' | 'M' | 'MMM' | 'MMMM';
  /** 日期格式：'dd' 两位日期, 'd' 一位日期 */
  day?: 'dd' | 'd';
  /** 星期格式：'EEE' 英文缩写, 'EEEE' 英文全称, 'E' 数字（0-6） */
  weekday?: 'EEE' | 'EEEE' | 'E';
  /** 小时格式：'HH' 24小时制两位, 'H' 24小时制一位, 'hh' 12小时制两位, 'h' 12小时制一位 */
  hour?: 'HH' | 'H' | 'hh' | 'h';
  /** 分钟格式：'mm' 两位, 'm' 一位 */
  minute?: 'mm' | 'm';
  /** 秒格式：'ss' 两位, 's' 一位 */
  second?: 'ss' | 's';
  /** 毫秒格式：'SSS' 三位 */
  millisecond?: 'SSS';
  /** 上午/下午标记：'a' */
  meridiem?: 'a';
}

/**
 * 日期范围接口
 */
export interface DateRange {
  start: Date;
  end: Date;
}

/**
 * 日期工具类
 */
export class DateUtils {
  private static readonly MONTH_NAMES = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  private static readonly MONTH_NAMES_SHORT = [
    'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
  ];

  private static readonly WEEKDAY_NAMES = [
    'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'
  ];

  private static readonly WEEKDAY_NAMES_SHORT = [
    'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'
  ];

  /**
   * 转换为 Date 对象
   */
  static toDate(input: DateInput): Date {
    if (input instanceof Date) {
      return new Date(input.getTime());
    }

    if (typeof input === 'string') {
      // 处理 ISO 格式字符串
      if (input.includes('T') || /^\d{4}-\d{2}-\d{2}/.test(input)) {
        const date = new Date(input);
        if (!isNaN(date.getTime())) {
          return date;
        }
      }

      // 处理时间戳字符串
      const timestamp = parseInt(input, 10);
      if (!isNaN(timestamp)) {
        return new Date(timestamp);
      }

      // 其他格式尝试解析
      return new Date(Date.parse(input));
    }

    // 处理数字（时间戳）
    return new Date(input);
  }

  /**
   * 格式化日期
   * @param date 日期
   * @param format 格式字符串，支持：
   *   yyyy - 完整年份
   *   yy   - 两位年份
   *   MMMM - 月份全称
   *   MMM  - 月份缩写
   *   MM   - 两位月份
   *   M    - 月份（不补零）
   *   dd   - 两位日期
   *   d    - 日期（不补零）
   *   EEEE - 星期全称
   *   EEE  - 星期缩写
   *   HH   - 24小时制两位小时
   *   H    - 24小时制小时（不补零）
   *   hh   - 12小时制两位小时
   *   h    - 12小时制小时（不补零）
   *   mm   - 两位分钟
   *   m    - 分钟（不补零）
   *   ss   - 两位秒
   *   s    - 秒（不补零）
   *   SSS  - 三位毫秒
   *   a    - 上午/下午
   */
  static format(date: DateInput, format: string = 'yyyy-MM-dd HH:mm:ss'): string {
    const d = this.toDate(date);

    const pad = (n: number, length: number = 2): string => {
      return n.toString().padStart(length, '0');
    };

    const replacements: Record<string, string> = {
      yyyy: d.getFullYear().toString(),
      yy: d.getFullYear().toString().slice(-2),
      MMMM: this.MONTH_NAMES[d.getMonth()],
      MMM: this.MONTH_NAMES_SHORT[d.getMonth()],
      MM: pad(d.getMonth() + 1),
      M: (d.getMonth() + 1).toString(),
      dd: pad(d.getDate()),
      d: d.getDate().toString(),
      EEEE: this.WEEKDAY_NAMES[d.getDay()],
      EEE: this.WEEKDAY_NAMES_SHORT[d.getDay()],
      HH: pad(d.getHours()),
      H: d.getHours().toString(),
      hh: pad(d.getHours() % 12 || 12),
      h: (d.getHours() % 12 || 12).toString(),
      mm: pad(d.getMinutes()),
      m: d.getMinutes().toString(),
      ss: pad(d.getSeconds()),
      s: d.getSeconds().toString(),
      SSS: pad(d.getMilliseconds(), 3),
      a: d.getHours() < 12 ? 'AM' : 'PM',
    };

    return format.replace(
      /yyyy|yy|MMMM|MMM|MM|M|dd|d|EEEE|EEE|HH|H|hh|h|mm|m|ss|s|SSS|a/g,
      (match) => replacements[match] || match
    );
  }

  /**
   * 解析字符串为日期
   */
  static parse(str: string, format?: string): Date | null {
    if (!format) {
      const date = new Date(str);
      return isNaN(date.getTime()) ? null : date;
    }

    // 简单的格式解析实现
    // 注：完整实现需要更复杂的解析逻辑
    const patterns: Record<string, RegExp> = {
      'yyyy-MM-dd': /^(\d{4})-(\d{2})-(\d{2})$/,
      'yyyy/MM/dd': /^(\d{4})\/(\d{2})\/(\d{2})$/,
      'dd-MM-yyyy': /^(\d{2})-(\d{2})-(\d{4})$/,
      'dd/MM/yyyy': /^(\d{2})\/(\d{2})\/(\d{4})$/,
      'yyyy-MM-dd HH:mm:ss': /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
    };

    const pattern = patterns[format];
    if (!pattern) {
      const date = new Date(str);
      return isNaN(date.getTime()) ? null : date;
    }

    const match = str.match(pattern);
    if (!match) return null;

    // 根据格式创建日期
    if (format.includes('yyyy-MM-dd')) {
      const [, year, month, day, hour = '0', minute = '0', second = '0'] = match;
      return new Date(
        parseInt(year),
        parseInt(month) - 1,
        parseInt(day),
        parseInt(hour),
        parseInt(minute),
        parseInt(second)
      );
    }

    return null;
  }

  /**
   * 日期加减
   */
  static add(date: DateInput, amount: number, unit: DateUnit): Date {
    const d = this.toDate(date);
    const result = new Date(d.getTime());

    switch (unit) {
      case 'year':
        result.setFullYear(result.getFullYear() + amount);
        break;
      case 'month':
        result.setMonth(result.getMonth() + amount);
        break;
      case 'week':
        result.setDate(result.getDate() + amount * 7);
        break;
      case 'day':
        result.setDate(result.getDate() + amount);
        break;
      case 'hour':
        result.setHours(result.getHours() + amount);
        break;
      case 'minute':
        result.setMinutes(result.getMinutes() + amount);
        break;
      case 'second':
        result.setSeconds(result.getSeconds() + amount);
        break;
      case 'millisecond':
        result.setMilliseconds(result.getMilliseconds() + amount);
        break;
    }

    return result;
  }

  /**
   * 设置日期部分
   */
  static set(date: DateInput, values: Partial<Record<DateUnit, number>>): Date {
    const d = this.toDate(date);
    const result = new Date(d.getTime());

    if (values.year !== undefined) result.setFullYear(values.year);
    if (values.month !== undefined) result.setMonth(values.month - 1); // 月份从0开始
    if (values.day !== undefined) result.setDate(values.day);
    if (values.hour !== undefined) result.setHours(values.hour);
    if (values.minute !== undefined) result.setMinutes(values.minute);
    if (values.second !== undefined) result.setSeconds(values.second);
    if (values.millisecond !== undefined) result.setMilliseconds(values.millisecond);

    return result;
  }

  /**
   * 获取日期部分
   */
  static get(date: DateInput, unit: DateUnit): number {
    const d = this.toDate(date);

    switch (unit) {
      case 'year':
        return d.getFullYear();
      case 'month':
        return d.getMonth() + 1;
      case 'week':
        return this.getWeekNumber(d);
      case 'day':
        return d.getDate();
      case 'hour':
        return d.getHours();
      case 'minute':
        return d.getMinutes();
      case 'second':
        return d.getSeconds();
      case 'millisecond':
        return d.getMilliseconds();
      default:
        return 0;
    }
  }

  /**
   * 获取星期几（0-6，0=周日）
   */
  static getDay(date: DateInput): number {
    return this.toDate(date).getDay();
  }

  /**
   * 获取当月天数
   */
  static getDaysInMonth(date: DateInput): number {
    const d = this.toDate(date);
    return new Date(d.getFullYear(), d.getMonth() + 1, 0).getDate();
  }

  /**
   * 获取周数
   */
  static getWeekNumber(date: DateInput, startDay: WeekStartDay = 1): number {
    const d = this.toDate(date);
    const firstDayOfYear = new Date(d.getFullYear(), 0, 1);
    const pastDaysOfYear = (d.getTime() - firstDayOfYear.getTime()) / 86400000;

    return Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
  }

  /**
   * 是否为闰年
   */
  static isLeapYear(date: DateInput): boolean {
    const year = this.get(date, 'year');
    return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
  }

  /**
   * 比较两个日期
   * @returns 0: 相等, 1: date1 > date2, -1: date1 < date2
   */
  static compare(date1: DateInput, date2: DateInput, unit?: DateUnit): number {
    const d1 = this.toDate(date1);
    const d2 = this.toDate(date2);

    if (unit) {
      // 比较指定单位
      const v1 = this.get(d1, unit);
      const v2 = this.get(d2, unit);
      return v1 === v2 ? 0 : v1 > v2 ? 1 : -1;
    }

    // 比较完整日期
    const time1 = d1.getTime();
    const time2 = d2.getTime();
    return time1 === time2 ? 0 : time1 > time2 ? 1 : -1;
  }

  /**
   * 判断两个日期是否相等（可指定比较单位）
   */
  static isEqual(date1: DateInput, date2: DateInput, unit?: DateUnit): boolean {
    return this.compare(date1, date2, unit) === 0;
  }

  /**
   * 判断 date1 是否在 date2 之前
   */
  static isBefore(date1: DateInput, date2: DateInput, unit?: DateUnit): boolean {
    return this.compare(date1, date2, unit) === -1;
  }

  /**
   * 判断 date1 是否在 date2 之后
   */
  static isAfter(date1: DateInput, date2: DateInput, unit?: DateUnit): boolean {
    return this.compare(date1, date2, unit) === 1;
  }

  /**
   * 判断日期是否在范围内
   */
  static isBetween(date: DateInput, range: DateRange, inclusive: boolean = true): boolean {
    const d = this.toDate(date).getTime();
    const start = range.start.getTime();
    const end = range.end.getTime();

    if (inclusive) {
      return d >= start && d <= end;
    }
    return d > start && d < end;
  }

  /**
   * 获取日期差
   */
  static diff(date1: DateInput, date2: DateInput, unit: DateUnit): number {
    const d1 = this.toDate(date1);
    const d2 = this.toDate(date2);
    const diffMs = d1.getTime() - d2.getTime();

    switch (unit) {
      case 'millisecond':
        return diffMs;
      case 'second':
        return Math.floor(diffMs / 1000);
      case 'minute':
        return Math.floor(diffMs / (1000 * 60));
      case 'hour':
        return Math.floor(diffMs / (1000 * 60 * 60));
      case 'day':
        return Math.floor(diffMs / (1000 * 60 * 60 * 24));
      case 'week':
        return Math.floor(diffMs / (1000 * 60 * 60 * 24 * 7));
      case 'month':
        const yearDiff = d1.getFullYear() - d2.getFullYear();
        const monthDiff = d1.getMonth() - d2.getMonth();
        return yearDiff * 12 + monthDiff;
      case 'year':
        return d1.getFullYear() - d2.getFullYear();
      default:
        return 0;
    }
  }

  /**
   * 获取月份的开始日期
   */
  static startOfMonth(date: DateInput): Date {
    const d = this.toDate(date);
    return new Date(d.getFullYear(), d.getMonth(), 1);
  }

  /**
   * 获取月份的结束日期
   */
  static endOfMonth(date: DateInput): Date {
    const d = this.toDate(date);
    return new Date(d.getFullYear(), d.getMonth() + 1, 0, 23, 59, 59, 999);
  }

  /**
   * 获取周的开始日期
   */
  static startOfWeek(date: DateInput, startDay: WeekStartDay = 1): Date {
    const d = this.toDate(date);
    const day = d.getDay();
    const diff = (day < startDay ? 7 : 0) + day - startDay;

    const result = new Date(d);
    result.setDate(d.getDate() - diff);
    result.setHours(0, 0, 0, 0);
    return result;
  }

  /**
   * 获取周的结束日期
   */
  static endOfWeek(date: DateInput, startDay: WeekStartDay = 1): Date {
    const start = this.startOfWeek(date, startDay);
    const result = new Date(start);
    result.setDate(start.getDate() + 6);
    result.setHours(23, 59, 59, 999);
    return result;
  }

  /**
   * 获取天的开始时间
   */
  static startOfDay(date: DateInput): Date {
    const d = this.toDate(date);
    return new Date(d.getFullYear(), d.getMonth(), d.getDate());
  }

  /**
   * 获取天的结束时间
   */
  static endOfDay(date: DateInput): Date {
    const d = this.toDate(date);
    return new Date(d.getFullYear(), d.getMonth(), d.getDate(), 23, 59, 59, 999);
  }

  /**
   * 获取季度
   */
  static getQuarter(date: DateInput): number {
    const month = this.get(date, 'month');
    return Math.floor((month - 1) / 3) + 1;
  }

  /**
   * 获取季度的开始日期
   */
  static startOfQuarter(date: DateInput): Date {
    const d = this.toDate(date);
    const quarter = this.getQuarter(d);
    const month = (quarter - 1) * 3;

    return new Date(d.getFullYear(), month, 1);
  }

  /**
   * 获取季度的结束日期
   */
  static endOfQuarter(date: DateInput): Date {
    const d = this.toDate(date);
    const quarter = this.getQuarter(d);
    const month = quarter * 3;

    return new Date(d.getFullYear(), month, 0, 23, 59, 59, 999);
  }

  /**
   * 获取相对时间描述（如：3分钟前，2天前）
   */
  static fromNow(date: DateInput): string {
    const d = this.toDate(date);
    const now = new Date();
    const diffMs = now.getTime() - d.getTime();
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);
    const diffMonth = Math.floor(diffDay / 30);
    const diffYear = Math.floor(diffMonth / 12);

    if (diffYear > 0) return `${diffYear}年前`;
    if (diffMonth > 0) return `${diffMonth}个月前`;
    if (diffDay > 0) return `${diffDay}天前`;
    if (diffHour > 0) return `${diffHour}小时前`;
    if (diffMin > 0) return `${diffMin}分钟前`;
    if (diffSec > 0) return `${diffSec}秒前`;
    return '刚刚';
  }

  /**
   * 获取日期范围
   */
  static getDateRange(start: DateInput, end: DateInput, unit: DateUnit = 'day'): Date[] {
    const dates: Date[] = [];
    let current = this.toDate(start);
    const endDate = this.toDate(end);

    while (current <= endDate) {
      dates.push(new Date(current));
      current = this.add(current, 1, unit);

      // 防止无限循环
      if (dates.length > 10000) break;
    }

    return dates;
  }

  /**
   * 判断是否为有效日期
   */
  static isValid(date: any): boolean {
    if (!date) return false;

    try {
      const d = this.toDate(date);
      return !isNaN(d.getTime());
    } catch {
      return false;
    }
  }

  /**
   * 获取当前时间戳
   */
  static now(): number {
    return Date.now();
  }

  /**
   * 获取当前日期对象
   */
  static today(): Date {
    return new Date();
  }

  /**
   * 克隆日期
   */
  static clone(date: DateInput): Date {
    return this.toDate(date);
  }

  /**
   * 获取最小日期
   */
  static min(...dates: DateInput[]): Date {
    return new Date(Math.min(...dates.map(d => this.toDate(d).getTime())));
  }

  /**
   * 获取最大日期
   */
  static max(...dates: DateInput[]): Date {
    return new Date(Math.max(...dates.map(d => this.toDate(d).getTime())));
  }
}

/**
 * 快捷方法导出
 */
export const dateUtils = {
  // 格式化相关
  format: DateUtils.format.bind(DateUtils),
  parse: DateUtils.parse.bind(DateUtils),

  // 计算相关
  add: DateUtils.add.bind(DateUtils),
  diff: DateUtils.diff.bind(DateUtils),

  // 比较相关
  isEqual: DateUtils.isEqual.bind(DateUtils),
  isBefore: DateUtils.isBefore.bind(DateUtils),
  isAfter: DateUtils.isAfter.bind(DateUtils),
  isBetween: DateUtils.isBetween.bind(DateUtils),

  // 获取相关
  get: DateUtils.get.bind(DateUtils),
  startOf: {
    day: DateUtils.startOfDay.bind(DateUtils),
    week: DateUtils.startOfWeek.bind(DateUtils),
    month: DateUtils.startOfMonth.bind(DateUtils),
    quarter: DateUtils.startOfQuarter.bind(DateUtils),
  },
  endOf: {
    day: DateUtils.endOfDay.bind(DateUtils),
    week: DateUtils.endOfWeek.bind(DateUtils),
    month: DateUtils.endOfMonth.bind(DateUtils),
    quarter: DateUtils.endOfQuarter.bind(DateUtils),
  },

  // 工具方法
  isValid: DateUtils.isValid.bind(DateUtils),
  fromNow: DateUtils.fromNow.bind(DateUtils),
  today: DateUtils.today.bind(DateUtils),
  now: DateUtils.now.bind(DateUtils),
};

export default DateUtils;