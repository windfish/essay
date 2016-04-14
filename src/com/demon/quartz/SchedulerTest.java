package com.demon.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerTest {

	public static void main(String[] args) {
		SchedulerFactory schedulerfactory = new StdSchedulerFactory();
		Scheduler scheduler = null;
		try {
			// 通过schedulerFactory获取一个调度器
			scheduler = schedulerfactory.getScheduler();

			// 创建jobDetail实例，绑定Job实现类
			// 指明job的名称，所在组的名称，以及绑定job类
			JobDetail sendJob = JobBuilder.newJob(MySendJob.class)
					.withIdentity("job1", "jgroup1").build();
			JobDetail receiveJob = JobBuilder.newJob(MyReceiveJob.class)
					.withIdentity("job2", "jgroup1").build();

			// 使用cornTrigger规则
			Trigger trigger1 = TriggerBuilder
					.newTrigger()
					.withIdentity("simpleTrigger1", "triggerGroup1")
					.withSchedule(
							CronScheduleBuilder.cronSchedule("0/2 * * * * ? *"))
					.startNow().build();
			Trigger trigger2 = TriggerBuilder
					.newTrigger()
					.withIdentity("simpleTrigger2", "triggerGroup2")
					.withSchedule(
							CronScheduleBuilder.cronSchedule("0/8 * * * * ? *"))
					.startNow().build();

			// 把作业和触发器注册到任务调度中
			scheduler.scheduleJob(sendJob, trigger1);
			scheduler.scheduleJob(receiveJob, trigger2);

			// 启动调度
			scheduler.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
