package com.demon.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author xuliang
 * @since 2017年12月3日 下午2:43:49
 *
 */
public class JsonUtil {

	// serialize
	private static SerializeConfig serializeConfig = new SerializeConfig();
	static {
//			serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
	}
	
	private static SerializerFeature[] serializerFeature = new SerializerFeature[]{
//				SerializerFeature.WriteNullListAsEmpty,
//				SerializerFeature.WriteNullStringAsEmpty,
			SerializerFeature.WriteDateUseDateFormat,
//				SerializerFeature.PrettyFormat
	};
	
	public static String toJsonString(Object object){
		if (object == null) {
			return "{}";
		}
		return JSON.toJSONString(object, serializeConfig, serializerFeature);
	}
	
	private static SerializerFeature[] logSerializerFeature = new SerializerFeature[]{
			SerializerFeature.WriteDateUseDateFormat,
			SerializerFeature.UseSingleQuotes,
//				SerializerFeature.SortField
	};
	
	/**
	 * 仅格式化打印使用
	 */
	public static String toJsonLogString(Object object){
		return JSON.toJSONString(object, serializeConfig, logSerializerFeature);
	}
	
	private static SerializerFeature[] prettySerializerFeature = new SerializerFeature[]{
			SerializerFeature.WriteDateUseDateFormat,
			SerializerFeature.PrettyFormat
	};
	
	public static String toJsonPrettyString(Object object){
		return JSON.toJSONString(object, serializeConfig, prettySerializerFeature);
	}
	
	// deserialize
    private static Feature[] features = new Feature[]{
    		
    };
	
	public static <T> T parseObject(String input, Class<T> clazz){
		return JSON.parseObject(input, clazz, features);
	}
	
	public static <T> List<T> parseArray(String input, Class<T> clazz){
		return JSON.parseArray(input, clazz);
	}
	
	public static JSONObject parseObject(Object obj){
		return JSON.parseObject(toJsonString(obj));
	}
	
	public static <T> T parseCamelCaseObject(String input, Class<T> clazz){
	    JSONObject result = new JSONObject();
	    for (Map.Entry<String, Object> e :JSON.parseObject(input).entrySet()){
	        result.put(getCamelCaseName(e.getKey()), e.getValue());
	    }
	    return JSONObject.toJavaObject(result, clazz);
	}
	
	private static String getCamelCaseName(String sourceName){
        StringBuffer name = new StringBuffer();
        boolean nextUpper = false;
        for (char ch : sourceName.toCharArray()){
            if (ch == '_'){
                nextUpper = true;
                continue;
            }
            if (nextUpper){
                name.append(Character.toUpperCase(ch));
                nextUpper = false;
                continue;
            }
            name.append(ch);
            nextUpper = false;
        }
        return name.toString();
    }
		
}
