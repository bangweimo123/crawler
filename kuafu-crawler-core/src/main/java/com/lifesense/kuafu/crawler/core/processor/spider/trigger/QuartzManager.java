package com.lifesense.kuafu.crawler.core.processor.spider.trigger;

// 简单的任务管理类

import org.apache.commons.collections.MapUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.util.Map;

/**
 *
 */

/**
 * @Title:Quartz管理类
 *
 */
public class QuartzManager {
    private static SchedulerFactory sf = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "group1";
    private static String TRIGGER_GROUP_NAME = "trigger1";


    public static boolean isExistJob(String triggerName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        Trigger trigger = sched.getTrigger(TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME));
        return null != trigger;
    }

    public static boolean isExistJob(String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        Trigger trigger = sched.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
        return null != trigger;
    }

    /** */
    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param jobName 任务名
     * @param job     任务
     * @param time    时间设置，参考quartz说明文档
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void addJob(String jobName, Job job, String time, Map<String, Object> params) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        JobDetail jobDetail = new JobDetailImpl(jobName, JOB_GROUP_NAME, job.getClass());// 任务名，任务组，任务执行类
        if (MapUtils.isNotEmpty(params)) {
            for (String key : params.keySet()) {
                jobDetail.getJobDataMap().put(key, params.get(key));
            }
        }
        // 触发器
        CronTriggerImpl trigger = new CronTriggerImpl(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
        trigger.setCronExpression(time);// 触发器时间设定
        sched.scheduleJob(jobDetail, trigger);
        // 启动
        if (!sched.isShutdown())
            sched.start();
    }

    /** */
    /**
     * 添加一个定时任务
     *
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param job              任务
     * @param time             时间设置，参考quartz说明文档
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job, String time, Map<String, Object> params) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        JobDetail jobDetail = new JobDetailImpl(jobName, jobGroupName, job.getClass());// 任务名，任务组，任务执行类
        if (MapUtils.isNotEmpty(params)) {
            for (String key : params.keySet()) {
                jobDetail.getJobDataMap().put(key, params.get(key));
            }
        }
        // 触发器
        CronTriggerImpl trigger = new CronTriggerImpl(triggerName, triggerGroupName);// 触发器名,触发器组
        trigger.setCronExpression(time);// 触发器时间设定
        sched.scheduleJob(jobDetail, trigger);
        if (!sched.isShutdown())
            sched.start();
    }

    /** */
    /**
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     * @param time
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void modifyJobTime(String jobName, String time) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        Trigger trigger = sched.getTrigger(TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME));
        if (trigger != null) {
            CronTriggerImpl ct = (CronTriggerImpl) trigger;
            ct.setCronExpression(time);
            sched.resumeTrigger(TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME));
        }
    }

    /** */
    /**
     * 修改一个任务的触发时间
     *
     * @param triggerName
     * @param triggerGroupName
     * @param time
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void modifyJobTime(String triggerName, String triggerGroupName, String time) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        Trigger trigger = sched.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
        if (trigger != null) {
            CronTriggerImpl ct = (CronTriggerImpl) trigger;
            // 修改时间
            ct.setCronExpression(time);
            // 重启触发器
            sched.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
        }
    }

    /** */
    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     * @throws SchedulerException
     */
    public static void removeJob(String jobName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        sched.pauseTrigger(triggerKey);// 停止触发器
        sched.unscheduleJob(triggerKey);// 移除触发器
        sched.deleteJob(JobKey.jobKey(jobName));// 删除任务
    }

    /** */
    /**
     * 移除一个任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @throws SchedulerException
     */
    public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        sched.pauseTrigger(triggerKey);// 停止触发器
        sched.unscheduleJob(triggerKey);// 移除触发器
    }
}