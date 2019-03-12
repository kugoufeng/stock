package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.AccountAsset;
import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.Stock;
import java.util.List;
import java.util.Map;

/**
 * 股票的交易接口
 *
 * @author kugoufeng
 * @date 2017/12/22 下午 6:20
 */
public interface Trade
{

    /**
     * 获取账户资产
     *
     * @return cn.jeremy.stock.bean.AccountAsset
     */
    AccountAsset getAccountAsset();

    /**
     * 买入股票
     *
     * @param stock 要买入的股票
     * @return java.lang.String
     */
    HttpResult buyStock(Stock stock);

    /**
     * 卖出股票 要卖出的股票
     *
     * @param stock 要卖出的股票
     * @return java.lang.String
     */
    HttpResult sellStock(Stock stock);

    /**
     * 批量交易股票
     *
     * @param tradeStocks 要交易的股票集合
     */
    void tradeStocks(Map<String, List<Stock>> tradeStocks);

    /**
     * 初始化股票持仓账户
     */
    void initStockPositionMap();

    /**
     * 查询股票买入价格
     *
     * @param stockCode 股票代码
     * @return int
     */
    int queryStockBuyPrice(String stockCode);

    /**
     * 查询股票卖出价格
     *
     * @param stockCode 股票代码
     * @return int
     */
    int queryStockSellPrice(String stockCode);

    /**
     * 查询股票开盘价格
     *
     * @param stockCode 股票代码
     * @return int
     */
    int queryStockOpenPrice(String stockCode);

    /**
     * 撤单
     *
     * @param orderId 要撤单的撤单号
     * @param args 其他条件
     * @return boolean
     */
    HttpResult cancelOrder(String orderId, String... args);

    /**
     * 查询撤单并执行撤单
     */
    void queryAndCancelOrder();

    /**
     * 获取访问的cookie
     *
     * @author fengjiangtao
     */
    String getRequestCookie();
}
