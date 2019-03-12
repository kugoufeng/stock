package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.RequestElement;
import cn.jeremy.stock.tools.HttpTools;
import cn.jeremy.stock.tools.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动登录
 *
 * @author fengjiangtao
 * @date 2018/1/7 下午 9:42
 */
public class AutoLogin
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoLogin.class);

    /**
     * 获取同花顺模拟交易的登录cookie
     *
     * @return java.lang.String
     * @author fengjiangtao
     */
    public static String getThsTradeCookie()
    {
        String cookie = HttpTools.getInstance().getCookie("accountLoginThs");
        String baiduCountCookie = HttpTools.getInstance().getCookie("baiduCount");
        if (Util.isNotEmpty(baiduCountCookie))
        {
            String[] values = baiduCountCookie.split("[|]");
            cookie = cookie.concat("Hm_lvt_" + values[0] + "=" + values[1] + "; ");
            cookie = cookie.concat("Hm_lpvt_" + values[0] + "=" + values[1] + "; ");
        }
        RequestElement autoLoginThs = LoadRequestElement.getRequestElement("autoLoginThs");
        autoLoginThs.setHeaders("Cookie:".concat(cookie));
        String cookie2 = HttpTools.getInstance().getCookie(autoLoginThs);
        cookie = cookie.concat(cookie2);
        autoLoginThs.setHeaders("Cookie:".concat(cookie));
        autoLoginThs(autoLoginThs);
        return autoLoginThs.getHeaders();
    }

    /**
     * 登录超时后，自动登录
     *
     * @param autoLoginThs request 请求元素
     * @author fengjiangtao
     */
    public static void autoLoginThs(RequestElement autoLoginThs)
    {
        HttpResult httpResult = HttpTools.getInstance().sendHttpRequest(autoLoginThs);
        String domain = "http://mncg.10jqka.com.cn";
        String url = domain.concat(httpResult.getRedirectLocation());
        RequestElement requestElement = new RequestElement();
        requestElement.setUrl(url);
        requestElement.setHeaders(autoLoginThs.getHeaders());
        HttpTools.getInstance().sendHttpRequest(requestElement);
    }
}
