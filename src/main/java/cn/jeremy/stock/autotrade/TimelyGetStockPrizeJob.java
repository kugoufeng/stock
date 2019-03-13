package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.tools.DateTools;
import cn.jeremy.stock.tools.FileTools;
import cn.jeremy.stock.tools.PropertiesTools;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 实时获取股票价格
 *
 * @author fengjiangtao
 * @date 2019/3/12 23:10
 */
public class TimelyGetStockPrizeJob implements Job
{
    private static final Logger LOGGER = LogManager.getLogger(TimelyGetStockPrizeJob.class);

    private static String stockStoreRootPath = PropertiesTools.getProperty("stockStoreRootPath");

    private static String stockFile = stockStoreRootPath.concat("/stock.txt");

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
        throws JobExecutionException
    {
        LOGGER.info("start TimelyGetStockPrizeJob");
        try
        {
            List<String> stockList = FileTools.readFile2List(stockFile, "utf-8");
            if (CollectionUtils.isNotEmpty(stockList))
            {
                String currentDate = DateTools.getCurrentDate(DateTools.DATE_FORMAT2_8);
                for (String stock : stockList)
                {
                    String[] split = stock.split(" ");
                    String filePath =
                        stockStoreRootPath.concat("/stock/").concat(split[0]).concat("/").concat(currentDate).concat(".txt");
                    FileTools.createOrExistsFile(filePath);
                    if (FileTools.getFileLines(filePath) == 0)
                    {
                        int pClosePrice = ThsMockTrade.getInstance().queryStockPClosePrice(split[1]);
                        String content =
                            DateTools.getCurrentDate(DateTools.DATE_FORMAT_21).concat(String.valueOf(pClosePrice / 100F)).concat("\n");
                        FileTools.writeFileFromString(filePath, content, false);
                    }
                    else if (FileTools.getFileLines(filePath) == 1)
                    {
                        int openPrice = ThsMockTrade.getInstance().queryStockOpenPrice(split[1]);
                        String content =
                            DateTools.getCurrentDate(DateTools.DATE_FORMAT_21).concat(String.valueOf(openPrice / 100F)).concat("\n");
                        FileTools.writeFileFromString(filePath, content, true);
                    }
                    else
                    {
                        int sellPrice = ThsMockTrade.getInstance().queryStockSellPrice(split[1]);
                        String content =
                            DateTools.getCurrentDate(DateTools.DATE_FORMAT_21).concat(String.valueOf(sellPrice / 100F)).concat("\n");
                        FileTools.writeFileFromString(filePath, content, true);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("exit ActualTradeJob, e:{}", e);
        }
    }

}
