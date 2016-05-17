package com.demon.clothes.inter;

import java.util.List;
import java.util.Map;

import com.demon.clothes.dto.StockDealDto;
import com.demon.clothes.dto.StockDto;
import com.demon.clothes.model.Stock;

public interface IStockOperation {
	/**
	 * 新增库存商品
	 */
	public boolean addStock(Stock stock);
	/**
	 * 查询库存商品
	 */
	public List<StockDto> queryStocks(Stock stock);
	/**
	 * 查询库存商品交易信息
	 */
	public List<StockDealDto> queryStockDeals(Map<String, Object> map);
}
