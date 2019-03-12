package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.RequestElement;
import cn.jeremy.stock.tools.FileTools;
import cn.jeremy.stock.tools.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 将http请求配置文件的内容放进map中，并提供接口用户访问
 *
 * @author fengjiangtao
 * @date 2018/1/10 上午 11:33
 */
public class LoadRequestElement
{
    private static volatile Map<String, RequestElement> requestElementMap = new HashMap<>();

    /**
     * 将配置文件的内容读取到hash表中
     *
     * @author fengjiangtao
     */
    private static void initMap()
    {
        if (Util.isNotEmpty(requestElementMap))
        {
            return;
        }
        InputStream resourceAsStream = LoadRequestElement.class.getResourceAsStream("/requestData.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Document document = Jsoup.parse(sb.toString());
        Elements requestElements = document.select("request > *");
        if (Util.isNotEmpty(requestElements))
        {
            for (Element element : requestElements)
            {
                RequestElement requestElement = new RequestElement();
                requestElement.setUrl(element.select("url").text());
                Elements paramElements = element.select("params > *");
                Map<String, String> params = new HashMap<>(16);
                for (Element paramElement : paramElements)
                {
                    params.put(paramElement.tagName(), paramElement.text());
                }
                requestElement.setRequestParams(params);
                Elements removeCookieElements = element.select("removeCookieKeys > *");
                List<String> removeCookies = new ArrayList<>();
                for (Element removeCookieElement : removeCookieElements)
                {
                    removeCookies.add(removeCookieElement.text());
                }
                requestElement.setRemoveCookieKeys(removeCookies);
                requestElement.setHeaders(element.select("headers").text());
                requestElement.setMailSubject(element.select("mailSubject").text());
                requestElement.setFinish(Boolean.parseBoolean(element.select("isFinish").text()));
                requestElement.setPost(Boolean.parseBoolean(element.select("isPost").text()));
                requestElementMap.put(element.tagName(), requestElement);
            }
        }
    }

    /**
     * 根据key值获取RequestElement对象
     *
     * @param key RequestElement对象的key
     * @return cn.jeremy.stock.bean.RequestElement
     * @author fengjiangtao
     */
    public static RequestElement getRequestElement(String key)
    {
        if (Util.isEmpty(requestElementMap))
        {
            initMap();
        }
        return requestElementMap.get(key.toLowerCase());
    }
}
