package com.demon.clothes.inter;

import java.util.List;

import com.demon.clothes.model.Type;

public interface ITypeOperation {

	public boolean addType(Type type);
	public List<Type> queryTypes();
}
