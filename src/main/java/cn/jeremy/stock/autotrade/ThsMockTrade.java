package cn.jeremy.stock.autotrade;

import static cn.jeremy.stock.constance.ParamConstance.BUY;

import cn.jeremy.stock.bean.AccountAsset;
import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.ShareHolder;
import cn.jeremy.stock.bean.Stock;
import cn.jeremy.stock.constance.ParamConstance;
import cn.jeremy.stock.tools.HttpTools;
import cn.jeremy.stock.tools.JackSonTools;
import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.StringTools;
import cn.jeremy.stock.tools.Util;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 同花顺模拟交易
 *
 * @author kugoufeng
 * @date 2017/12/21 下午 6:44
 */
public class ThsMockTrade implements Trade
{
    private static final Logger LOGGER = LogManager.getLogger(ThsMockTrade.class);

    /**
     * 请求头信息
     */
    private static final String MOCK_REQ_HEADER = AutoLogin.getThsTradeCookie();

    /**
     * 查询账户信息url
     */
    private static final String QUERY_ACCOUNT_URL = PropertiesTools.getProperty("queryAccount");

    /**
     * 买入股票url
     */
    private static final String BUY_STOCK_URL = PropertiesTools.getProperty("buyStock");

    /**
     * 查询股东账户url
     */
    private static final String QUERY_SHARE_HOLDER_ACCOUNT_URL = PropertiesTools.getProperty("queryShareHolderAccount");

    /**
     * 查询股票持仓url
     */
    private static final String QUERY_STOCK_POSITION_URL = PropertiesTools.getProperty("queryStockPosition");

    /**
     * 查询撤单url
     */
    private static final String QUERY_CANCEL_STOCKS_URL = PropertiesTools.getProperty("queryCancelStocks");

    /**
     * 查询股票价格url
     */
    private static final String QUERY_STOCK_PRICE_URL = PropertiesTools.getProperty("queryStockPrice");

    /**
     * 撤单url
     */
    private static final String CANCEL_ORDER_URL = PropertiesTools.getProperty("cancelOrder");

    /**
     * 撤单失败主题
     */
    private static final String CANCEL_ORDER_FAIL_SUBJECT = PropertiesTools.getProperty("cancelOrderFailSubject");

    /**
     * 查询买入价格失败主题
     */
    private static final String QUERY_BUY_PRICE_FAIL_SUBJECT = PropertiesTools.getProperty("queryBuyPriceFailSubject");

    /**
     * 查询股票卖出价格失败主题
     */
    private static final String QUERY_SELL_PRICE_FAIL_SUBJECT =
        PropertiesTools.getProperty("querySellPriceFailSubject");

    /**
     * 初始化账户股票持仓失败
     */
    private static final String INIT_STOCK_POSITION_MAP_FAIL_SUBJECT =
        PropertiesTools.getProperty("initStockPositionMapFailSubject");

    /**
     * 买入失败主题
     */
    private static final String BUY_FAIL_SUBJECT = PropertiesTools.getProperty("buyFailSubject");

    /**
     * 卖出失败主题
     */
    private static final String SELL_FAIL_SUBJECT = PropertiesTools.getProperty("sellFailSubject");

    /**
     * 查询撤单失败主题
     */
    private static final String QUERY_CANCEL_STOCKS_FAIL_SUBJECT =
        PropertiesTools.getProperty("queryCancelStocksFailSubject");

    /**
     * 初始化股东账户失败主题
     */
    private static final String INIT_SHARE_HOLDER_FAIL_SUBJECT =
        PropertiesTools.getProperty("initShareHolderFailSubject");

    /**
     * 查询账户资金失败主题
     */
    private static final String GET_ACCOUNT_ASSET_FAIL_SUBJECT =
        PropertiesTools.getProperty("getAccountAssetFailSubject");

    /**
     * 买入汉字
     */
    private static final String BUY_CHINESE = PropertiesTools.getProperty("buyChinese");

    /**
     * 卖出汉字
     */
    private static final String SELL_CHINESE = PropertiesTools.getProperty("sellChinese");

    /**
     * 撤单后买入汉字
     */
    private static final String CANCEL_AND_BUY_CHINESE = PropertiesTools.getProperty("cancelAndBuyChinese");

