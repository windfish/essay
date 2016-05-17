package com.demon.clothes.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demon.clothes.inter.ITypeOperation;
import com.demon.clothes.model.Type;

@Controller
@RequestMapping("/type")
public class TypeController {

	private static final Logger logger = Logger.getLogger(TypeController.class);
	
	@Autowired
	private ITypeOperation typeOperation;
	
	@RequestMapping("/addType")
	@ResponseBody
	public String addType(Type type){
		logger.info("type param: "+type);
		
		boolean b = typeOperation.addType(type);
		logger.info("add type: "+b);
		if(b){
			return "ok";
		}else{
			return "fail";
		}
	}
	
	@RequestMapping("/queryTypes")
	@ResponseBody
	public List<Type> queryTypes(){
		return typeOperation.queryTypes();
	}
}
