package cn.jeremy.stock.tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTools
{
    private static final Logger LOGGER = LogManager.getLogger(QuartzTools.class);
    
    private static String JOB_NAME = "EXTJWEB_NAME";
    
    private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
    
    private static String TRIGGER_NAME = "EXTJWEB_NAME";
    
    private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";
    
    private static Scheduler scheduler = null;
    
    private static QuartzTools instance = null;
    
    private static Integer LOCK_ZERO = 0;
    
    private static Integer LOCK_ONE = 1;
    
    private QuartzTools()
    {
        getScheduler();
    }
    
    public static QuartzTools getInstance()
    {
        synchronized (LOCK_ZERO)
        {
            if (instance == null)
            {
                instance = new QuartzTools();
            }
            return instance;
        }
    }
    
    private Scheduler getScheduler()
    {
        synchronized (LOCK_ONE)
        {
            if (scheduler == null)
            {
                try
                {
                    scheduler = new StdSchedulerFactory().getScheduler();
                }
                catch (SchedulerException e)
                {
                    LOGGER.error("create scheduler fail", e);
                }
            }
            return scheduler;
        }
    }
    
    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param sched:调度器
     * @param jobClass:任务
     * @param cronExpression:时间设置，CronExpression表达式
     */
    public QuartzTools addJob(Class<? extends Job> jobClass, String cronExpression)
    {
        return addJob(jobClass, cronExpression, JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME);
    }
    
    /**
     * @Description: 添加一个定时任务
     * @param sched:调度器
     * @param jobClass:任务
     * @param cronExpression:时间设置，CronExpression表达式
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     * @param triggerName:触发器名
     * @param triggerGroupName:触发器组名
     */
    public QuartzTools addJob(Class<? extends Job> jobClass, String cronExpression, String jobName, String jobGroupName,
        String triggerName, String triggerGroupName)
    {
        
        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerName, triggerGroupName)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();
        try
        {
            getScheduler().scheduleJob(job, trigger);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("add job fail", e);
        }
        
        return instance;
    }
    
    /**
     * 添加任务和定时器到map中
     *
     * @author fengjiangtao
     * @param triggersAndJobs
     * @param jobClass
     * @param cronExpression
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    public void addTriggersAndJobs(Map<JobDetail, Set<? extends Trigger>> triggersAndJobs,
        Class<? extends Job> jobClass, String cronExpression, int jobSize)
    {
        
        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(JOB_NAME + jobSize, JOB_GROUP_NAME + jobSize).build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(TRIGGER_NAME + jobSize, TRIGGER_GROUP_NAME + jobSize)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();
        Set<CronTrigger> triggers = new HashSet<>();
        triggers.add(trigger);
        triggersAndJobs.put(job, triggers);
    }
    
    /**
     * 添加多个任务
     *
     * @author fengjiangtao
     * @param triggersAndJobs
     * @return
     */
    public QuartzTools addJobs(Map<JobDetail, Set<? extends Trigger>> triggersAndJobs)
    {
        try
        {
            getScheduler().scheduleJobs(triggersAndJobs, Boolean.FALSE);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("add job fail", e);
        }
        
        return instance;
        
    }
    
    /**
     * @Description: 定义一个任务之后进行触发设定(使用默认的任务组名，触发器名，触发器组名)
     * @param sched:调度器
     * @param time
     */
    public QuartzTools addJObLaterUse(Class<? extends Job> jobClass, String time)
    {
        return addJObLaterUse(jobClass, time, JOB_NAME, JOB_GROUP_NAME);
    }
    
    /**
     * @Description: 定义一个任务之后进行触发设定
     * @param sched:调度器
     * @param time
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    public QuartzTools addJObLaterUse(Class<? extends Job> jobClass, String time, String jobName, String jobGroupName)
    {
        
        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).storeDurably().build();
        
        try
        {
            getScheduler().addJob(job, false);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("add job later use fail", e);
        }
        
        return instance;
    }
    
    /**
     * @Description: 对已存储的任务进行scheduling(使用默认的任务组名，触发器名，触发器组名)
     * @param sched:调度器
     * @param time
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    public QuartzTools schedulingStoredJOb(Class<? extends Job> jobClass, String time)
    {
        return schedulingStoredJOb(jobClass, time, JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME);
    }
    
    /**
     * @Description: 对已存储的任务进行scheduling
     * @param sched:调度器
     * @param time
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    public QuartzTools schedulingStoredJOb(Class<? extends Job> jobClass, String time, String jobName,
        String jobGroupName, String triggerName, String triggerGroupName)
    {
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerName, triggerGroupName)
            .startNow()
            .forJob(JobKey.jobKey(jobName, jobGroupName))
            .build();
        try
        {
            getScheduler().scheduleJob(trigger);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("scheduling stored job fail", e);
        }
        
        return instance;
    }
    
    /**
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @param sched:调度器
     * @param time
     */
    public QuartzTools modifyJobTime(String time)
    {
        return modifyJobTime(TRIGGER_NAME, TRIGGER_GROUP_NAME, time);
    }
    
    /**
     * @Description: 修改一个任务的触发时间
     * @param sched:调度器
     * @param triggerName
     * @param triggerGroupName
     * @param time
     */
    public QuartzTools modifyJobTime(String triggerName, String triggerGroupName, String time)
    {
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerName, triggerGroupName)
            .withSchedule(CronScheduleBuilder.cronSchedule(time))
            .startNow()
            .build();
        
        try
        {
            getScheduler().rescheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName), trigger);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("modify job time fail", e);
        }
        return instance;
    }
    
    /**
     * @Description: 修改一个任务(使用默认的任务组名，任务名)
     * @param sched:调度器
     */
    public QuartzTools modifyJob(Class<? extends Job> jobClass)
    {
        return modifyJob(jobClass, JOB_NAME, JOB_GROUP_NAME);
    }
    
    /**
     * @Description: 修改一个任务
     * @param sched:调度器
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    public QuartzTools modifyJob(Class<? extends Job> jobClass, String jobName, String jobGroupName)
    {
        JobDetail job1 = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        try
        {
            getScheduler().addJob(job1, true);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("modify job fail", e);
        }
        
        return instance;
    }
    
    /**
     * @Description: 删除一个任务的的trigger
     * @param sched:调度器
     * @param triggerName
     * @param triggerGroupName
     */
    public QuartzTools unschedulingJob(String triggerName, String triggerGroupName)
    {
        try
        {
            getScheduler().unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));
        }
        catch (SchedulerException e)
        {
            LOGGER.error("un scheduling job fail", e);
        }
        
        return instance;
    }
    
    /**
     * @Description: 移除一个任务，以及任务的所有trigger
     * @param sched:调度器
     * @param jobName
     */
    public QuartzTools removeJob(String jobName, String jobGroupName)
    {
        try
        {
            getScheduler().deleteJob(JobKey.jobKey(jobName, jobGroupName));
        }
        catch (SchedulerException e)
        {
            LOGGER.error("remove job fail", e);
        }
        
        return instance;
    }
    
    /**
     * @Description:启动所有定时任务
     * @param sched:调度器
     */
    public void startJobs()
    {
        try
        {
            getScheduler().start();
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @Description:关闭所有定时任务
     * @param sched:调度器
     */
    public void shutdownJobs()
    {
        try
        {
            if (!getScheduler().isShutdown())
            {
                // 未传参或false：不等待执行完成便结束；true：等待任务执行完才结束
                getScheduler().shutdown(Boolean.TRUE);
            }
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
