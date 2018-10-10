package com.demon.jsvc.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.demon.log.LogUtil;

/**
 * 线程池执行队列任务
 * 
 * 1、Master线程从队列中pop数据
 * 2、若没有空闲的Worker线程可处理，wait()
 * 3、若有空闲的Worker线程可处理，Worker空闲线程数减1，并把数据派送到Worker线程池处理
 * 4、Worker线程处理完毕，空闲Worker线程数加1，并notify Master线程处理下一个数据
 * 5、Master线程没有获取到可处理的数据，即队列已空，执行postEnd()
 * 
 */
public abstract class BaseThreadPoolQueueJob<T> extends BaseAbstractCycleJob {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final int DEFAULT_MAX_EXECUTING = 2;
	
	protected static final long DEFAULT_SLEEP_SECONDS = 2;
	
	/**
	 * 最大的线程处理个数
	 * 当每个线程都在运行的时候，即idleCount == 0时，不允许继续pop队列取数据继续处理。
	 * 当有空闲队列可以继续处理时，唤醒pop队列线程，继续取数据处理。
	 */
	protected final int maxExecuting;
	
	/**
	 * 当前的空闲线程个数
	 */
	private volatile AtomicInteger idleCount;
	
	/**
	 * 线程池
	 */
	private final ExecutorService executor;
	
	/**
	 * 任务名称
	 */
	protected final JmainName jmainName;
	
	protected BaseThreadPoolQueueJob(JmainName jmainName) {
		this(jmainName, DEFAULT_MAX_EXECUTING);
	}
	
	protected BaseThreadPoolQueueJob(JmainName jmainName, int maxExecuting) {
		this.jmainName = jmainName;
		this.maxExecuting = maxExecuting;
		this.idleCount = new AtomicInteger(maxExecuting);
		this.executor = Executors.newFixedThreadPool(maxExecuting);
	}
	
	@Override
	public void initSleepTime() {
		SLEEP_TIME = TimeUnit.SECONDS.toMillis(DEFAULT_SLEEP_SECONDS);
	}

	/**
	 * 出队列
	 * @return	待处理对象
	 */
	protected abstract T popQueue() throws Exception;
	
	/**
	 * 是否结束处理，默认为队列Pop值为null
	 */
	protected boolean isEnd(T t){
		return t == null;
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
			
			synchronized (idleCount) {
				while (idleCount.get() <= 0){
					logger.warn("executor has no idle thread, idleCount:{}", idleCount.get());
					idleCount.wait();
				}
				
				try {
	                if (!markToHandleQueueData(t)){
	                    continue;
	                }
	            } catch (Exception e) {
	                logger.error("markToHandleQueueData exception:", e);
	                continue;
	            }
				
				idleCount.decrementAndGet();
			}
			
			final T c = t;
			executor.submit(new Runnable() {
				@Override
				public void run() {
					LogUtil.mark(jmainName.name());
					process(c);
				}
			});
			
		}
	}
	
	protected boolean markToHandleQueueData(T t) throws Exception {
	    return true;
	}
	
	/**
	 * 处理出队列的数据
	 */
	protected abstract void handleQueueData(T t) throws Exception;
	
	protected void process(T t){
		try {
			handleQueueData(t);
		} catch (Exception e) {
			logger.error("process exception, data:" + JSON.toJSONString(t), e);
		} finally {
			synchronized (idleCount) {
				idleCount.incrementAndGet();
				logger.info("process end, idleCount:{}", idleCount.get());
				idleCount.notifyAll();
			}
		}
	}
	
	/**
	 * 当次循环结束的后处理
	 */
	protected void postEnd() throws Exception {
	}
	
}
