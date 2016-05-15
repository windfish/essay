package com.demon.clothes.model;

/**
 * 买方信息
 */
public class Buyer {

	private int buyerId;
	private String name;
	private String addr;
	private String phone;
	private String wechat;
	
	public int getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
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
		return "Buyer [buyerId=" + buyerId + ", name=" + name + ", addr=" + addr + ", phone=" + phone + ", wechat="
				+ wechat + "]";
	}
	
}