    /**
     * 撤单后卖出汉字
     */
    private static final String CANCEL_AND_SELL_CHINESE = PropertiesTools.getProperty("cancelAndSellChinese");

    /**
     * 股东账户
     */
    private static volatile Map<String, ShareHolder> shareHolderMap = new HashMap<>();

    /**
     * 实际持仓信息
     */
    private static volatile Map<String, Stock> stockPositionMap = new HashMap<>();

    /**
     * 买入
     */
    private static final String TYPE_BUY = "cmd_wt_mairu";

    /**
     * 卖出
     */
    private static final String TYPE_SELL = "cmd_wt_maichu";

    /**
     * 撤单
     */
    private static final String TYPE_CANCEL = "cmd_qu_chedan";

    /**
     * 手续费
     */
    private static final double POUNDAG = NumberUtils.toDouble(PropertiesTools.getProperty("poundag"), 1.003);

    /**
     * 资金账户总金额平分的份数
     */
    private static int TOTAL_BALANCE_VALVE = AutoTrade.totalBalanceValve;

    private static final Trade INSTANCE = new ThsMockTrade();

    private ThsMockTrade()
    {

    }

    public static Trade getInstance()
    {
        return INSTANCE;
    }

    /**
     * 设置股份账户
     *
     * @param stockNum 股票代码
     * @param postParams 封装请求报文的map
     */
    private void setShareHolder(String stockNum, Map<String, String> postParams)
    {
        ShareHolder shareHolder = null;
        if (stockNum.startsWith("6"))
        {
            shareHolder = getShareHolderMap().get("沪A");
        }
        else
        {
            shareHolder = getShareHolderMap().get("深A");
        }
        postParams.put("mkcode", shareHolder.getMkCode());
        postParams.put("gdzh", shareHolder.getCode());

    }

    /**
     * 获取股东账户
     *
     * @return java.util.Map<java.lang.String                               ,                               cn.jeremy
      * .stock.bean.ShareHolder>
     */
    private Map<String, ShareHolder> getShareHolderMap()
    {
        if (Util.isEmpty(shareHolderMap))
        {
            initShareHolderMap();
        }
        return shareHolderMap;
    }

    /**
     * 初始化股东账户
     */
    private void initShareHolderMap()
    {
        shareHolderMap.clear();
        HttpResult result =
            HttpTools.getInstance()
                .sendRequestByPost(QUERY_SHARE_HOLDER_ACCOUNT_URL,
                    null,
                    MOCK_REQ_HEADER,
                    INIT_SHARE_HOLDER_FAIL_SUBJECT, Boolean.TRUE);
        if (result.getRespCode() == HttpStatus.SC_OK)
        {
            Document document = Jsoup.parse(result.getResponseBody());
            // 设置股东账户信息
            Elements gddm = document.select("#gddm option");
            for (Element element : gddm)
            {
                String text = element.text();
                String[] split = text.split(" ");
                shareHolderMap.put(split[0], new ShareHolder(split[0], split[1], element.attr("value")));
            }
        }
    }

    @Override
    public AccountAsset getAccountAsset()
    {
        HttpResult result = HttpTools.getInstance()
            .sendRequestByPost(QUERY_ACCOUNT_URL, null, MOCK_REQ_HEADER, GET_ACCOUNT_ASSET_FAIL_SUBJECT, Boolean.TRUE);
        if (result.getRespCode() == HttpStatus.SC_OK)
        {
            AccountAsset accountAsset = new AccountAsset();
            Document document = Jsoup.parse(result.getResponseBody());
            accountAsset.setAvailableBalance(StringTools.yuanToFen(document.select("#kyye").text()));
            accountAsset.setFundBalance(StringTools.yuanToFen(document.select("#zjye").text()));
            accountAsset.setBlockedBalance(StringTools.yuanToFen(document.select("#djje").text()));
            accountAsset.setDesirableBalance(StringTools.yuanToFen(document.select("#kqje").text()));
            accountAsset.setTotalBalance(StringTools.yuanToFen(document.select("#zzc").text()) / TOTAL_BALANCE_VALVE);
            accountAsset.setMarketValueOfSecurities(StringTools.yuanToFen(document.select("#gpsz").text()));
            return accountAsset;
        }

        return null;
    }

