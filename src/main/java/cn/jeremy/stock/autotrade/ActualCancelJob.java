package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.RequestElement;
import cn.jeremy.stock.exception.ExitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 实时撤单任务
 *
 * @author kugoufeng
 * @date 2017/12/25 下午 9:20
 */
public class ActualCancelJob implements Job
{
    private static final Logger LOGGER = LogManager.getLogger(ActualCancelJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
        throws JobExecutionException
    {
        LOGGER.info("start ActualCancelJob");
        try
        {
            ThsMockTrade.getInstance().queryAndCancelOrder();
        }
        catch (ExitException e)
        {
            LOGGER.error("exit ActualCancelJob, e:{}", e);
        }
    }

}
