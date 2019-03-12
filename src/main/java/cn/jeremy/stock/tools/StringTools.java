package cn.jeremy.stock.tools;

import static java.util.regex.Pattern.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串常用操作工具类
 *
 * @author kugoufeng
 * @date 2017/12/20 下午 9:46
 */
public class StringTools
{
    /**
     * 私有化构造方法
     */
    private StringTools()
    {
    }

    /**
     * 允许null对象的trim方法
     *
     * @param str 给定的对象
     * @return java.lang.String
     */
    public static String trim(String str)
    {
        return str == null ? null : str.trim();
    }

    /**
     * 返回非空字符串
     *
     * @param o 输入对象
     * @return java.lang.String
     */
    public static String nvl(Object o)
    {
        return (null == o) ? "" : o.toString().trim();
    }

    /**
     * 若null对象或是空白字符，返回默认值，否则其trim结果
     *
     * @param str 给定的对象
     * @param def 给定的默认值
     * @return java.lang.String
     */
    public static String getTrim(String str, String def)
    {
        if (str == null)
        {
            return def;
        }

        String t = str.trim();
        return (t.length() == 0) ? def : t;
    }

    /**
     * 判断字符串是否与数组中的某个参数相等
     *
     * @param base 给定字符串
     * @param matches 给定数组
     * @return boolean
     */
    public static boolean matches(String base, String[] matches)
    {
        if (null == matches || 0 == matches.length)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < matches.length; i++)
            {
                if (matches[i].equals(base))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断两字符串是否相等
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return boolean
     */
    public static boolean isEq(String str1, String str2)
    {
        if (str1 == null)
        {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    /**
     * 元转换为分
     *
     * @param yuan 转换的金额
     * @author fengjiangtao
     */
    public static int yuanToFen(String yuan)
    {
        if (Util.isEmpty(yuan))
        {
            return 0;
        }
        yuan = yuan.trim();
        int indexOf = yuan.indexOf(".");
        if (indexOf == -1)
        {
            return Integer.parseInt(yuan) * 100;
        }
        int fen = Integer.parseInt(yuan.replace(".", ""));
        if ((yuan.length() - indexOf) == 4)
        {
            return fen / 10;
        }
        if ((yuan.length() - indexOf) == 3)
        {
            return fen;
        }
        if ((yuan.length() - indexOf) == 2)
        {
            return fen * 10;
        }
        if ((yuan.length() - indexOf) == 1)
        {
            return fen * 100;
        }

        return fen;
    }

    /**
     * unicode转中文
     *
     * @param str 转换的字符串
     * @return java.lang.String
     */
    public static String unicodeToString(String str)
    {
        if (Util.isEmpty(str))
        {
            return str;
        }
        Pattern pattern = compile("(\\\\u(\\p{XDigit}{4}))");

        Matcher matcher = pattern.matcher(str);

        char ch;

        while (matcher.find())
        {

            ch = (char)Integer.parseInt(matcher.group(2), 16);

            str = str.replace(matcher.group(1), ch + "");

        }

        return str;

    }

    /**
     * 判断字符串是否是中文
     *
     * @param str 要判断的字符串
     * @return boolean
     * @author fengjiangtao
     */
    public static boolean isChinese(String str)
    {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
