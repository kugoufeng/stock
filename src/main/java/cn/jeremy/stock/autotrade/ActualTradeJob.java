package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.Stock;
import cn.jeremy.stock.tools.Util;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 实时交易
 *
 * @author kugoufeng
 * @date 2017/12/25 下午 9:10
 */
public class ActualTradeJob implements Job
{
    private static final Logger LOGGER = LogManager.getLogger(ActualTradeJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
        throws JobExecutionException
    {
        LOGGER.info("start ActualTradeJob");
        try
        {
            Map<String, Stock> stockPosition = JDStockPosition.getInstance().getStockPosition();
            if (Util.isNotEmpty(stockPosition))
            {
                Map<String, List<Stock>> tradeStocks = HandleStockData.getInstance().getTradeStocks(stockPosition);
                if (Util.isNotEmpty(tradeStocks))
                {
                    ThsMockTrade.getInstance().tradeStocks(tradeStocks);
                }
                HandleStockData.getInstance().writeToStockPosition(stockPosition);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("exit ActualTradeJob, e:{}", e);
        }
    }

}
