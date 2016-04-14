package com.demon.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MyReceiveJob implements Job {

	public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		System.out.println("Receive Job : " + System.currentTimeMillis());
	}

}
