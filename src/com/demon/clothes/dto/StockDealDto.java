package com.demon.clothes.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StockDealDto implements Serializable {

	private static final long serialVersionUID = 204660200333009137L;
	
	private int dealId;
	private Date time;
	private BigDecimal price;
	private BigDecimal expressCharge;
	private String expressNo;
	private String status;
	
	private int stockId;
	private String name;
	private String color;
	private String size;
	private String type;
	private int number;
	private BigDecimal purchasePrice;
	
	public int getDealId() {
		return dealId;
	}
	public void setDealId(int dealId) {
		this.dealId = dealId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getExpressCharge() {
		return expressCharge;
	}
	public void setExpressCharge(BigDecimal expressCharge) {
		this.expressCharge = expressCharge;
	}
	public String getExpressNo() {
		return expressNo;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getStockId() {
		return stockId;
	}
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return "StockDealDto [dealId=" + dealId + ", time=" + time + ", price="
				+ price + ", expressCharge=" + expressCharge + ", expressNo="
				+ expressNo + ", status=" + status + ", stockId=" + stockId
				+ ", name=" + name + ", color=" + color + ", size=" + size
				+ ", type=" + type + ", number=" + number + ", purchasePrice="
				+ purchasePrice + "]";
	}
	
}
