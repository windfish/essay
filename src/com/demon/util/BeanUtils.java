package com.demon.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeanUtils {

	/**
	 * 复制源对象的属性到目标对象，且返回目标对象。
	 * 复制仅限于共同名称共同类型的属性。
	 * @param source	源对象
	 * @param target	目标对象的class，必须带有空构造方法
	 * @return
	 */
	public static final <T> T copy(Object source, Class<T> clazz){
		if (source == null){
			return null;
		}
		return JsonUtil.parseObject(JsonUtil.toJsonString(source), clazz);
	}
	
	public static final <E, T> List<T> copyList(Collection<E> sourceList, Class<T> clazz){
		if (sourceList == null){
			return null;
		}
		List<T> destList = new ArrayList<>();
		for (E e : sourceList){
			destList.add(copy(e, clazz));
		}
		return destList;
	}
	
}
