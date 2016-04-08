package com.demon.distributed.curator.extensions;

import java.io.Closeable;
import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * 相当于分布式环境中的服务应用，服务应用启动时调用start()，关闭时调用close()
 */
public class ExampleServer implements Closeable {
	private final ServiceDiscovery<InstanceDetails> serviceDiscovery;
	private final ServiceInstance<InstanceDetails> thisInstance;
	
	public ExampleServer(CuratorFramework client, String path, String serviceName, String description) throws Exception {
		UriSpec uriSpec = new UriSpec("{scheme}://foo.com:{port}");
		thisInstance = ServiceInstance.<InstanceDetails> builder().name(serviceName).payload(new InstanceDetails(description))
				.port((int) (65535 * Math.random()))
				.uriSpec(uriSpec).build();
		JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class).client(client).basePath(path).serializer(serializer)
                .thisInstance(thisInstance).build();
	}

	@Override
	public void close() throws IOException {
		
	}
	
}