package com.demon.hadoop.rpc.server;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Builder;
import org.apache.hadoop.ipc.RPC.Server;

public class LoginServerInit {

	public static void main(String[] args) throws HadoopIllegalArgumentException, IOException {
		Configuration conf = new Configuration();
		Builder builder = new RPC.Builder(conf);
		builder.setBindAddress("localhost").setPort(10001)
			.setProtocol(LoginService.class).setInstance(new LoginServiceImpl());
		Server server = builder.build();
		server.start();
	}

}
