package com.demon.jsvc.task;

import com.alibaba.fastjson.JSON;
import com.demon.log.LogUtil;


public abstract class BaseSingleThreadQueueJob<T> extends BaseThreadPoolQueueJob<T> {

	protected BaseSingleThreadQueueJob(JmainName jmainName) {
	    super(jmainName, 1);
	}
	
	@Override
	public void doJob() throws InterruptedException {
		LogUtil.mark(jmainName.name());
		
		while(true){
			T t = null;
			try {
                t = popQueue();
            } catch (Exception e) {
                logger.error("popQueue exception:", e);
            }
			
			if(isEnd(t)){
				logger.info("popQueue isEnd");
				try {
					postEnd();
				} catch (Exception e) {
					logger.error("postEnd exception:", e);
				}
				break;
			}
			
			try {
                if (!markToHandleQueueData(t)){
                    continue;
                }
            } catch (Exception e) {
                logger.error("markToHandleQueueData exception:", e);
                continue;
            }
			
			final T c = t;
			LogUtil.mark(jmainName.name());
            process(c);
		}
	}
	
	protected void process(T t){
		try {
			handleQueueData(t);
		} catch (Exception e) {
			logger.error("process exception, data:" + JSON.toJSONString(t), e);
		}
	}
	
}
