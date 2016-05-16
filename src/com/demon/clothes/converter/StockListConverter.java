package com.demon.clothes.converter;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.demon.clothes.model.Stock;

@Component
public class StockListConverter implements Converter<String, List<Stock>> {

	@Override
	public List<Stock> convert(String source) {
		System.out.println(source);
		
		return null;
	}
	
}
