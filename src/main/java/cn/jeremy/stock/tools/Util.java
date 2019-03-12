package cn.jeremy.stock.tools;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 提供常用的操作类
 *
 * @author kugoufeng
 * @date 2017/12/20 下午 9:44
 */
public class Util
{
    private static Logger LOGGER = LogManager.getLogger(Util.class);

    /**
     * 私有化构造方法
     */
    private Util()
    {
    }

    /**
     * 给字符串去掉空格
     *
     * @param arg 处理的字符串
     * @return java.lang.String
     */
    public static String trim(String arg)
    {
        if (null == arg)
        {
            return "";
        }
        else
        {
            return arg.trim();
        }
    }

    /**
     * 检查字符串是否为空
     *
     * @param str 指定的字符串
     * @return boolean
     */
    public static boolean isEmpty(String str)
    {
        return null == str || 0 == str.trim().length();
    }

    /**
     * 判断指定的字符是否为空
     *
     * @param str 指定的字符
     * @return boolean
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * 判断指定的对象是否为空
     *
     * @param obj 指定的对象
     * @return boolean
     */
    public static boolean isEmpty(Object obj)
    {
        return null == obj;
    }

    /**
     * 判断指定的对象是否为空
     *
     * @param obj 指定的对象
     * @return boolean
     */
    public static boolean isNotEmpty(Object obj)
    {
        return !isEmpty(obj);
    }

    /**
     * 判断指定的map是否为空
     *
     * @param map 指定的map
     * @return boolean
     */
    public static boolean isEmpty(Map map)
    {
        return (null == map) || (map.isEmpty());
    }

    /**
     * 判断指定的map是否为空
     *
     * @param map 指定的map
     * @return boolean
     */
    public static boolean isNotEmpty(Map map)
    {
        return !isEmpty(map);
    }

    /**
     * 判断指定的字符串数组是否为空
     *
     * @param strArr 指定的字符串数组
     * @return boolean
     */
    public static boolean isEmpty(String[] strArr)
    {
        return (null == strArr) || (strArr.length < 1);
    }

    /**
     * 判断指定的字符串数组是否为空
     *
     * @param strArr 字符串数组
     * @return boolean
     */
    public static boolean isNotEmpty(String[] strArr)
    {
        return !isEmpty(strArr);
    }

    /**
     * 判断指定的对象数组是否为空
     *
     * @param objArr 对象数组
     * @return boolean
     */
    public static boolean isEmpty(Object[] objArr)
    {
        return (null == objArr) || (objArr.length < 1);
    }

    /**
     * 判断指定的对象数组是否为空
     *
     * @param objArr 指定的对象数组
     * @return boolean
     */
    public static boolean isNotEmpty(Object[] objArr)
    {
        return !isEmpty(objArr);
    }

    /**
     * 判断指定的对象列表是否为空
     *
     * @param lst 指定的对象列表
     * @return boolean
     */
    public static boolean isEmpty(List<? extends Object> lst)
    {
        return (null == lst) || (lst.isEmpty());
    }

    /**
     * 判断指定的对象列表是否为空
     *
     * @param lst 指定的对象列表
     * @return boolean
     */
    public static boolean isNotEmpty(List<? extends Object> lst)
    {
        return !isEmpty(lst);
    }

    /**
     * 判断指定的对象集合是否为空
     *
     * @param set 指定的对象列表
     * @return boolean
     */
    public static boolean isEmpty(Set<Object> set)
    {
        return (null == set) || (set.isEmpty());
    }

    /**
     * 判断指定的对象集合是否为空
     *
     * @param set 指定的对象列表
     * @return boolean
     */
    public static boolean isNotEmpty(Set<Object> set)
    {
        return !isEmpty(set);
    }

    /**
     * 返回对象的字符形式 如果对象为null，则返回""
     *
     * @param o 输入对象
     * @return string
     */
    public static String nvl(Object o)
    {
        return (null == o) ? "" : o.toString().trim();
    }

}
