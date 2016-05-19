package com.demon.clothes.inter;

import java.util.List;

import com.demon.clothes.model.Shop;

public interface IShopOperation {

	public boolean addShop(Shop shop);
	public List<Shop> queryShops();
}
