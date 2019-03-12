package cn.jeremy.stock.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json工具类
 *
 * @author kugoufeng
 * @date 2017/12/21 下午 6:28
 */
public class JackSonTools
{

    /**
     * 日志对象
     */
    private static Logger logger = LogManager.getLogger(JackSonTools.class.getName());

    private static JackSonTools instance = new JackSonTools();

    private JackSonTools()
    {

    }

    public static JackSonTools getInstance()
    {
        return instance;
    }

    /**
     * json转对象
     *
     * @param json json字符串
     * @param c 对象类型
     * @return java.lang.Object
     */
    public Object josnToObject(String json, Class<?> c)
    {
        if (Util.isEmpty(json))
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Object obj = null;
        try
        {
            // 忽略不存在的字段
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            obj = mapper.readValue(json, c);
        }
        catch (Exception e)
        {
            logger.error("josnToObject error ,json:" + json + ",c:" + c.getName());
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * 对象转json 字符串
     *
     * @param obj 要转换的对象
     * @return java.lang.String
     */
    public String objectToJson(Object obj)
    {
        if (null == obj)
        {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        // 为了使JSON视觉上的可读性，增加一行如下代码
        // mapper.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
        // 配置mapper忽略空属性
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        String json = null;
        try
        {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            json = mapper.writeValueAsString(obj);
        }
        catch (Exception e)
        {
            logger.error("objectToJson error ,object：" + obj + ",message:" + e.getMessage());
            e.printStackTrace();
        }

        return json;
    }

}
