/*
 *
 *     Copyright (c) 2019 - forever, javaeer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the haoheio.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * Author: javaeer (javaeer@aliyun.com)
 *
 */

package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    public final static String ZERO = "0";
    public final static String ZERO_O = "0.0";
    public final static String ZERO_OO = "0.00";
    /**
     * 支持的最小进制数
     */
    public static final int MIN_RADIX = 2;
    final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};
    /**
     * 支持的最大进制数
     */
    public static final int MAX_RADIX = digits.length;
    /**
     * 62进制字母【已排除易混淆字符】
     */
    final static String[] letters = {"A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};
    /**
     * 数字
     */
    final static char[] nums = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    final static Map<Character, Integer> digitMap = new HashMap<>();
    private static final Integer DEF_DIV_SCALE = 2;

    static {
        for (int i = 0; i < digits.length; i++) {
            digitMap.put(digits[i], i);
        }
    }

    /**
     * 将长整型数值转换为指定的进制数（最大支持62进制，字母数字已经用尽）
     *
     * @param i
     * @param radix
     * @return
     */
    public static String toString(long i, int radix) {
        if (radix < MIN_RADIX || radix > MAX_RADIX) {
            radix = 10;
        }
        if (radix == 10) {
            return Long.toString(i);
        }

        final int size = 65;
        int charPos = 64;

        char[] buf = new char[size];
        boolean negative = (i < 0);

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = digits[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = digits[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }


        return new String(buf, charPos, (size - charPos));
    }


    /**
     * 将 64以内的数字 转化为 大写字母
     *
     * @param i
     * @return
     */
    public static String toUpperLetter(int i) {
        Assert.isTrue(i <= 26, "i must be no greater than 26");
        return (char) (64 + i) + "";
    }

    /**
     * 将字符串转换为长整型数字
     *
     * @param s     数字字符串
     * @param radix 进制数
     * @return
     */
    public static long toNumber(String s, int radix) {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < MIN_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " less than Numbers.MIN_RADIX");
        }
        if (radix > MAX_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " greater than Numbers.MAX_RADIX");
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multmin;
        Integer digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw new NumberFormatException("For input : \"" + s + "\" Invalid");
                }

                if (len == 1) {
                    throw new NumberFormatException("For input : \"" + s + "\" only one ");
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                digit = digitMap.get(s.charAt(i++));
                if (digit == null) {
                    throw new NumberFormatException("For input : \"" + s + "\"");
                }
                if (digit < 0) {
                    throw new NumberFormatException("For input : \"" + s + "\"");
                }
                if (result < multmin) {
                    throw new NumberFormatException("For input : \"" + s + "\"");
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException("For input : \"" + s + "\"");
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException("For input : \"" + s + "\"");
        }
        return negative ? result : -result;
    }

    /**
     * 获取百分比
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return
     */
    public static String getPercent(Double dividend, Double divisor) {
        Assert.isTrue(divisor != 0, "divisor must ne 0");
        double percent = dividend / divisor;
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(DEF_DIV_SCALE);
        return nt.format(percent);
    }

    /**
     * 是否 奇数
     *
     * @param num
     * @return
     */
    public static boolean isOdd(int num) {

        if ((num & 1) == 1) {
            return true;
        }
        return false;
    }


    /**
     * 是否偶数
     *
     * @param num
     * @return
     */
    public static boolean isEven(int num) {

        if ((num & 1) != 1) {
            return true;
        }
        return false;
    }


    /**
     * 指定长度的 随机数字
     *
     * @param length
     * @return
     */
    public static String randomNums(int length) {
        if (length <= 0) {
            length = 4;
        }
        StringBuffer sb = new StringBuffer();
        String str = "0123456789";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(str.length());
            sb.append(str.charAt(num));
            str.replace((str.charAt(num) + ""), "");
        }
        return sb.toString();
    }

    /**
     * 补 0
     *
     * @param num
     * @return
     */
    public static String fillZero(int num) {
        return frontCompWithZero(num, 2);
    }

    /**
     * 补 00
     *
     * @param num
     * @return
     */
    public static String fillTwoZero(int num) {
        return frontCompWithZero(num, 3);
    }

    /**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     *
     * @param num
     * @param formatLength
     * @return 重组后的数据
     */
    public static String frontCompWithZero(int num, int formatLength) {
        /*
         * 0 指前面补充零
         * formatLength 字符总长度为 formatLength
         * d 代表为正数。
         */
        return String.format("%0" + formatLength + "d", num);
    }


    /**
     * a的n次方 + (n-1)*a的(n-1)次方,直到 a的 1次方 最后乘以 乘数
     *
     * @param a 阶乘数
     * @param b 乘数
     * @param n 次方
     * @return
     */
    private static long getEquation(int a, int b, int n) {
        long l = 0;
        for (int i = 1; i <= n; i++) {
            double pow;
            if (i == 1 || i == n) {
                pow = Math.pow(a, i);
            } else {
                int t = n - 1;
                pow = t * Math.pow(a, i);
            }
            l += pow;
        }
        return l * b;
    }

    /**
     * 获取字母数字
     * 将数字 转化为 字母+数字
     * 数字位最大支持 到 9999
     * 即 A0001-A9999 后，自动 切换至 B0001-B9999
     * 穷尽字母表后 最终数字可能为 ZZZZZ
     *
     * @param num
     * @return
     */
    public static String getLetterNum(int num) {
        int maxNum = 9999;
        return getLetterNum(num, maxNum, 0, getNumberLength(maxNum));
    }

    public static String getLetterNum(int num, int maxNum, int letterCount, int maxNumLength) {
        if (letterCount < 1) {
            letterCount = 1;
        }
        if (maxNumLength == 0) {
            maxNumLength = getNumberLength(maxNum);
        }

        if (letterCount > 5) {
            throw new NumberFormatException("For input : must be less than 5");
        }


        String s1 = "";

        //1.获取字母数量
        int lettersLength = letters.length;


        //int z = lettersLength * maxNum;
        int z = (int) getEquation(lettersLength, maxNum, 1);

        int zz = (int) getEquation(lettersLength, maxNum, 2);

        //double v1 = (Math.pow(lettersLength, 1) + lettersLength * 0) * maxNum;
        //double v2 = (Math.pow(lettersLength, 2) + lettersLength * 1) * maxNum;
        //
        //double v3 = ((Math.pow(lettersLength, 3) + lettersLength) + (Math.pow(lettersLength, 2) + lettersLength)) * maxNum;

        //int zzz = lettersLength * (lettersLength * lettersLength * maxNum + lettersLength * maxNum) + (lettersLength * lettersLength * maxNum + lettersLength * maxNum);
        int zzz = (int) getEquation(lettersLength, maxNum, 3);

        int zzzz = (int) getEquation(lettersLength, maxNum, 4);

        int zzzzz = (int) getEquation(lettersLength, maxNum, 5);

        //log.info("号码节点：{}\t{}\t{}\t{}\t{}\t", z, zz, zzz, zzzz, zzzzz);
        if (num <= z) {
            //获取倍数，可定位具体对应的字母
            int i = num / maxNum;
            int s = num % maxNum;
            /*
            最后一个数字
             */
            if (i == 0 && s == 0) {
                String letter = letters[lettersLength - 1];
                s1 = letter + maxNum;
            } else if (i < lettersLength) {
                if (s == 0) {
                    String letter = letters[i - 1];
                    s1 = letter + maxNum;
                } else if (s > 0 && s < (Math.pow(10, maxNumLength))) {
                    String letter = letters[i];
                    s1 = letter + frontCompWithZero(s, maxNumLength);
                } else {
                    String letter = letters[i];
                    s1 = letter + s;
                }
            } else if (i == lettersLength) {
                String letter = letters[lettersLength - 1];
                s1 = letter + maxNum;
            }
        } else {
            //两个字母+（01-99的数字）组成
            if (num <= zz) {
                //确定第一个字母
                String firstLetter = "";
                //去除不需要的部分
                int i1 = num - z;
                int i = i1 / z;
                if (i == lettersLength) {
                    firstLetter = String.valueOf(letters[lettersLength - 1]);
                } else if (i < lettersLength) {
                    firstLetter = String.valueOf(letters[i]);
                }
                int i2 = i1 - z * i;
                //确定第二个字母+数字
                String letterNum = getLetterNum(i2, maxNum, 0, maxNumLength);
                s1 = firstLetter + letterNum;

            } else {
                if (num <= zzz) {
                    //确定第一个字母
                    String firstLetter = "";
                    int i1 = num - zz;
                    int i = i1 / zz;
                    if (i == lettersLength) {
                        firstLetter = String.valueOf(letters[lettersLength - 1]);
                    } else if (i < lettersLength) {
                        firstLetter = String.valueOf(letters[i]);
                    }
                    //获取第二第三个
                    int i2 = i1 - zz * i + z;
                    String letterNum = getLetterNum(i2, maxNum, 0, maxNumLength);
                    s1 = firstLetter + letterNum;
                } else {
                    if (num <= zzzz) {
                        //确定第一个字母
                        String firstLetter = "";
                        int i1 = num - zzz;
                        int i = i1 / zzz;
                        if (i == lettersLength) {
                            firstLetter = String.valueOf(letters[lettersLength - 1]);
                        } else if (i < lettersLength) {
                            firstLetter = String.valueOf(letters[i]);
                        }
                        //获取第二第三个第四个
                        int i2 = i1 - zzz * i + zz;
                        String letterNum = getLetterNum(i2, maxNum, 0, maxNumLength);
                        s1 = firstLetter + letterNum;
                    } else {
                        if (num <= zzzzz) {
                            //确定第一个字母
                            String firstLetter = "";
                            int i1 = num - zzzz;
                            int i = i1 / zzzz;
                            if (i == lettersLength) {
                                firstLetter = String.valueOf(letters[lettersLength - 1]);
                            } else if (i < lettersLength) {
                                firstLetter = String.valueOf(letters[i]);
                            }
                            //获取第二第三个
                            int i2 = i1 - zzzz * i + zzz;
                            String letterNum = getLetterNum(i2, maxNum, 0, maxNumLength);
                            s1 = firstLetter + letterNum;
                        } else {
                            throw new NumberFormatException("The merchant numbers have been exhausted");
                        }
                    }
                }
            }
        }
        return s1;
    }

    /**
     * 获取数字长度
     * 包含负整数
     *
     * @param num
     * @return
     */
    public static int getNumberLength(int num) {
        return String.valueOf(Math.abs(num)).length();
    }

    /**
     * 获取正整数的位数
     * 可通过枚举实现 单位的转换
     * switch (unit) {
     * case 1:
     * // 个
     * break;
     * case 2:
     * //十
     * break;
     * case 3:
     * // 百
     * break;
     * case 4:
     * // 千
     * break;
     * case 5:
     * // 万
     * break;
     * case 6:
     * // 十万
     * break;
     * default:
     * // 未知单位
     * break;
     * }
     *
     * @param num
     * @return
     */
    public static int getNumberBits(Integer num) {
        if (num == null) {
            throw new IllegalArgumentException("num cannot be null");
        }
        if (num < 0) {
            throw new IllegalArgumentException("num cannot be lt 0");
        }
        int count = 0;
        while (num > 0) {
            num /= 10;
            count++;
        }
        return count;
    }

    /**
     * 获取 折扣 后的价格
     * 折后价
     *
     * @param amount
     * @param percent
     * @return
     */
    public static BigDecimal getDiscountAmount(BigDecimal amount, Integer percent) {
        Assert.isTrue(percent > 0, "percent must gt 0");
        return amount.multiply(BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100)));
    }


    /**
     * 提供精确的加法运算。
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static Double add(Number value1, Number value2) {
        if (value1 == null) {
            value1 = 0;
        }
        if (value2 == null) {
            value2 = 0;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
        BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的加法运算。
     *
     * @param numbers 加数集合
     * @return 加数集合的和
     */
    public static BigDecimal add(Number... numbers) {
        BigDecimal decimal = BigDecimal.ZERO;
        for (Number number : numbers) {
            if (number == null
                    || number.doubleValue() == 0) {
                continue;
            }
            decimal = decimal.add(new BigDecimal(Double.toString(number.doubleValue())));
        }
        return decimal;
    }

    /**
     * 提供精确的减法运算。
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(Number value1, Number value2) {
        if (value1 == null) {
            value1 = 0;
        }
        if (value2 == null) {
            value2 = 0;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
        BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static Double mul(Number value1, Number value2) {
        if (value1 == null) {
            value1 = 0;
        }
        if (value2 == null) {
            value2 = 0;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(value1.doubleValue()));
        BigDecimal b2 = new BigDecimal(Double.toString(value2.doubleValue()));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 两个参数的商
     */
    public static Double div(Double dividend, Double divisor) {
        return div(dividend, divisor, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static Double div(Double dividend, Double divisor, Integer scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(dividend));
        BigDecimal b2 = new BigDecimal(Double.toString(divisor));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param value 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static Double round(Double value, Integer scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(value));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static void main(String[] args) {
        ////这里的数后面加“D”是表明它是Double类型，否则相除的话取整，无法正常使用
        //System.out.println("百分数：" + getPercent(50D, -100D));

        //System.out.println(BigDecimal.valueOf(0).divide(BigDecimal.valueOf(100)));

        //System.out.println(fillZero(11));

        System.out.println(toUpperLetter(27));

        //System.out.println(getLetterNum(2476, 99, 1));
        //System.out.println(getLetterNum(59401, 99, 1, 0));
        //System.out.println(getLetterNum(1));
        //号码节点：216	5400	135000	3375000	84126168
        //for (int i = 1; i <= 5402; i++) {
        //    System.out.println(getLetterNum(i, 999, 1, 0));
        //}

        //int a = 24, b = 9, n = 5;
        //System.out.println(getEquation(a, b, n));

        //System.out.println(getAfterAmount(new BigDecimal(0.01),80));

        //BigDecimal ten = new BigDecimal(100.00);
        //BigDecimal bigDecimal = new BigDecimal(-500.00);

        //BigDecimal remainder = bigDecimal.remainder(ten);


        //System.out.println(remainder.compareTo(BigDecimal.ZERO) == 0);


        //System.out.println(add(BigDecimal.ONE, 100D, 50L));
        //System.System.out.println(getNumberLength(-1));
        //System.out.println(getNumberBits(500));

        //System.out.println(BigDecimal.valueOf(NumberUtils.div(Double.valueOf(5), Double.valueOf(10000), NumberUtils.getNumberLength(10000))));
    }

}
