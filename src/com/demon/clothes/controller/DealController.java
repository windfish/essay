package com.demon.clothes.controller;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demon.clothes.inter.IBuyerOperation;
import com.demon.clothes.model.Buyer;
import com.demon.clothes.service.DealService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@RequestMapping("/deal")
public class DealController {

	private static final Logger log = Logger.getLogger(DealController.class);
	
	@Autowired
	private DealService dealService;
	@Autowired
	private IBuyerOperation buyerOperation;
	
	@RequestMapping("/addDeal")
	@ResponseBody
	public void addDeal(String data) throws JsonParseException, JsonMappingException, IOException{
		log.info(data);
		
		dealService.addDeal(data);
		
	}
	
	@RequestMapping("/queryBuyers")
	@ResponseBody
	public List<Buyer> queryBuyers(){
		return buyerOperation.queryBuyers();
	}
	
}
