package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.Stock;
import cn.jeremy.stock.tools.HttpTools;
import cn.jeremy.stock.tools.PropertiesTools;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.logging.log4j.Logger;

/**
 * 追踪牛人的持仓账户
 *
 * @author kugoufeng
 * @date 2017/12/22 上午 11:02
 */
public abstract class TradeStockPosition
{
    protected static Logger LOGGER;

    /**
     * 请求地址
     */
    private static final String url = PropertiesTools.getProperty("url");

    /**
     * 请求头
     */
    private static final String reqHeader = PropertiesTools.getProperty("reqHeader");

    /**
     * 封装请求条件
     */
    protected static Map<String, String> requestMap = new HashMap<>(16);

    /**
     * 获取牛人持仓失败主题
     */
    private static final String GET_STOCK_POSITION_FAIL_SUBJECT =
        PropertiesTools.getProperty("getStockPositionFailSubject");

    /**
     * 获得牛人的持仓股票
     *
     * @return java.util.Map<java.lang.String,cn.jeremy.stock.bean.Stock>
     */
    public Map<String, Stock> getStockPosition()
    {
        HttpResult result = HttpTools.getInstance().sendRequestByPost(url,
            requestMap,
            reqHeader, GET_STOCK_POSITION_FAIL_SUBJECT, Boolean.TRUE);
        if (result.getRespCode() == HttpStatus.SC_OK)
        {
            return parseXML(result.getResponseBody());
        }
        else
        {
            LOGGER.error("url:{},requestMap:{},reqHeader:{},result:{}", url, requestMap, reqHeader, result);
        }

        return null;
    }

    /**
     * 解析网页获取牛人持仓股票信息
     *
     * @param respXML 要解析的网页内容
     * @return java.util.Map<java.lang.String,cn.jeremy.stock.bean.Stock>
     */
    public abstract Map<String, Stock> parseXML(String respXML);

}
