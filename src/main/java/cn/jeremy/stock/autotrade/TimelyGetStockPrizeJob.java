package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.tools.DateTools;
import cn.jeremy.stock.tools.FileTools;
import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.StringTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

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

    private static String stockPriseFile = stockStoreRootPath.concat("/prise.txt");

    private static NumberFormat nf = NumberFormat.getNumberInstance();

    static
    {
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.UP);
        FileTools.createOrExistsFile(stockPriseFile);
    }

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
                int i = 0;
                for (String stock : stockList)
                {
                    i++;
                    String[] split = stock.split(" ");
                    String stockName = split[0];
                    String stockCode = split[1];
                    String filePath =
                        stockStoreRootPath.concat("/stock/")
                            .concat(stockName)
                            .concat("/")
                            .concat(currentDate)
                            .concat(".txt");
                    FileTools.createOrExistsFile(filePath);

                    if (FileTools.getFileLines(filePath) == 0)
                    {
                        int pClosePrice = ThsMockTrade.getInstance().queryStockPClosePrice(stockCode);
                        String content =
                            DateTools.getCurrentDate(DateTools.DATE_FORMAT_21)
                                .concat("-")
                                .concat(String.valueOf(pClosePrice / 100F))
                                .concat("\n");
                        FileTools.writeFileFromString(filePath, content, false);
                    }
                    else if (FileTools.getFileLines(filePath) == 1)
                    {
                        int openPrice = ThsMockTrade.getInstance().queryStockOpenPrice(stockCode);
                        double range = countRange(getPClosePrize(filePath), openPrice);
                        String rangeStr = range < 0 ?
                            "-↓".concat(nf.format(-range)) : "-↑".concat(nf.format(range));
                        String content =
                            DateTools.getCurrentDate(DateTools.DATE_FORMAT_21).concat("-")
                                .concat(String.valueOf(openPrice / 100F)).concat(rangeStr)
                                .concat("\n");
                        FileTools.writeFileFromString(filePath, content, true);
                    }
                    else
                    {
                        int sellPrice = ThsMockTrade.getInstance().queryStockSellPrice(stockCode);
                        int pClosePrize = getPClosePrize(filePath);
                        double range = countRange(pClosePrize, sellPrice);
                        String rangeStr = range < 0 ?
                            "-↓".concat(nf.format(-range)) : "-↑".concat(nf.format(range));
                        String content =
                            DateTools.getCurrentDate(DateTools.DATE_FORMAT_21).concat("-")
                                .concat(String.valueOf(sellPrice / 100F)).concat(rangeStr)
                                .concat("\n");
                        FileTools.writeFileFromString(filePath, content, true);
                        content = stockName.concat(" ").concat(String.valueOf(pClosePrize / 100F)).concat("-").concat(content);
                        if (i == 1)
                        {
                            FileTools.writeFileFromString(stockPriseFile, content, false);
                        }
                        else
                        {
                            FileTools.writeFileFromString(stockPriseFile, content, true);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("exit ActualTradeJob, e:{}", e);
        }
    }

    private int getPClosePrize(String filePath)
    {
        List<String> prizeList = FileTools.readFile2List(filePath, 1, 1, "utf-8");
        if (CollectionUtils.isNotEmpty(prizeList))
        {
            String pClosePrize = prizeList.get(0);
            String[] split = pClosePrize.split("-");
            if (split != null && split.length > 0)
            {
                return StringTools.yuanToFen(split[split.length - 1]);
            }
        }
        return 0;
    }

    private double countRange(int pCLosePrize, int prize)
    {
        if (pCLosePrize > 0)
        {
            return ((prize - pCLosePrize) * 1.0 / pCLosePrize) * 100;
        }
        return 0d;
    }

}