    @Override
    public HttpResult buyStock(Stock stock)
    {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("type", TYPE_BUY);
        setPostParams(postParams, stock);
        return HttpTools.getInstance()
            .sendRequestByPost(BUY_STOCK_URL, postParams, MOCK_REQ_HEADER, BUY_FAIL_SUBJECT, Boolean.FALSE);
    }

    @Override
    public HttpResult sellStock(Stock stock)
    {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("type", TYPE_SELL);
        setPostParams(postParams, stock);
        return HttpTools.getInstance()
            .sendRequestByPost(BUY_STOCK_URL, postParams, MOCK_REQ_HEADER, SELL_FAIL_SUBJECT, Boolean.FALSE);
    }

    /**
     * 设置买卖股票时的请求参数
     *
     * @param postParams 封装请求参数的map
     * @param stock 要交易的股票
     */
    private void setPostParams(Map<String, String> postParams, Stock stock)
    {
        String stockNum = stock.getStockNum();
        postParams.put("stockcode", stockNum);
        DecimalFormat df = new DecimalFormat("######0.00");
        postParams.put("price", df.format(stock.getStockPrice() / 100d));
        postParams.put("amount", String.valueOf(stock.getStockPositionNum()));
        setShareHolder(stockNum, postParams);
    }

    @Override
    public void tradeStocks(Map<String, List<Stock>> tradeStocks)
    {
        List<Stock> buyStocks = tradeStocks.get(BUY);
        List<Stock> sellStocks = tradeStocks.get(ParamConstance.SELL);
        Map<String, String> result = new HashMap<>(16);
        //买入股票
        if (Util.isNotEmpty(buyStocks))
        {
            initStockPositionMap();
            for (Stock stock : buyStocks)
            {
                AccountAsset accountAsset = getAccountAsset();
                setBuyNumAndPrice(stock, accountAsset);
                HttpResult httpResult = null;
                if (stock.getStockPositionNum() > 0)
                {
                    httpResult = buyStock(stock);
                }
                //记录交易数据
                HandleStockData.getInstance()
                    .recordStockTradeData(BUY_CHINESE, stock, accountAsset, stockPositionMap, httpResult);
            }
        }
        //卖出股票
        if (Util.isNotEmpty(sellStocks))
        {
            initStockPositionMap();
            for (Stock stock : sellStocks)
            {
                AccountAsset accountAsset = getAccountAsset();
                setSellNumAndPrice(stock, accountAsset);
                HttpResult httpResult = null;
                if (stock.getStockPositionNum() > 0)
                {
                    httpResult = sellStock(stock);
                }
                //记录交易数据
                HandleStockData.getInstance()
                    .recordStockTradeData(SELL_CHINESE, stock, accountAsset, stockPositionMap, httpResult);
            }
        }
    }

    /**
     * 设置股票当前买入的数量以及价格
     *
     * @param stock 交易的股票
     * @param accountAsset 账户资金
     */
    private void setBuyNumAndPrice(Stock stock, AccountAsset accountAsset)
    {
        int amount =
            (int)Math.floor(
                accountAsset.getTotalBalance() * 1.0 / stock.getStockPrice() * (stock.getStockPer() / 10000d) / 100) *
                100;
        if (amount == 0)
        {
            if (accountAsset.getAvailableBalance() > 100 * stock.getStockPrice() * POUNDAG)
            {
                amount = 100;
            }
        }
        else
        {
            while (amount > 0)
            {
                if (accountAsset.getAvailableBalance() > amount * stock.getStockPrice() * POUNDAG)
                {
                    break;
                }
                amount -= 100;
            }
        }
        if (amount > 0)
        {
            Stock stock2 = stockPositionMap.get(stock.getStockName());
            if (Util.isNotEmpty(stock2))
            {
                amount = amount - stock2.getStockPositionNum();
            }
            if (amount > 0)
            {
                stock.setStockPositionNum(amount);
                int price = queryStockBuyPrice(stock.getStockNum());
                if (price > 0)
                {
                    stock.setStockPrice(price);
                }
            }
        }
    }

