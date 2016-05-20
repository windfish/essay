package com.demon.clothes.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.demon.clothes.inter.IBuyerOperation;
import com.demon.clothes.inter.IDealOperation;
import com.demon.clothes.inter.IStockOperation;
import com.demon.clothes.model.Buyer;
import com.demon.clothes.model.Deal;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DealService {

	private static final Logger log = Logger.getLogger(DealService.class);
	
	@Autowired
	private IDealOperation dealOperation;
	@Autowired
	private IBuyerOperation buyerOperation;
	@Autowired
	private IStockOperation stockOperation;
	
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void addDeal(String data) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, String> map = mapper.readValue(data, Map.class);
		log.info(map);
		
		Deal deal = new Deal();
		deal.setTime(new Date());
		deal.setStock_id(Integer.parseInt(map.get("stockId")));
		deal.setPrice(BigDecimal.valueOf(Double.parseDouble(map.get("price"))));
		deal.setExpressCharge(BigDecimal.valueOf(Double.parseDouble(map.get("expressCharge"))));
		deal.setExpressNo(map.get("expressNo"));
		deal.setStatus(map.get("status"));
		
		if(map.get("buyerId")==null || "".equals(map.get("buyerId"))){
			//需要新建买方信息
			Buyer buyer = new Buyer();
			buyer.setName(map.get("name"));
			buyer.setAddr(map.get("addr"));
			buyer.setPhone(map.get("phone"));
			buyerOperation.addBuyer(buyer);
			deal.setBuyer_id(buyer.getBuyerId());
		}else{
			//不需要新建买方信息
			deal.setBuyer_id(Integer.parseInt(map.get("buyerId")));
		}
		
		dealOperation.addDeal(deal);
		
		stockOperation.sellStock(deal.getStock_id());
		
	}
}
