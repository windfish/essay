package com.demon.clothes.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易信息
 */
public class Deal {

	private int dealId;
	private Date time;
	private BigDecimal price;
	private BigDecimal expressCharge;
	private String expressNo;
	private int stock_id;
	private int buyer_id;
	private String status;
	
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
	public int getStock_id() {
		return stock_id;
	}
	public void setStock_id(int stock_id) {
		this.stock_id = stock_id;
	}
	public int getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(int buyer_id) {
		this.buyer_id = buyer_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Deal [dealId=" + dealId + ", time=" + time + ", price=" + price + ", expressCharge=" + expressCharge
				+ ", expressNo=" + expressNo + ", stock_id=" + stock_id + ", buyer_id=" + buyer_id + ", status="
				+ status + "]";
	}
	
}
