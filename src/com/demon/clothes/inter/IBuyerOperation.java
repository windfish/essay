package com.demon.clothes.inter;

import java.util.List;

import com.demon.clothes.model.Buyer;

public interface IBuyerOperation {

	public void addBuyer(Buyer buyer);
	
	public List<Buyer> queryBuyers();
}
