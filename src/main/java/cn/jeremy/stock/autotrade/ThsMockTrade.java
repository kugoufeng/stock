package cn.jeremy.stock.autotrade;

import static cn.jeremy.stock.constance.ParamConstance.BUY;

import cn.jeremy.stock.bean.AccountAsset;
import cn.jeremy.stock.bean.BaseStockData;
import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.ShareHolder;
import cn.jeremy.stock.bean.Stock;
import cn.jeremy.stock.bean.StockCloseData;
import cn.jeremy.stock.constance.ParamConstance;
import cn.jeremy.stock.tools.DateTools;
import cn.jeremy.stock.tools.HttpTools;
import cn.jeremy.stock.tools.JackSonTools;
import cn.jeremy.stock.tools.MysqlTools;
import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.StringTools;
import cn.jeremy.stock.tools.Util;
import com.alibaba.fastjson.JSONObject;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
     * 分页查询a股市场所有股票代码url
     */
    private static final String PAGE_A_MARK_STOCKS_URL = PropertiesTools.getProperty("aMarkStocks");

    /**
     * 查询个股当天收盘数据url
     */
    private static final String STOCK_CLOSE_DATA_URL = PropertiesTools.getProperty("stockCloseData");

    /**
     * 查询个股当天资金成交数据url
     */
    private static final String STOCK_TRADE_DATA_URL = PropertiesTools.getProperty("stockTradeData");

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

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `stock_%s` (\n" +
        "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
        "  `open_price` int(11) NOT NULL COMMENT '开盘价格，单位分',\n" +
        "  `top_price` int(11) NOT NULL COMMENT '当天股票最高价格，单位分',\n" +
        "  `low_price` int(11) NOT NULL COMMENT '当天股票最低价格，单位分',\n" +
        "  `yest_close_price` int(11) NOT NULL COMMENT '昨天收盘价格，单位分',\n" +
        "  `close_price` int(11) NOT NULL COMMENT '当天股票收盘价格，单位分',\n" +
        "  `chg` int(5) NOT NULL COMMENT '股票涨跌百分比分子，分母10000',\n" +
        "  `zlc` int(11) NOT NULL COMMENT '当天的资金总流出，单位百元',\n" +
        "  `zlr` int(11) NOT NULL COMMENT '当天的资金总流如，单位百元',\n" +
        "  `je` int(11) NOT NULL COMMENT '当天资金净额，单位百元',\n" +
        "  `today` date NOT NULL COMMENT '当天的日期',\n" +
        "  PRIMARY KEY (`id`),\n" +
        "  UNIQUE KEY `uni_today` (`today`) USING HASH\n" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='%s, 收盘数据';";

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
     * @return java.util.Map<java.lang.String
                       *       ,
                       *       cn.jeremy
                       *       .
                       *       stock.bean.ShareHolder>
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
                    INIT_SHARE_HOLDER_FAIL_SUBJECT, Boolean.TRUE, "utf-8");
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
            .sendRequestByPost(QUERY_ACCOUNT_URL,
                null,
                MOCK_REQ_HEADER,
                GET_ACCOUNT_ASSET_FAIL_SUBJECT,
                Boolean.TRUE,
                "utf-8");
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
            .sendRequestByPost(BUY_STOCK_URL, postParams, MOCK_REQ_HEADER, BUY_FAIL_SUBJECT, Boolean.FALSE, "utf-8");
    }

    @Override
    public HttpResult sellStock(Stock stock)
    {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("type", TYPE_SELL);
        setPostParams(postParams, stock);
        return HttpTools.getInstance()
            .sendRequestByPost(BUY_STOCK_URL, postParams, MOCK_REQ_HEADER, SELL_FAIL_SUBJECT, Boolean.FALSE, "utf-8");
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
                INIT_STOCK_POSITION_MAP_FAIL_SUBJECT, Boolean.TRUE, "utf-8");
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

    @Override
    public int queryStockPClosePrice(String stockCode)
    {
        String price = queryStockData(stockCode, TYPE_BUY, "st_pclose");
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
                    QUERY_SELL_PRICE_FAIL_SUBJECT, Boolean.FALSE, "utf-8");
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
                    Boolean.FALSE, "utf-8");
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
                    QUERY_CANCEL_STOCKS_FAIL_SUBJECT, Boolean.FALSE, "utf-8");
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

    @Override
    public void updateAMarkStocks()
    {
        //查询第一页数据
        String responseBody = pageStock(1);
        int totalPage = getTotalPageFromXML(responseBody);
        List<BaseStockData> stockListFromXML = getStockListFromXML(responseBody);
        List<BaseStockData> list = new ArrayList<>();
        if (Util.isNotEmpty(stockListFromXML))
        {
            list.addAll(stockListFromXML);
        }
        if (totalPage > 1)
        {
            for (int i = 2; i < totalPage + 1; i++)
            {
                responseBody = pageStock(i);
                stockListFromXML = getStockListFromXML(responseBody);
                if (Util.isNotEmpty(stockListFromXML))
                {
                    list.addAll(stockListFromXML);
                }
            }
        }
        Connection connection = MysqlTools.getConnection();
        if (null != connection)
        {
            try
            {
                for (BaseStockData baseStockData : list)
                {
                    ResultSet resultSet = MysqlTools.executeQuery(connection,
                        String.format("select count(1) from stock_base where num = %s", baseStockData.getNum()));
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    if (count == 0)
                    {
                        MysqlTools.executeInsert(connection, String.format("insert into stock_base values ('%s', '%s')",
                            baseStockData.getName(),
                            baseStockData.getNum()));
                    }
                    else
                    {
                        MysqlTools.executeUpdate(connection,
                            String.format("update stock_base set name = '%s' where num = '%s'",
                                baseStockData.getName(),
                                baseStockData.getNum()));
                    }
                    MysqlTools.executeInsert(connection, String.format(CREATE_TABLE_SQL,
                        baseStockData.getNum(),
                        baseStockData.getName()));
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateStockCloseData()
    {
        Connection connection = MysqlTools.getConnection();
        if (null != connection)
        {
            try
            {
                ResultSet rs = MysqlTools.executeQuery(connection, "select num from stock_base");
                while (rs.next())
                {
                    String num = rs.getString(1);
                    StockCloseData stockCloseData = getStockCloseData(num);
                    if (null == stockCloseData)
                    {
                        continue;
                    }
                    setStockTradeData(stockCloseData, num);
                    insertOrUpdateStockCloseData(connection, stockCloseData, num);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void insertOrUpdateStockCloseData(Connection connection, StockCloseData stockCloseData, String num)
    {
        String selectSql = String.format("select id from stock_%s where today = ?", num);
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setDate(1, new java.sql.Date(stockCloseData.getToday().getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                int id = resultSet.getInt(1);
                String updateSql = String.format(
                    "update stock_%s set open_price = ?, top_price = ?, low_price = ?, yest_close_price = ?, " +
                        "close_price = ?, chg = ?, zlc = ?, zlr = ?, je = ? where id = ?", num);
                preparedStatement = connection.prepareCall(updateSql);
                preparedStatement.setInt(1, stockCloseData.getOpenPrice());
                preparedStatement.setInt(2, stockCloseData.getTopPrice());
                preparedStatement.setInt(3, stockCloseData.getLowPrice());
                preparedStatement.setInt(4, stockCloseData.getYestClosePrice());
                preparedStatement.setInt(5, stockCloseData.getClosePrice());
                preparedStatement.setInt(6, stockCloseData.getChg());
                preparedStatement.setInt(7, stockCloseData.getZlc());
                preparedStatement.setInt(8, stockCloseData.getZlr());
                preparedStatement.setInt(9, stockCloseData.getJe());
                preparedStatement.setInt(10, id);
                preparedStatement.executeUpdate();
            }
            else
            {
                String insertSql = String.format(
                    "insert into stock_%s (open_price, top_price, low_price, yest_close_price, close_price, chg, zlc," +
                        " zlr, je, today) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", num);
                preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, stockCloseData.getOpenPrice());
                preparedStatement.setInt(2, stockCloseData.getTopPrice());
                preparedStatement.setInt(3, stockCloseData.getLowPrice());
                preparedStatement.setInt(4, stockCloseData.getYestClosePrice());
                preparedStatement.setInt(5, stockCloseData.getClosePrice());
                preparedStatement.setInt(6, stockCloseData.getChg());
                preparedStatement.setInt(7, stockCloseData.getZlc());
                preparedStatement.setInt(8, stockCloseData.getZlr());
                preparedStatement.setInt(9, stockCloseData.getJe());
                preparedStatement.setDate(10, new java.sql.Date(stockCloseData.getToday().getTime()));
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    private StockCloseData getStockCloseData(String num)
    {
        String stockCloseDataUrl = STOCK_CLOSE_DATA_URL.replace("{num}", num);
        HttpResult result =
            HttpTools.getInstance()
                .sendHttpRequestByGet(stockCloseDataUrl,
                    "Referer:http://stockpage.10jqka.com.cn/realHead_v2.html",
                    "gbk");
        if (result.getRespCode() != HttpStatus.SC_OK)
        {
            return null;
        }
        String responseBody = result.getResponseBody();
        //去掉jsonp包装
        String respJson = responseBody.substring(responseBody.indexOf("(") + 1, responseBody.length() - 1);
        JSONObject jsonObject = JSONObject.parseObject(respJson).getJSONObject("items");
        String updateTimeStr = jsonObject.getString("updateTime").substring(0, 10);

        Date updateTime = DateTools.timeStr2Date(updateTimeStr, DateTools.DATE_FORMAT_10);
        StockCloseData stockCloseData = new StockCloseData(updateTime);
        stockCloseData.setOpenPrice(StringTools.yuanToFen(jsonObject.getString("7")));
        stockCloseData.setTopPrice(StringTools.yuanToFen(jsonObject.getString("8")));
        stockCloseData.setLowPrice(StringTools.yuanToFen(jsonObject.getString("9")));
        stockCloseData.setYestClosePrice(StringTools.yuanToFen(jsonObject.getString("6")));
        stockCloseData.setClosePrice(StringTools.yuanToFen(jsonObject.getString("10")));
        stockCloseData.setChg(StringTools.yuanToFen(jsonObject.getString("199112")));
        return stockCloseData;
    }

    private void setStockTradeData(StockCloseData stockCloseData, String num)
    {
        String stockTradeDataUrl = STOCK_TRADE_DATA_URL.replace("{num}", num);
        HttpResult result =
            HttpTools.getInstance()
                .sendHttpRequestByGet(stockTradeDataUrl, null, "gbk");
        if (result.getRespCode() != HttpStatus.SC_OK)
        {
            return;
        }
        String responseBody = result.getResponseBody();
        JSONObject jsonObject = JSONObject.parseObject(responseBody);
        JSONObject title = jsonObject.getJSONObject("title");
        stockCloseData.setZlc(StringTools.yuanToFen(title.getString("zlc")));
        stockCloseData.setZlr(StringTools.yuanToFen(title.getString("zlr")));
        stockCloseData.setJe(StringTools.yuanToFen(title.getString("je")));
    }

    /**
     * 分页查询股票列表
     *
     * @param page
     * @return java.lang.String
     * @author fengjiangtao
     */
    private String pageStock(int page)
    {
        String url = PAGE_A_MARK_STOCKS_URL.replace("{page}", page + "");
        HttpResult result =
            HttpTools.getInstance()
                .sendHttpRequestByGet(url, MOCK_REQ_HEADER, "gbk");
        if (result.getRespCode() != HttpStatus.SC_OK)
        {
            return null;
        }
        return result.getResponseBody();
    }

    public static void main(String[] args)
    {
        ThsMockTrade.getInstance().updateStockCloseData();
    }

    /**
     * 从xml数据中，分离出总的分页数据
     *
     * @param respXML
     * @return int
     * @author fengjiangtao
     */
    private int getTotalPageFromXML(String respXML)
    {
        if (Util.isEmpty(respXML))
        {
            LOGGER.error("respXML is empty");
            return 0;
        }
        Document document = Jsoup.parse(respXML);
        Elements span = document.select(".page_info");
        // currentPage/totalPage
        String text = span.get(0).text();
        String[] split = text.split("/");
        return Integer.parseInt(split[1]);
    }

    /**
     * 从xml数据中分离出股票名称、代码列表
     *
     * @param respXML
     * @return java.util.List<cn.jeremy.stock.bean.BaseStockData>
     * @author fengjiangtao
     */
    private List<BaseStockData> getStockListFromXML(String respXML)
    {
        if (Util.isEmpty(respXML))
        {
            LOGGER.error("respXML is empty");
            return null;
        }
        Document document = Jsoup.parse(respXML);
        Elements tbodyElements = document.select("tbody");
        if (Util.isEmpty(tbodyElements))
        {
            return null;
        }
        Elements trElements = tbodyElements.get(0).select("tr");
        if (Util.isEmpty(trElements))
        {
            return null;
        }
        List<BaseStockData> list = new ArrayList<>(trElements.size());
        for (Element element : trElements)
        {
            Elements tdElements = element.select("td");
            String stockNum = tdElements.get(1).select("a").get(0).text();
            String stockName = tdElements.get(2).select("a").get(0).text();
            list.add(new BaseStockData(stockName, stockNum));
        }
        return list;
    }
}
