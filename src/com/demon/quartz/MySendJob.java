package com.demon.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MySendJob implements Job {

	public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		System.out.println("Send Job : " + System.currentTimeMillis());
	}

}
