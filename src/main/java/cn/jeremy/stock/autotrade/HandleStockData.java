package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.AccountAsset;
import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.Stock;
import cn.jeremy.stock.constance.ParamConstance;
import cn.jeremy.stock.tools.DateTools;
import cn.jeremy.stock.tools.FileTools;
import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 处理股票数据
 *
 * @author kugoufeng
 * @date 2017/12/22 下午 3:57
 */
public class HandleStockData
{
    private static final Logger LOGGER = LogManager.getLogger(HandleStockData.class);

    /**
     * 交易数据存放的文件夹
     */
    private static String STOCK_DATA_URL =
        PropertiesTools.getProperty("tradeDataUrl").concat(AutoTrade.packageId);

    /**
     * 老的持仓的url
     */
    private static String STOCK_POSITION_URL = STOCK_DATA_URL.concat("/stockPosition.txt");

    /**
     * 记录账户的资金情况
     */
    private static String ACCOUNT_RECORD_URL = STOCK_DATA_URL.concat("/accountRecord.txt");

    /**
     * 记录错误的数据
     */
    private static String ERROR_DATA_URL = STOCK_DATA_URL.concat("/errorData.txt");

    /**
     * 交易详情文件夹
     */
    private static String TRADE_DETAIL_PATH =
        STOCK_DATA_URL.concat("/tradeDetail/").concat(DateTools.getCurrentDate(DateTools.DATE_FORMAT_10)).concat("/");

    /**
     * 撤单详情文件夹
     */
    private static String CANCEL_DETAIL_PATH =
        STOCK_DATA_URL.concat("/cancelDetail/").concat(DateTools.getCurrentDate(DateTools.DATE_FORMAT_10)).concat("/");

    /**
     * 牛人持仓（老的持仓）
     */
    private static volatile Map<String, Stock> stockPosition = new HashMap<>();

    /**
     * 文件的类型
     */
    private static final String FILE_SUFFIX = ".txt";

    /**
     * 冒号分割符
     */
    private static final String SPILT_SYMBOL = ":";

    /**
     * 交易的阀门值，用于控制股票是否交易的条件
     */
    public static int TRADE_VALVE = AutoTrade.tradeValve;

    static
    {
        LOGGER.debug("start init stockPosition{}", "");
        // 创建文件夹和文件
        FileTools.createOrExistsDir(STOCK_DATA_URL);
        FileTools.createOrExistsDir(TRADE_DETAIL_PATH);
        FileTools.createOrExistsDir(CANCEL_DETAIL_PATH);
        FileTools.createOrExistsFile(STOCK_POSITION_URL);
        FileTools.createOrExistsFile(ACCOUNT_RECORD_URL);
        FileTools.createOrExistsFile(ERROR_DATA_URL);
        List<String> fileList = FileTools.readFile2List(STOCK_POSITION_URL, "utf-8");
        if (Util.isNotEmpty(fileList))
        {
            for (String line : fileList)
            {
                String[] split = line.split(SPILT_SYMBOL);
                if (split.length == 2)
                {
                    stockPosition.put(split[0].trim(), new Stock(split[1]));
                }
            }
        }
        LOGGER.debug("stockPosition{}", stockPosition);
    }

    private static final HandleStockData INSTANCE = new HandleStockData();

    private HandleStockData()
    {

    }

    public static HandleStockData getInstance()
    {
        return INSTANCE;
    }

    /**
     * 记录账户的资金情况
     *
     * @author fengjiangtao
     */
    public void recordAccount()
    {
        AccountAsset accountAsset = ThsMockTrade.getInstance().getAccountAsset();
        //创建需要记录的数据
        StringBuffer sb = new StringBuffer();
        sb.append(DateTools.getCurrentDate(DateTools.DATE_FORMAT_24HOUR_21)).append("\r\n");
        sb.append(accountAsset);
        sb.append("\r\n");
        FileTools.writeFileFromString(ACCOUNT_RECORD_URL, sb.toString(), Boolean.TRUE);
    }

