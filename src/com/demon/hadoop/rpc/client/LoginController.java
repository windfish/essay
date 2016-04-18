package com.demon.hadoop.rpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import com.demon.hadoop.rpc.server.LoginService;

public class LoginController {

	public static void main(String[] args) throws IOException {
		LoginService service = RPC.getProxy(LoginService.class, 1L, 
				new InetSocketAddress("localhost", 10001), new Configuration());
		System.out.println(service.login("aaa", "111111"));
	}
	
}
