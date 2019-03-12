package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.Stock;
import cn.jeremy.stock.constance.ParamConstance;
import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.StringTools;
import cn.jeremy.stock.tools.Util;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 获取jd股票牛人股票持仓
 *
 * @author kugoufeng
 * @date 2017/12/22 上午 11:12
 */
public class JDStockPosition extends TradeStockPosition
{
    /**
     * 实例化对象
     */
    private static final TradeStockPosition INSTANCE = new JDStockPosition();

    static
    {
        LOGGER = LogManager.getLogger(JDStockPosition.class);
        requestMap.put(ParamConstance.packageId, AutoTrade.packageId);
    }

    public static TradeStockPosition getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Map<String, Stock> parseXML(String respXML)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("enter, respXML:{}", respXML);
        }
        if (Util.isEmpty(respXML))
        {
            LOGGER.error("respXML is empty");
            return null;
        }
        Document document = Jsoup.parse(respXML);
        Elements tableElements = document.select("table");
        if (Util.isEmpty(tableElements))
        {
            return null;
        }
        Elements trElements = tableElements.get(0).select("tr");
        if (Util.isEmpty(trElements))
        {
            return null;
        }
        Map<String, Stock> stockMap = new HashMap<>(16);
        for (Element element : trElements)
        {
            Stock stock = buildStock(element);
            if (Util.isEmpty(stock))
            {
                continue;
            }
            if (Util.isNotEmpty(stock.getStockName()))
            {
                stockMap.put(stock.getStockName(), stock);
            }
            else
            {
                stockMap.put(stock.getPlateName(), stock);
            }
        }

        //判断得到的数据是否合法，获取不到现金的hash值，说明数据不合法
        if (Util.isEmpty(stockMap.get("现金")))
        {
            HandleStockData.getInstance().recordErrorData(respXML);
            return null;
        }

        if (LOGGER.isDebugEnabled())

        {
            LOGGER.debug("exit, stockMap:{}", stockMap);
        }

        return stockMap;
    }

    /**
     * 构建股票对象
     *
     * @param element 包含股票对象信息的xml文件
     * @return cn.jeremy.stock.bean.Stock
     */
    private Stock buildStock(Element element)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("enter, element:{}", element);
        }

        if (Util.isEmpty(element))
        {
            LOGGER.error("element is empty,element:{}", element);
            return null;
        }
        Elements tdElements = element.select("td");
        if (Util.isEmpty(tdElements))
        {
            LOGGER.error("tdElements is empty,element{}", element);
            return null;
        }
        Stock stock = null;
        if (tdElements.size() == 3)
        {
            stock = new Stock();
            Elements plateElements = tdElements.get(0).select("div");
            if (Util.isNotEmpty(plateElements))
            {
                stock.setPlateName(plateElements.get(0).text());
                String platePer = plateElements.get(1).text();
                stock.setPlatePer(StringTools.yuanToFen(platePer.substring(0, platePer.length() - 1)));
            }
            Elements stockElements = tdElements.get(1).select("div");
            if (Util.isNotEmpty(stockElements))
            {
                stock.setStockName(stockElements.get(0).text());
                stock.setStockNum(stockElements.get(1).text());
            }
            String stockPer = tdElements.get(2).text();
            stock.setStockPer(StringTools.yuanToFen(stockPer.substring(0, stockPer.length() - 1)));
        }
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("exist, stock:{}", stock);
        }
        return stock;
    }

}