    /**
     * 获取当天交易的股票名称
     *
     * @return java.lang.String
     * @author fengjiangtao
     */
    public String getTradeStockName()
    {
        List<File> files = FileTools.listFilesInDir(TRADE_DETAIL_PATH, Boolean.FALSE);
        if (Util.isNotEmpty(files))
        {
            StringBuffer sb = new StringBuffer();
            for (File file : files)
            {
                sb.append(file.getName()).append("|");
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 更新牛人当前股票持仓
     *
     * @param newStockPosition 牛人新的股票持仓
     */
    public void writeToStockPosition(Map<String, Stock> newStockPosition)
    {
        LOGGER.debug("enter, stockPosition:{}, newStockPosition:{}", stockPosition, newStockPosition);
        if (Util.isEmpty(newStockPosition))
        {
            return;
        }
        stockPosition.clear();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Stock> entry : newStockPosition.entrySet())
        {
            sb.append(entry.getKey()).append(SPILT_SYMBOL).append(entry.getValue().toString()).append("\r\n");
            stockPosition.put(entry.getKey(), entry.getValue());
        }

        FileTools.writeFileFromString(STOCK_POSITION_URL, sb.toString(), Boolean.FALSE);
        LOGGER.debug("exit, stockPosition:{}", stockPosition);
    }

    /**
     * 比较新，老股票持仓，获取需要交易的股票
     *
     * @param newStockPosition 新的股票持仓
     * @return java.util.Map<java.lang.String,java.util.List<cn.jeremy.stock.bean.Stock>>
     */
    public Map<String, List<Stock>> getTradeStocks(Map<String, Stock> newStockPosition)
    {

        LOGGER.debug("enter, stockPosition:{}, newStockPosition:{}", stockPosition, newStockPosition);

        List<Stock> buyStocks = new ArrayList<>();

        List<Stock> sellStocks = new ArrayList<>();

        Map<String, Stock> oldCopy = new HashMap<>(stockPosition);
        Map<String, Stock> newCopy = new HashMap<>(newStockPosition);

        for (Map.Entry<String, Stock> entry : newCopy.entrySet())
        {
            String key = entry.getKey();
            Stock newStock = entry.getValue();
            if (Util.isEmpty(newStock.getStockNum())
                || !(newStock.getStockNum().startsWith("0") || newStock.getStockNum().startsWith("6")))
            {
                LOGGER.debug("stock:{}, can not be trade", newStock);
                oldCopy.remove(key);
                continue;
            }
            Stock oldStock = oldCopy.get(key.trim());
            int tradeSymbol = newStock.equals(oldStock);
            if (tradeSymbol > TRADE_VALVE)
            {
                buyStocks.add(newStock);
            }
            else if (tradeSymbol < -1 * TRADE_VALVE)
            {
                sellStocks.add(newStock);
            }
            else if (newStock.getStockPer() == 0)
            {
                sellStocks.add(newStock);
            }
            oldCopy.remove(key);
        }

        for (Map.Entry<String, Stock> entry : oldCopy.entrySet())
        {
            Stock stock = entry.getValue();
            stock.setStockPer(0);
            sellStocks.add(stock);
        }

        Map<String, List<Stock>> tradeStocks = new HashMap<>(16);

        if (Util.isNotEmpty(buyStocks))
        {
            tradeStocks.put(ParamConstance.BUY, buyStocks);
        }

        if (Util.isNotEmpty(sellStocks))
        {
            tradeStocks.put(ParamConstance.SELL, sellStocks);
        }

        LOGGER.debug("exit, tradeStocks:{}", tradeStocks);

        return tradeStocks;
    }

    /**
     * 记录股票的交易信息
     *
     * @param tradeStocks
     * @author fengjiangtao
     */
    public void recordStockTradeMessage(Map<String, List<Stock>> tradeStocks)
    {
        List<Stock> buyStocks = tradeStocks.get(ParamConstance.BUY);

        List<Stock> sellStocks = tradeStocks.get(ParamConstance.SELL);

        if (Util.isNotEmpty(buyStocks))
        {
            for (Stock stock : buyStocks)
            {
                String filePath = TRADE_DETAIL_PATH.concat(stock.getStockName()).concat(FILE_SUFFIX);
                FileTools.createOrExistsFile(filePath);
                String content = DateTools.getCurrentDate(DateTools.DATE_FORMAT_24HOUR_21)
                    .concat(ParamConstance.BUY)
                    .concat("-")
                    .concat(stock.toString())
                    .concat("\r\n");
                FileTools.writeFileFromString(filePath, content, Boolean.TRUE);
            }
        }

        if (Util.isNotEmpty(sellStocks))
        {
            for (Stock stock : sellStocks)
            {
                String filePath = TRADE_DETAIL_PATH.concat(stock.getStockName()).concat(FILE_SUFFIX);
                FileTools.createOrExistsFile(filePath);
                String content = DateTools.getCurrentDate(DateTools.DATE_FORMAT_24HOUR_21)
                    .concat(ParamConstance.SELL)
                    .concat("-")
                    .concat(stock.toString())
                    .concat("\r\n");
                FileTools.writeFileFromString(filePath, content, Boolean.TRUE);
            }
        }

    }

    /**
     * 记录股票交易的记录
     *
     * @param type 交易的类型
     * @param stock 交易的股票
     * @param accountAsset 账户资金情况
     * @param stockPositionMap 当前持仓
     * @param httpResult 交易结果
     */
    public void recordStockTradeData(String type, Stock stock, AccountAsset accountAsset,
        Map<String, Stock> stockPositionMap, HttpResult httpResult)
    {
        //检查或者创建需要记录数据的文件
        String filePath = TRADE_DETAIL_PATH.concat(stock.getStockName()).concat(FILE_SUFFIX);
        FileTools.createOrExistsFile(filePath);
        //创建需要记录的数据
        StringBuffer sb = new StringBuffer();
        sb.append(DateTools.getCurrentDate(DateTools.DATE_FORMAT_24HOUR_21)).append(type).append("\r\n");
        sb.append("stock:").append(stock).append("\r\n");
        sb.append("accountAsset:").append(accountAsset).append("\r\n");
        sb.append("stockPosition:").append(stockPositionMap).append("\r\n");
        sb.append("httpResult:").append(httpResult).append("\r\n");
        sb.append("\r\n");
        FileTools.writeFileFromString(filePath, sb.toString(), Boolean.TRUE);
    }

    /**
     * 记录撤单的数据
     *
     * @param stock 要撤单的股票
     * @param accountAsset 账户资金
     * @param stockPositionMap 当前持仓
     * @param httpResult 撤单结果
     */
    public void recordStockCancelData(Stock stock, AccountAsset accountAsset,
        Map<String, Stock> stockPositionMap, HttpResult httpResult)
    {
        //检查或者创建需要记录数据的文件
        String filePath = CANCEL_DETAIL_PATH.concat(stock.getStockName()).concat(FILE_SUFFIX);
        FileTools.createOrExistsFile(filePath);
        //创建需要记录的数据
        StringBuffer sb = new StringBuffer();
        sb.append(DateTools.getCurrentDate(DateTools.DATE_FORMAT_24HOUR_21)).append("\r\n");
        sb.append("stock:").append(stock).append("\r\n");
        sb.append("accountAsset:").append(accountAsset).append("\r\n");
        sb.append("stockPosition:").append(stockPositionMap).append("\r\n");
        sb.append("httpResult:").append(httpResult).append("\r\n");
        sb.append("\r\n");
        FileTools.writeFileFromString(filePath, sb.toString(), Boolean.TRUE);
    }

    /**
     * 获取当前账户的持仓
     *
     * @return java.util.Map<java.lang.String,cn.jeremy.stock.bean.Stock>
     * @author fengjiangtao
     */
    public Map<String, Stock> getStockPosition()
    {
        return stockPosition;
    }

    /**
     * 记录错误的数据
     *
     * @param data 要记录的数据
     * @author fengjiangtao
     */
    public void recordErrorData(String data)
    {
        //创建需要记录的数据
        StringBuffer sb = new StringBuffer();
        sb.append(DateTools.getCurrentDate(DateTools.DATE_FORMAT_24HOUR_21)).append("\r\n");
        sb.append(data).append("\r\n");
        sb.append("\r\n");
        FileTools.writeFileFromString(ERROR_DATA_URL, sb.toString(), Boolean.TRUE);
    }

}
