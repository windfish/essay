package com.demon.clothes.inter;

import java.util.List;

import com.demon.clothes.model.Shop;
import com.demon.clothes.model.Type;

public interface IShopOperation {

	public boolean addShop(Shop shop);
	public List<Shop> queryShops();
}
