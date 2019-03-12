package cn.jeremy.stock.tools;

import cn.jeremy.stock.exception.ExitException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 配置加载项
 *
 * @author fengjiangtao
 * @data 2017年11月27日
 */
public class PropertiesTools
{
    private static final Logger LOGGER = LogManager.getLogger(PropertiesTools.class);

    private static Properties p = new Properties();

    /**
     * 读取properties配置文件信息
     */
    static
    {
        try
        {
            LOGGER.info("start init properties");
            p.load(PropertiesTools.class.getClassLoader().getResourceAsStream("commonConfigure.properties"));
        }
        catch (IOException e)
        {
            throw new ExitException(-1, "exit program", e);
        }
    }

    /**
     * 根据key得到value的值
     */
    public static String getProperty(String key)
    {
        return p.getProperty(key);
    }

    /**
     * 根据key得到value的值，并将value转换为int类型
     *
     * @param key 要获取的值的key
     * @return int
     */
    public static int getPropertyInt(String key)
    {
        return NumberUtils.toInt(p.getProperty(key));
    }

    /**
     * 根据key得到value的值，并将value转换为int类型，转换失败给默认值
     *
     * @param key 要获取的值的key
     * @param def 默认值
     * @return int
     */
    public static int getPropertyInt(String key, int def)
    {
        return NumberUtils.toInt(p.getProperty(key), def);
    }

    /**
     * 根据key得到value的值
     */
    public static String getProperty(String key, String defValue)
    {
        if (Util.isEmpty(p.getProperty(key)))
        {
            return defValue;
        }

        return p.getProperty(key);
    }
}