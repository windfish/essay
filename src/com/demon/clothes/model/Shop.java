package com.demon.clothes.model;

/**
 * 店铺
 */
public class Shop {

	private int shopId;
	private String name;
	private String addr;
	private String phone;
	private String wechat;
	
	public int getShopId() {
		return shopId;
	}
	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	
	@Override
	public String toString() {
		return "Shop [shopId=" + shopId + ", name=" + name + ", addr=" + addr + ", phone=" + phone + ", wechat="
				+ wechat + "]";
	}
	
}