    /**
     * 设置股票卖出的价格以及数量
     *
     * @param stock 要交易的股票
     * @param accountAsset 账户资金
     */
    private void setSellNumAndPrice(Stock stock, AccountAsset accountAsset)
    {
        Stock stock2 = stockPositionMap.get(stock.getStockName());
        if (Util.isNotEmpty(stock2) && stock2.getStockPositionNum() > 0)
        {
            int sellAmount = 0;
            int amount =
                (int)Math.floor(
                    accountAsset.getTotalBalance() * 1.0 / stock.getStockPrice() * (stock.getStockPer() / 10000d) /
                        100) *
                    100;
            if (amount == 0)
            {
                sellAmount = stock2.getStockPositionNum();
            }
            else
            {
                sellAmount = stock2.getStockPositionNum() - amount;
            }
            if (sellAmount <= 0)
            {
                sellAmount = 100;
            }
            stock.setStockPositionNum(sellAmount);
            int price = queryStockSellPrice(stock.getStockNum());
            if (price > 0)
            {
                stock.setStockPrice(price);
            }
        }
    }

    /**
     * 初始化实际股票持仓
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initStockPositionMap()
    {
        stockPositionMap.clear();
        HttpResult result = HttpTools.getInstance()
            .sendRequestByPost(QUERY_STOCK_POSITION_URL,
                null,
                MOCK_REQ_HEADER,
                INIT_STOCK_POSITION_MAP_FAIL_SUBJECT, Boolean.TRUE);
        if (result.getRespCode() == HttpStatus.SC_OK)
        {
            try
            {
                Map<String, Object> map =
                    (Map<String, Object>)JackSonTools.getInstance()
                        .josnToObject(result.getResponseBody(), Map.class);
                map = (Map<String, Object>)map.get("result");
                List<Map<String, String>> list = (List<Map<String, String>>)map.get("list");
                for (Map<String, String> map2 : list)
                {
                    stockPositionMap.put(map2.get("d_2103"),
                        new Stock(null, 0, map2.get("d_2103"), map2.get("d_2102"), 0, 0, 0,
                            Integer.parseInt(map2.get("d_2121"))));
                }
            }
            catch (Exception e)
            {
                LOGGER.error("result:{}, error:{}", result, e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public int queryStockBuyPrice(String stockCode)
    {
        String price = queryStockData(stockCode, TYPE_BUY, "mcjw1");
        return StringTools.yuanToFen(price);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int queryStockSellPrice(String stockCode)
    {
        String price = queryStockData(stockCode, TYPE_BUY, "mrjw1");
        return StringTools.yuanToFen(price);
    }

    @Override
    public int queryStockOpenPrice(String stockCode)
    {
        String price = queryStockData(stockCode, TYPE_BUY, "st_open");
        return StringTools.yuanToFen(price);
    }

    public String queryStockData(String stockCode, String type, String dataKey)
    {
        Map<String, String> postParams = new HashMap<>(16);
        postParams.put("type", type);
        postParams.put("stockcode", stockCode);
        HttpResult result =
            HttpTools.getInstance()
                .sendRequestByPost(QUERY_STOCK_PRICE_URL,
                    postParams,
                    MOCK_REQ_HEADER,
                    QUERY_SELL_PRICE_FAIL_SUBJECT, Boolean.FALSE);
        if (result.getRespCode() == HttpStatus.SC_OK)
        {
            try
            {
                Map<String, Object> map =
                    (Map<String, Object>)JackSonTools.getInstance()
                        .josnToObject(result.getResponseBody(), Map.class);
                map = (Map<String, Object>)map.get("result");
                map = (Map<String, Object>)map.get("data");
                return (String)map.get(dataKey);
            }
            catch (Exception e)
            {
                LOGGER.error("result:{}, error:{}", result, e);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpResult cancelOrder(String orderId, String... args)
    {
        Map<String, String> postParams = new HashMap<>(16);
        postParams.put("htbh", orderId);
        postParams.put("wtrq", args[0]);
        HttpResult result =
            HttpTools.getInstance()
                .sendRequestByPost(CANCEL_ORDER_URL,
                    postParams,
                    MOCK_REQ_HEADER,
                    CANCEL_ORDER_FAIL_SUBJECT,
                    Boolean.FALSE);
        if (result.getRespCode() == HttpStatus.SC_OK)
        {
            try
            {
                Map<String, Object> map = (Map<String, Object>)JackSonTools.getInstance()
                    .josnToObject(result.getResponseBody(), Map.class);
                int errorcode = (int)map.get("errorcode");
                String message = (String)map.get("errormsg");
                result.setRespCode(errorcode);
                result.setResponseBody(message);
                return result;
            }
            catch (Exception e)
            {
                LOGGER.error("result:{}, error:{}", result, e);
            }
        }
        result.setRespCode(-99);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void queryAndCancelOrder()
    {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("type", TYPE_CANCEL);
        postParams.put("updateClass", "qryChedan");
        HttpResult result =
            HttpTools.getInstance()
                .sendRequestByPost(QUERY_CANCEL_STOCKS_URL,
                    postParams,
                    MOCK_REQ_HEADER,
                    QUERY_CANCEL_STOCKS_FAIL_SUBJECT, Boolean.FALSE);
        if (result.getRespCode() != HttpStatus.SC_OK)
        {

            return;
        }
        try
        {
            Map<String, Object> map =
                (Map<String, Object>)JackSonTools.getInstance()
                    .josnToObject(result.getResponseBody(), Map.class);
            map = (Map<String, Object>)map.get("result");
            map = (Map<String, Object>)map.get("qryChedan");
            int errorCode = (int)map.get("errorcode");
            if (errorCode != 0)
            {
                return;
            }
            map = (Map<String, Object>)map.get("result");
            List<Map<String, String>> list = (List<Map<String, String>>)map.get("list");
            for (Map<String, String> map2 : list)
            {

                Stock stock = new Stock(null, 0, map2.get("d_2103"), map2.get("d_2102"),
                    StringTools.yuanToFen(map2.get("d_2127")), 0, 0, NumberUtils.toInt(map2.get("d_2126")));
                //不属于当前用户的股票排除掉
                if (!HandleStockData.getInstance().getStockPosition().containsKey(stock.getStockName()))
                {
                    String tradeStockName = HandleStockData.getInstance().getTradeStockName();
                    if (Util.isEmpty(tradeStockName) || tradeStockName.indexOf(stock.getStockName()) == -1)
                    {
                        continue;
                    }
                }
                AccountAsset accountAsset = getAccountAsset();
                String tradeFlag = map2.get("d_2109");

                int price = 0;
                //新的买入价格不比原来高，或者余额不足时，不撤单
                if (BUY_CHINESE.equals(tradeFlag))
                {
                    price = queryStockBuyPrice(stock.getStockNum());
                    if (price == 0 || (price - stock.getStockPrice()) <= 0 ||
                        (price - stock.getStockPrice()) * HandleStockData.TRADE_VALVE >
                            accountAsset.getAvailableBalance())
                    {
                        continue;
                    }
                }
                //新的卖出价格不比原来低
                else if (SELL_CHINESE.equals(tradeFlag))
                {
                    price = queryStockSellPrice(stock.getStockNum());
                    if (price == 0 || (price - stock.getStockPrice()) >= 0)
                    {
                        continue;
                    }
                }
                //执行撤单
                HttpResult httpResult = cancelOrder(map2.get("d_2135"), map2.get("d_2139"));
                initStockPositionMap();
                //记录撤单信息
                HandleStockData.getInstance()
                    .recordStockCancelData(stock, accountAsset, stockPositionMap, httpResult);
                if (httpResult.getRespCode() == 0)
                {
                    if (BUY_CHINESE.equals(tradeFlag))
                    {
                        stock.setStockPrice(price);
                        HttpResult buyResult = buyStock(stock);
                        HandleStockData.getInstance().recordStockTradeData(CANCEL_AND_BUY_CHINESE,
                            stock,
                            accountAsset,
                            stockPositionMap,
                            buyResult);
                    }
                    else if (SELL_CHINESE.equals(tradeFlag))
                    {
                        stock.setStockPrice(price);
                        HttpResult sellResult = sellStock(stock);
                        HandleStockData.getInstance().recordStockTradeData(CANCEL_AND_SELL_CHINESE,
                            stock,
                            accountAsset,
                            stockPositionMap,
                            sellResult);
                    }
                }
                else
                {
                    SendMail.sendMail(CANCEL_ORDER_FAIL_SUBJECT, stock.toString(), null);
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("result:{}, error:{}", result, e);
        }

    }

    @Override
    public String getRequestCookie()
    {
        return MOCK_REQ_HEADER;
    }
}
