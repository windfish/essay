package com.demon.clothes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demon.clothes.inter.IStockOperation;
import com.demon.clothes.model.Stock;

@Controller
@RequestMapping("/stock")
public class StockController {
	@Autowired
	private IStockOperation stockOperation;
	
	@RequestMapping("/addStock")
	@ResponseBody
	public String addStock(@RequestParam List<Stock> stocks){
		System.out.println(stocks.size());
		
		return "123";
	}
}
