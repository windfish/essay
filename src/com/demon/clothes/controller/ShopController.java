package com.demon.clothes.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demon.clothes.inter.IShopOperation;
import com.demon.clothes.model.Shop;

@Controller
@RequestMapping("/shop")
public class ShopController {

	private static final Logger logger = Logger.getLogger(ShopController.class);
	
	@Autowired
	private IShopOperation shopOperation;
	
	@RequestMapping("/addShop")
	@ResponseBody
	public void addShop(Shop shop){
		logger.info(shop);
		
		shopOperation.addShop(shop);
	}
	
	@RequestMapping("/queryShops")
	@ResponseBody
	public List<Shop> queryShops(){
		return shopOperation.queryShops();
	}
}
