package com.demon.distributed.curator.Extensions;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * 服务类，定义了服务实例的基本信息
 */
@JsonRootName("details")
public class InstanceDetails {
	private String description;
	
	public InstanceDetails() {
		this("");
	}

	public InstanceDetails(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
