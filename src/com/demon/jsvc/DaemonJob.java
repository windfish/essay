package com.demon.jsvc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author xuliang
 * @since 2018年9月19日 下午3:37:26
 *
 */
public abstract class DaemonJob implements Daemon {

	private static final Map<Class<?>, Object> RUNNING_MAP = new LinkedHashMap<>();
	
	public static final void register(Class<?> jobClass){
		RUNNING_MAP.put(jobClass, new Object());
	}
	
	public static final void removeRegister(Class<?> jobClass){
		RUNNING_MAP.remove(jobClass);
	}
	
	@Override
	public void init(DaemonContext context) throws DaemonInitException, Exception {
	}

	@Override
	public abstract void start() throws Exception;

	@Override
	public void stop() throws Exception {
		Logger logger = LoggerFactory.getLogger(DaemonJob.class);
		for (int i = 0; i < 60; i++){
			logger.info("i:{}, RUNNING_MAP:{}", i, JSON.toJSONString(RUNNING_MAP));
			if (RUNNING_MAP.isEmpty()){
				logger.info("All daemon job stopped...");
				break;
			}
			Thread.sleep(1000);
		}
	}

	@Override
	public void destroy() {
	}

}
