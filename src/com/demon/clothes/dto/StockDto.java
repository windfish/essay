package com.demon.clothes.dto;

import java.math.BigDecimal;

public class StockDto {

	private int stockId;
	private String name;
	private String color;
	private String size;
	private String type;
	private String type_name;
	private int number;
	private BigDecimal purchasePrice;
	private BigDecimal hopePrice;
	private BigDecimal discountPrice;
	private int shopId;
	private String shop_name;
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
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
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
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "StockDto [stockId=" + stockId + ", name=" + name + ", color="
				+ color + ", size=" + size + ", type=" + type + ", type_name="
				+ type_name + ", number=" + number + ", purchasePrice="
				+ purchasePrice + ", hopePrice=" + hopePrice
				+ ", discountPrice=" + discountPrice + ", shopId=" + shopId
				+ ", shop_name=" + shop_name + ", status=" + status + "]";
	}
	
}
