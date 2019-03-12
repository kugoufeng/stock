package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.QuartzTools;
import cn.jeremy.stock.tools.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.math.NumberUtils;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动交易主方法
 *
 * @author kugoufeng
 * @date 2017/12/25 下午 9:26
 */
public class AutoTrade
{

    /**
     * jd牛人账号id
     */
    public static String packageId = PropertiesTools.getProperty("packageId");

    /**
     * 资金账户总金额平分的份数
     */
    public static int totalBalanceValve = NumberUtils.toInt(PropertiesTools.getProperty("totalBalanceValve"), 1);

    /**
     * 实时交易程序的定时表达式
     */
    private static String actualTradeCronExpression = PropertiesTools.getProperty("actualTradeCronExpression");

    /**
     * 实时交易程序的定时表达式
     */
    private static String actualCancelCronExpression = PropertiesTools.getProperty("actualCancelCronExpression");

    /**
     * 股票波动的阀门值
     */
    public static int tradeValve = PropertiesTools.getPropertyInt("tradeValve", 100);

    /**
     * 发送邮件频率限制阀门
     */
    public static int sendMailFrequencyLimitValve = PropertiesTools.getPropertyInt("sendMailFrequencyLimitValve", 10);

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTrade.class);

    public static void main(String[] args)
    {
        if (Util.isNotEmpty(args))
        {
            packageId = args[0];
            if (args.length > 1)
            {
                totalBalanceValve = NumberUtils.toInt(args[1], totalBalanceValve);
            }
            if (args.length > 2)
            {
                actualTradeCronExpression = args[2];
            }
            if (args.length > 3)
            {
                actualCancelCronExpression = args[3];
            }
            if (args.length > 4)
            {
                tradeValve = NumberUtils.toInt(args[4], tradeValve);
            }
            if (args.length > 5)
            {
                sendMailFrequencyLimitValve = NumberUtils.toInt(args[5], sendMailFrequencyLimitValve);
            }
        }
        LOGGER.info(
            "start program, packageId:{}, totalBalanceValve:{}, actualTradeCronExpression:{}, actualCancelCronExpression:{}, tradeValve:{}",
            packageId,
            totalBalanceValve,
            actualTradeCronExpression,
            actualCancelCronExpression,
            tradeValve);
        //记录账户资金信息
//        HandleStockData.getInstance().recordAccount();
        //启动定时任务
        Map<JobDetail, Set<? extends Trigger>> triggersAndJobs = new HashMap<>(16);
//        QuartzTools.getInstance()
//            .addTriggersAndJobs(triggersAndJobs, ActualTradeJob.class, actualTradeCronExpression, 1);
//        QuartzTools.getInstance()
//            .addTriggersAndJobs(triggersAndJobs, ActualCancelJob.class, actualCancelCronExpression, 2);
        QuartzTools.getInstance()
            .addTriggersAndJobs(triggersAndJobs, TimelyGetStockPrizeJob.class, actualCancelCronExpression, 3);
        QuartzTools.getInstance().addJobs(triggersAndJobs).startJobs();
    }
}
