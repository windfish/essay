package com.demon.clothes.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demon.clothes.dto.StockDto;
import com.demon.clothes.inter.IStockOperation;
import com.demon.clothes.model.Stock;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/stock")
public class StockController {
	
	private static final Logger log = Logger.getLogger(StockController.class);
	
	@Autowired
	private IStockOperation stockOperation;
	
	@RequestMapping("/addStock")
	@ResponseBody
	public void addStock(@RequestParam String stocks) throws JsonParseException, JsonMappingException, IOException{
		log.info("request data: "+stocks);
		
		//Gson gson = new Gson();
		//List<Stock> list = gson.fromJson(stocks, new TypeToken<List<Stock>>(){}.getType());
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Stock.class);
		List<Stock> list = mapper.readValue(stocks, javaType);
		
		for(int i=0;i<list.size();i++){
			stockOperation.addStock(list.get(i));
		}
	}
	
	@RequestMapping("/queryStocks")
	@ResponseBody
	public List<StockDto> queryStocks(String name){
		Stock stock = new Stock();
		stock.setName(name);
		List<StockDto> list = stockOperation.queryStocks(stock);
		
		return list;
	}
	
}
