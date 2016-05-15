package com.demon.clothes.model;

import java.math.BigDecimal;

/**
 * 库存信息
 */
public class Stock {

	private int stockId;
	private String name;
	private String color;
	private String size;
	private String type;
	private int number;
	private BigDecimal purchasePrice;
	private BigDecimal hopePrice;
	private BigDecimal discountPrice;
	private int shopId;
	private String status;
	
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
	public BigDecimal getHopePrice() {
		return hopePrice;
	}
	public void setHopePrice(BigDecimal hopePrice) {
		this.hopePrice = hopePrice;
	}
	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(BigDecimal discountPrice) {
		this.discountPrice = discountPrice;
	}
	public int getShopId() {
		return shopId;
	}
	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Stock [stockId=" + stockId + ", name=" + name + ", color=" + color + ", size=" + size + ", type=" + type
				+ ", number=" + number + ", purchasePrice=" + purchasePrice + ", hopePrice=" + hopePrice
				+ ", discountPrice=" + discountPrice + ", shopId=" + shopId + ", status=" + status + "]";
	}
	
}
