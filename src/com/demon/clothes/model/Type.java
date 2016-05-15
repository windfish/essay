package com.demon.clothes.model;

/**
 * 衣服类别
 */
public class Type {

	private int typeId;
	private String name;
	
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Type [typeId=" + typeId + ", name=" + name + "]";
	}
	
}
