package com.demon.hadoop.rpc.server;

public interface LoginService {

	/*服务端接口版本号*/
	public static final long versionID = 1L;
	
	public String login(String username, String password);
	
}
